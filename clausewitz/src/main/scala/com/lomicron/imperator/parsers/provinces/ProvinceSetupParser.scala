package com.lomicron.imperator.parsers.provinces

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.{ArrayNode, ObjectNode}
import com.lomicron.imperator.model.provinces.Province
import com.lomicron.imperator.repository.api.{ProvinceRepository, RepositoryFactory}
import com.lomicron.oikoumene.parsers.ClausewitzParser.Fields
import com.lomicron.oikoumene.parsers.ConfigField
import com.lomicron.oikoumene.parsers.map.MapConfigParser.{Prov, parseProvinceDefinitions}
import com.lomicron.oikoumene.repository.api.resources.LocalisationRepository
import com.lomicron.utils.json.JsonMapper
import com.lomicron.utils.json.JsonMapper.{ArrayNodeEx, JsonNodeEx, ObjectNodeEx, toObjectNode}
import com.typesafe.scalalogging.LazyLogging

import scala.collection.parallel.CollectionConverters._

object ProvinceSetupParser extends LazyLogging {

  def apply(repos: RepositoryFactory): ProvinceRepository = {
    val popTypes = PopTypeParser(repos)
    val popTypeIds = popTypes.findAll.map(_.id)
    val buildings = BuildingParser(repos)
    val buildingIds = buildings.findAll.map(_.id)

    val definitions = repos.resources.getProvinceDefinitions
    val es = parseProvinceDefinitions(definitions)
      .par
      .flatMap(p => toObjectNode(p).map(o => Prov(p.id, o)))
      .map(addLocalisation(_, repos.localisation))
      .map(_.conf)
      .map(setPops(_, popTypeIds))
      .map(setBuildings(_, buildingIds))
      .toList
    ConfigField.printCaseClass("Province", es)

    val repo = repos.provinces
    es.map(Province.fromJson).foreach(repo.create)

    repos.provinces
  }

  def addLocalisation(p: Prov, localisation: LocalisationRepository): Prov =
    localisation
      .fetchProvince(p.id)
      .map(loc => p.update(_.setEx(Fields.localisation, loc)))
      .getOrElse(p)

  def setBuildings(province: ObjectNode, buildingIds: Seq[String]): ObjectNode = {
    val buildings = buildingIds
      .filter(province.has)
      .map(pt => pt -> province.remove(pt))
      .foldLeft(JsonMapper.objectNode)((acc, t) => acc.setEx(t._1, t._2))
    province.setEx("buildings", buildings)
  }

  def parseBuildings(province: ObjectNode): ObjectNode = {
    val buildings = province
      .fieldSeq()
      .filter(_.endsWith("_building"))
      .map(pt => pt -> province.remove(pt))
      .foldLeft(JsonMapper.objectNode)((acc, t) => acc.setEx(t._1, t._2))
    if (buildings.isEmpty())
      province
    else
      province.setEx("buildings", buildings)
  }

  def setPops(province: ObjectNode, popTypes: Seq[String]): ObjectNode = {
    def parsePops(pt: String): Seq[ObjectNode] =
      Option(province.get(pt))
        .map({
          case a: ArrayNode => a: JsonNode
          case j: JsonNode => JsonMapper.arrayNodeOf(j)
        })
        .flatMap(_.asArray)
        .toSeq
        .flatMap(_.toSeq)
        .flatMap(_.asObject)
        .map(_.setEx("type", pt))
        .map(pop => {
          if (!pop.has("culture"))
            pop.set("culture", province.get("culture"))
          if (!pop.has("religion"))
            pop.set("religion", province.get("religion"))
          pop
        })

    val pops = popTypes
      .flatMap(parsePops)
      .groupBy(_.get("type").asText())
      .foldLeft(JsonMapper.objectNode)((acc, pop) => acc.setEx(pop._1, pop._2))

    province.setEx("pops", pops)
  }

}
