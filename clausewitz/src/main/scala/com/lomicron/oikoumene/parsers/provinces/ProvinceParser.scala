package com.lomicron.oikoumene.parsers.provinces

import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.oikoumene.model.Color
import com.lomicron.oikoumene.model.provinces.{Province, ProvinceTypes}
import com.lomicron.oikoumene.parsers.ClausewitzParser
import com.lomicron.oikoumene.parsers.ClausewitzParser.{parse, parseEvents, startDate}
import com.lomicron.oikoumene.repository.api.map.{BuildingRepository, ProvinceRepository}
import com.lomicron.oikoumene.repository.api.{LocalisationRepository, RepositoryFactory, ResourceRepository}
import com.lomicron.utils.collection.CollectionUtils._
import com.lomicron.utils.json.JsonMapper
import com.lomicron.utils.json.JsonMapper.{ObjectNodeEx, booleanYes, textNode, toObjectNode}
import com.typesafe.scalalogging.LazyLogging

import scala.util.matching.Regex

object ProvinceParser extends LazyLogging {

  val provinceDefinitionPat: Regex =
    "^(?<id>\\d+);(?<red>\\d+);(?<green>\\d+);(?<blue>\\d+);(?<comment>[^;]*)(?:;(?<tag>.*)){0,1}".r
  val addBuildingField = "add_building"
  val removeBuildingField = "remove_building"

  def apply(repos: RepositoryFactory): ProvinceRepository =
    apply(repos.resources, repos.localisations, repos.provinces, repos.buildings)

  def apply
  (files: ResourceRepository,
   localisation: LocalisationRepository,
   provinces: ProvinceRepository,
   buildings: BuildingRepository): ProvinceRepository = {

    val definitions = files.getProvinceDefinitions
    val provinceTypes = files.getProvinceTypes // default.map
    val provincePositions = files.getProvincePositions
    val history = files.getProvinceHistory

    apply(
      definitions,
      provinceTypes,
      provincePositions,
      history,
      localisation,
      buildings,
      provinces)
  }

  def apply
  (definitions: Option[String],
   provinceTypes: Option[String],
   provincePositions: Option[String],
   provinceHistory: Map[Int, String],
   localisation: LocalisationRepository,
   buildings: BuildingRepository,
   provinces: ProvinceRepository)
  : ProvinceRepository = {

    val provinceById = parseProvinces(definitions)
    val withLocalisation = addLocalisation(provinceById, localisation)
    val withType = addProvinceType(withLocalisation, provinceTypes)
    val withHistory = addHistory(withType, provinceHistory, buildings)

    logger.info(s"Parsed ${withHistory.size} province definitions")
    withHistory.values
      .map(Province.fromJson)
      .map(_.atStart())
      .foreach(provinces.create)

    provinces
  }

  def parseProvinces(definitions: Option[String]): Map[Int, ObjectNode] =
    definitions
      .map(_.lines)
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
      case _ =>
        logger.warn(s"'$line' doesn't match province definitions")
        None
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

  def addProvinceType
  (provinceById: Map[Int, ObjectNode],
   provinceTypes: Option[String]
  ): Map[Int, ObjectNode] = {

    val types = provinceTypes
      .map(ClausewitzParser.parse)
      .map(o => {
        if (o._2.nonEmpty) logger.warn(s"Encountered ${o._2.size} errors while parsing province types: ${o._2}")
        JsonMapper.convert[ProvinceTypes](o._1)
      })
      .get

    provinceById
      .foreach { case (id, prov) => prov.put("type", types.identifyType(id)) }

    provinceById
  }

  def addHistory
  (provincesById: Map[Int, ObjectNode],
   histories: Map[Int, String],
   buildings: BuildingRepository
  ): Map[Int, ObjectNode] = {

    provincesById
      .mapKVtoValue((id, prov) => addHistory(prov, histories.get(id), buildings))
  }

  private def addHistory
  (province: ObjectNode,
   history: Option[String],
   buildings: BuildingRepository
  ): ObjectNode =
    history
      .map(parse)
      .map(histAndErrors => {
        val errors = histAndErrors._2
        if (errors.nonEmpty)
          logger.warn(s"Encountered errors parsing country history for province '${province.get("id")}': $errors")
        histAndErrors._1
      })
      .map(history => {
        val events = Seq(history) ++ parseEvents(history).map(setBuildings(_, buildings))
        JsonMapper.arrayNodeOf(events)
      })
      .map(province.setEx("history", _))
      .getOrElse(province)

  private def setBuildings(event: ObjectNode, buildings: BuildingRepository) = {
    val buildingFields = event.fieldNames.toSeq.filter(buildings.find(_).isSuccess).toList
    buildingFields.foreach(f => {
      val buildingField = if (event.get(f) == booleanYes) addBuildingField else removeBuildingField
      event.set(buildingField, textNode(f))
      event.remove(f)
    })
    event
  }

}
