package com.lomicron.imperator.parsers.provinces

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.{ArrayNode, ObjectNode}
import com.lomicron.imperator.model.provinces.Province
import com.lomicron.imperator.repository.api.{BuildingRepository, PopTypeRepository, ProvinceRepository, RepositoryFactory}
import com.lomicron.oikoumene.parsers.{ClausewitzParser, ConfigField}
import com.lomicron.utils.json.JsonMapper
import com.typesafe.scalalogging.LazyLogging
import com.lomicron.utils.json.JsonMapper.ObjectNodeEx
import com.lomicron.utils.json.JsonMapper.JsonNodeEx
import com.lomicron.utils.json.JsonMapper.ArrayNodeEx

object ProvinceSetupParser extends LazyLogging {

  def apply(repos: RepositoryFactory): ProvinceRepository = {
    val popTypes = PopTypeParser(repos)
    val popTypeIds = popTypes.findAll.map(_.id)
    val buildings = BuildingParser(repos)
    val buildingIds = buildings.findAll.map(_.id)

    val files = repos.resources.getProvinceSetup
    val es = ClausewitzParser
      .parseFileFieldsAsEntities(files)
      .map(setPops(_, popTypeIds))
      .map(setBuildings(_, buildingIds))
//      .map(localisation.setLocalisation)
    ConfigField.printCaseClass("Province", es)

    val repo = repos.provinces
    es.map(Province.fromJson).foreach(repo.create)

    repos.provinces
  }

  def setBuildings(province: ObjectNode, buildingIds: Seq[String]): ObjectNode = {
    val buildings = buildingIds
      .filter(province.has)
      .map(pt => pt -> province.remove(pt))
      .foldLeft(JsonMapper.objectNode)((acc, t) => acc.setEx(t._1, t._2))
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

    province.setEx("pops", pops);
  }

}
