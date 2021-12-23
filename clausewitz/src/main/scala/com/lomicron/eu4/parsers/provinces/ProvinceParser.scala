package com.lomicron.eu4.parsers.provinces

import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.eu4.model.map.RouteTypes
import com.lomicron.eu4.model.provinces.{Province, ProvinceGeography}
import com.lomicron.eu4.model.trade.TradeNode
import com.lomicron.oikoumene.parsers.ClausewitzParser.{Fields, parse, parseHistory}
import com.lomicron.eu4.repository.api.RepositoryFactory
import com.lomicron.eu4.repository.api.map.{BuildingRepository, GeographicRepository, MapRepository, ProvinceRepository}
import com.lomicron.eu4.repository.api.resources.{LocalisationRepository, ResourceRepository}
import com.lomicron.oikoumene.model.Color
import com.lomicron.oikoumene.repository.api.resources.GameFile
import com.lomicron.utils.collection.CollectionUtils._
import com.lomicron.utils.json.JsonMapper
import com.lomicron.utils.json.JsonMapper.{ArrayNodeEx, JsonNodeEx, ObjectNodeEx, booleanYes, toObjectNode}
import com.softwaremill.quicklens._
import com.typesafe.scalalogging.LazyLogging

import scala.collection.parallel.CollectionConverters.seqIsParallelizable
import scala.util.matching.Regex

object ProvinceParser extends LazyLogging {

  val provinceDefinitionPat: Regex =
    "^(?<id>\\d+);(?<red>\\d+);(?<green>\\d+);(?<blue>\\d+);(?<comment>[^;]*)(?:;(?<tag>.*))?".r
  val addBuildingField = "add_building"
  val removeBuildingField = "remove_building"

  case class Prov(id: Int, conf: ObjectNode) { self =>
    def update(f: ObjectNode => ObjectNode): Prov = {
      f(conf)
      self
    }
  }

  def apply (repos: RepositoryFactory): ProvinceRepository = {

    val files = repos.resources
    val definitions = files.getProvinceDefinitions

    val provinceHistory = repos.resources.getProvinceHistoryResources
    val tradeNodeByProvId = repos.tradeNodes.findAll.flatMap(tn => tn.members.map(_ -> tn)).toMap
    val provinces = repos.provinces

    parseProvinceDefinitions(definitions)
      .par
      .flatMap(p => toObjectNode(p).map(o => Prov(p.id, o)))
      .map(addLocalisation(_, repos.localisations))
      .map(addHistory(_, provinceHistory, repos.resources, repos.buildings))
      .map(p => Province.fromJson(p.conf))
      .map(_.atStart)
      .map(addGeography(_, repos.geography))
      .map(addPolitics(_, repos))
      .map(addTrade(_, tradeNodeByProvId))
      .to(Seq)
      .foreach(provinces.create)

    recalculateRoutes(provinces, repos.geography)

    provinces
  }

  def parseProvinceDefinitions(definitions: Option[String]): Seq[Province] =
    definitions
      .map(_.linesIterator.toSeq)
      .getOrElse(Seq.empty)
      .flatMap(parseDefinition)

  def parseDefinition(line: String): Option[Province] =
    line match {
      case provinceDefinitionPat(id, r, g, b, comment) =>
        Some(Province(id.toInt, Color(r.toInt, g.toInt, b.toInt), comment))
      case provinceDefinitionPat(id, r, g, b, comment, tag2) =>
        Some(Province(id.toInt, Color(r.toInt, g.toInt, b.toInt), comment, tag2))
      case _ => None
    }

  def addLocalisation(p: Prov, localisation: LocalisationRepository): Prov =
    localisation
      .fetchProvince(p.id)
      .map(loc => p.update(_.setEx(Fields.localisation, loc)))
      .getOrElse(p)

  def addHistory
  (
    p: Prov,
    histories: Map[Int, GameFile],
    resources: ResourceRepository,
    buildings: BuildingRepository
  ): Prov =
    histories
      .get(p.id)
      .flatMap(resources.getResource)
      .map(f => p.update(conf => addHistory(conf, f, buildings)))
      .getOrElse(p)

  private def addHistory
  (province: ObjectNode,
   history: Option[GameFile],
   buildings: BuildingRepository
  ): ObjectNode =
    history
      .flatMap(_.content)
      .map(parse)
      .map(histAndErrors => {
        val errors = histAndErrors._2
        if (errors.nonEmpty)
          logger.warn(s"Encountered errors parsing country history for province '${province.get("id")}': $errors")
        histAndErrors._1
      })
      .map(parseHistory)
      .map(cleanUpHistory)
      .map(setHistBuildings(_, buildings))
      .map(setHistSourceFile(_, history))
      .map(province.setEx(Fields.history, _))
      .getOrElse(province)

  def cleanUpHistory(update: ObjectNode): ObjectNode = {
    // TODO: a plug for a failing mod, instead make a universal mechanism
    //  to cleanup known arrays (empty obj to empty array, array to last val for non-array fields
    update.getObject(Fields.init).foreach(init => {
      init.getArray("is_city").map(_.toSeq).foreach(a => {
        if (a.nonEmpty) init.setEx("is_city", a.last)
        else init.remove("is_city")
      })
    })
    update
  }

  def setHistBuildings(h: ObjectNode, buildings: BuildingRepository): ObjectNode = {
    h.getObject(Fields.init).foreach(setBuildings(_, buildings))
    h.getArray(Fields.events)
      .map(es => es.toSeq).getOrElse(Seq.empty).flatMap(_.asObject)
      .foreach(setBuildings(_, buildings))
    h
  }

  def setHistSourceFile(provHist: ObjectNode, history: Option[GameFile]): ObjectNode =
    history
      .map(_.name)
      .map(provHist.setEx(Fields.sourceFile, _))
      .getOrElse(provHist)

  private def setBuildings(event: ObjectNode, buildings: BuildingRepository) = {
    val buildingFields = event.fieldNames.toSeq.filter(buildings.find(_).nonEmpty).toList
    buildingFields
      .groupBy(event.get(_) == booleanYes)
      .foreachKV { case (k, v) =>
        val buildings = JsonMapper.arrayNodeOf(v)
        val buildingField = if (k) addBuildingField else removeBuildingField
        event.setEx(buildingField, buildings)
      }
    buildingFields.foreach(event.remove)

    event
  }

  private val landlockedRoutes = Set(RouteTypes.LAND, RouteTypes.IMPASSABLE)
  def recalculateRoutes
  (provinces: ProvinceRepository, geography: GeographicRepository)
  : ProvinceRepository = {
    geography.map.buildRoutes(provinces)
    val landlocked = geography.map
      .routes
      .filterValues(_.nonEmpty)
      .filterValues(_.forall(r => landlockedRoutes.contains(r.`type`)))
      .keys.toList
    provinces
      .find(landlocked)
      .map(p => p.modify(_.geography.landlocked).setTo(true))
      .foreach(provinces.update)
    provinces
  }

  def addGeography
  (province: Province, geography: GeographicRepository)
  : Province = {
    val id = province.id

    val pType = geography.provinceTypes.map(_.identifyType(id))
    val mapTerrain = terrainMapTypeOf(province, geography.map)
    val terrainOverride = geography.terrain.ofProvince(id)
    val terrain = terrainOverride.orElse(mapTerrain)
    val climate = geography.climate.ofProvince(id)

    val area = geography.areas.areaOfProvince(id).map(_.id)
    val region = area.flatMap(geography.regions.regionOfArea).map(_.id)
    val superRegion = region.flatMap(geography.superregions.superRegionOfRegion).map(_.id)
    val continent = geography.continent.continentOfProvince(id).map(_.id)

    val positions = geography.map.positionsOf(province.id)

    val provinceGeography = ProvinceGeography(pType, terrain, climate, area, region, superRegion, continent)
        .copy(positions = positions)

    province.copy(geography = provinceGeography)
  }

  def terrainMapTypeOf(p: Province, m: MapRepository): Option[String] =
    m.terrainMapType(p.color)

  def addPolitics
  (provinceRepo: ProvinceRepository, repos: RepositoryFactory)
  : ProvinceRepository = {
    provinceRepo.findAll.map(addPolitics(_, repos)).foreach(provinceRepo.update)
    provinceRepo
  }

  def addPolitics
  (p: Province, repos: RepositoryFactory)
  : Province = {
    val cultureGroup = p.state.culture.flatMap(repos.cultures.groupOf).map(_.id)
    val religionGroup = p.state.religion.flatMap(repos.religions.groupOf).map(_.id)
    val state = p.state.copy(cultureGroup = cultureGroup, religionGroup = religionGroup)
    p.withState(state)
  }

  def addTrade(p: Province, tradeNodes: Map[Int, TradeNode]): Province =
    tradeNodes
      .get(p.id)
      .map(tn => p.withTradeNode(tn.id))
      .getOrElse(p)

}
