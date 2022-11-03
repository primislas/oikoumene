package com.lomicron.vicky.parsers

import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.oikoumene.model.Color
import com.lomicron.oikoumene.model.map.RouteTypes
import com.lomicron.oikoumene.model.trade.TradeNode
import com.lomicron.oikoumene.parsers.ClausewitzParser.{Fields, parseFile, parseFileFieldsAsEntities, parseHistory}
import com.lomicron.oikoumene.parsers.ConfigField
import com.lomicron.oikoumene.repository.api.resources.LocalisationRepository
import com.lomicron.utils.collection.CollectionUtils._
import com.lomicron.utils.json.JsonMapper.{JsonNodeEx, ObjectNodeEx, toObjectNode}
import com.lomicron.vicky.model.province.{Province, ProvinceGeography}
import com.lomicron.vicky.repository.api.{GeographicRepository, MapRepository, ProvinceRepository, RepositoryFactory}
import com.softwaremill.quicklens._
import com.typesafe.scalalogging.LazyLogging

import scala.collection.immutable.ListMap
import scala.collection.parallel.CollectionConverters.seqIsParallelizable
import scala.util.matching.Regex

object ProvinceParser extends LazyLogging {

  val provinceDefinitionPat: Regex =
    "^(?<id>\\d+);(?<red>\\d+);(?<green>\\d+);(?<blue>\\d+);(?<comment>[^;]*)(?:;(?<tag>.*))?".r
  val addBuildingField = "add_building"
  val removeBuildingField = "remove_building"

  case class Prov(id: Int, conf: ObjectNode) {
    self =>
    def update(f: ObjectNode => ObjectNode): Prov = {
      f(conf)
      self
    }
  }

  def apply(repos: RepositoryFactory, evalEntityFields: Boolean): ProvinceRepository = {

    val files = repos.resources
    val definitions = files.getProvinceDefinitions

    val provinceHistory = repos.resources
      .getProvinceHistoryResources
      .flatMapValues(parseFile)
      .mapValuesEx(parseHistory)

    val popHistoryResources = repos.resources.getPopHistory
    val popHistoriesById = parseFileFieldsAsEntities(popHistoryResources)
      .flatMap(ph => ph.getInt(Fields.idKey).map(_ -> ph))
      .to(ListMap)
      .mapValuesEx(on =>
        on.entries()
          .flatMap { case (popTypeId, popConf) => popConf.asObject.map(popTypeId -> _) }
          .map { case (popTypeId, popConf) => popConf.setEx("pop_type", popTypeId) }
      )

    val provinces = repos.provinces

    if (evalEntityFields) {
      val popHistories = popHistoriesById.values.toSeq.flatten
      ConfigField.printCaseClass("PopHistory", popHistories)

      val histories = provinceHistory.values.toSeq
      val inits = histories.flatMap(_.getObject(Fields.init))
      val events = histories.flatMap(_.getSeq(Fields.events)).flatMap(_.asObject)
      val updates = inits ++ events
      ConfigField.printCaseClass("ProvinceUpdate", updates)

      val partyLoyalties = updates.flatMap(_.getObject("party_loyalty"))
      ConfigField.printCaseClass("PartyLoyalty", partyLoyalties)

      val stateBuildings = updates.flatMap(_.getSeq("state_building")).flatMap(_.asObject)
      ConfigField.printCaseClass("StateBuilding", stateBuildings)
    }

    parseProvinceDefinitions(definitions)
      .par
      .flatMap(p => toObjectNode(p).map(o => Prov(p.id, o)))
      .map(addLocalisation(_, repos.localisations))
      .map(addHistory(_, provinceHistory, popHistoriesById))
      .map(p => Province.fromJson(p.conf))
      .map(_.atStart)
      .map(addGeography(_, repos.geography))
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
    histories: Map[Int, ObjectNode],
    popHistories: Map[Int, Seq[ObjectNode]],
  ): Prov =
    histories
      .get(p.id)
      .map(f => p.update(conf => addHistory(conf, f, popHistories.getOrElse(p.id, Seq.empty))))
      .getOrElse(p)

  private def addHistory
  (province: ObjectNode,
   history: ObjectNode,
   pops: Seq[ObjectNode] = Seq.empty
  ): ObjectNode =
    province
      .setEx(Fields.history, history)
      .setEx(Fields.pops, pops)

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

    // TODO: region
//    val region = area.flatMap(geography.regions.regionOfArea).map(_.id)
    val continent = geography.continent.continentOfProvince(id).map(_.id)

    val positions = geography.map.positionsOf(province.id)

    val provinceGeography = ProvinceGeography(pType, terrain, climate, None, None, None, continent)
      .copy(positions = positions)

    province.copy(geography = provinceGeography)
  }

  def terrainMapTypeOf(p: Province, m: MapRepository): Option[String] =
    m.terrainMapType(p.color)

  def addTrade(p: Province, tradeNodes: Map[Int, TradeNode]): Province =
    tradeNodes
      .get(p.id)
      .map(tn => p.withTradeNode(tn.id))
      .getOrElse(p)

}
