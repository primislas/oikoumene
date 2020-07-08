package com.lomicron.oikoumene.parsers.provinces

import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.oikoumene.io.FileNameAndContent
import com.lomicron.oikoumene.model.Color
import com.lomicron.oikoumene.model.provinces.{Province, ProvinceGeography}
import com.lomicron.oikoumene.parsers.ClausewitzParser.{Fields, parse, parseHistory}
import com.lomicron.oikoumene.repository.api.map.{BuildingRepository, GeographicRepository, MapRepository, ProvinceRepository}
import com.lomicron.oikoumene.repository.api.{LocalisationRepository, RepositoryFactory}
import com.lomicron.utils.collection.CollectionUtils._
import com.lomicron.utils.json.JsonMapper
import com.lomicron.utils.json.JsonMapper.{ArrayNodeEx, JsonNodeEx, ObjectNodeEx, booleanYes, toObjectNode}
import com.typesafe.scalalogging.LazyLogging

import scala.util.matching.Regex

object ProvinceParser extends LazyLogging {

  val provinceDefinitionPat: Regex =
    "^(?<id>\\d+);(?<red>\\d+);(?<green>\\d+);(?<blue>\\d+);(?<comment>[^;]*)(?:;(?<tag>.*))?".r
  val addBuildingField = "add_building"
  val removeBuildingField = "remove_building"

  def apply
  (repos: RepositoryFactory)
  : ProvinceRepository = {
    val files = repos.resources
    val definitions = files.getProvinceDefinitions
    val history = files.getProvinceHistory

    val withHistory = parseProvinces(definitions, history, repos.localisations, repos.buildings, repos.provinces)
    val withGeography = addGeography(withHistory, repos.geography)
    val withPolitics = addPolitics(withGeography, repos)
    val withTrade = addTrade(withPolitics, repos)

    repos.geography.map.buildRoutes(repos.provinces)

    withTrade
  }

  def parseProvinces
  (definitions: Option[String],
   provinceHistory: Map[Int, FileNameAndContent],
   localisation: LocalisationRepository,
   buildings: BuildingRepository,
   provinces: ProvinceRepository)
  : ProvinceRepository = {

    val provinceById = parseProvinceDefinitions(definitions)
    val withLocalisation = addLocalisation(provinceById, localisation)
    val withHistory = addHistory(withLocalisation, provinceHistory, buildings)

    withHistory.values
      .map(Province.fromJson)
      .map(_.atStart)
      .foreach(provinces.create)

    provinces
  }

  def parseProvinceDefinitions(definitions: Option[String]): Map[Int, ObjectNode] =
    definitions
      .map(_.lines.toSeq)
      .getOrElse(Seq.empty)
      .flatMap(parseDefinition)
      .map(p => p.id -> p)
      .toMap
      .flatMapValues(toObjectNode)

  def parseDefinition(line: String): Option[Province] =
    line match {
      case provinceDefinitionPat(id, r, g, b, comment) =>
        Some(Province(id.toInt, Color(r.toInt, g.toInt, b.toInt), comment))
      case provinceDefinitionPat(id, r, g, b, comment, tag2) =>
        Some(Province(id.toInt, Color(r.toInt, g.toInt, b.toInt), comment, tag2))
      case _ => None
    }

  def addLocalisation
  (provinceById: Map[Int, ObjectNode],
   localisation: LocalisationRepository): Map[Int, ObjectNode] = {
    val localById = localisation.fetchProvinces
    provinceById
      .foreachKV((id, prov) => localById
        .get(id)
        .foreach(loc => prov.set("localisation", loc)))
  }

  def addHistory
  (provincesById: Map[Int, ObjectNode],
   histories: Map[Int, FileNameAndContent],
   buildings: BuildingRepository
  ): Map[Int, ObjectNode] = {

    provincesById
      .mapKVtoValue((id, prov) => addHistory(prov, histories.get(id), buildings))
  }

  private def addHistory
  (province: ObjectNode,
   history: Option[FileNameAndContent],
   buildings: BuildingRepository
  ): ObjectNode =
    history
      .map(_.content)
      .map(parse)
      .map(histAndErrors => {
        val errors = histAndErrors._2
        if (errors.nonEmpty)
          logger.warn(s"Encountered errors parsing country history for province '${province.get("id")}': $errors")
        histAndErrors._1
      })
      .map(parseHistory)
      .map(setHistBuildings(_, buildings))
      .map(setHistSourceFile(_, history))
      .map(province.setEx(Fields.history, _))
      .getOrElse(province)

  def setHistBuildings(h: ObjectNode, buildings: BuildingRepository): ObjectNode = {
    h.getObject(Fields.init).foreach(setBuildings(_, buildings))
    h.getArray(Fields.events)
      .map(es => es.toSeq).getOrElse(Seq.empty).flatMap(_.asObject)
      .foreach(setBuildings(_, buildings))
    h
  }

  def setHistSourceFile(provHist: ObjectNode, history: Option[FileNameAndContent]): ObjectNode =
    history
      .map(_.name)
      .map(provHist.setEx(Fields.sourceFile, _))
      .getOrElse(provHist)

  private def setBuildings(event: ObjectNode, buildings: BuildingRepository) = {
    val buildingFields = event.fieldNames.toSeq.filter(buildings.find(_).isSuccess).toList
    buildingFields
      .groupBy(event.get(_) == booleanYes)
      .foreachKV { case (k, v) =>
        val buildings = JsonMapper.arrayNodeOf(v)
        val buildingField = if (k) addBuildingField else removeBuildingField
        event.set(buildingField, buildings)
      }
    buildingFields.foreach(event.remove)

    event
  }

  def addGeography
  (provinceRepo: ProvinceRepository, geography: GeographicRepository)
  : ProvinceRepository = {
    provinceRepo.findAll.map(addGeography(_, geography)).foreach(provinceRepo.update)
    provinceRepo
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

  def addTrade(provinces: ProvinceRepository, factory: RepositoryFactory): ProvinceRepository = {
    factory.tradeNodes.findAll
      .flatMap(tn => tn.members.flatMap(provId => provinces.find(provId).toOption.map(_.withTradeNode(tn.id))))
      .foreach(provinces.update)
    provinces
  }

}
