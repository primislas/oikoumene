package com.lomicron.eu4.parsers.provinces

import com.fasterxml.jackson.databind.node.{ArrayNode, ObjectNode, TextNode}
import com.lomicron.eu4.model.provinces.SuperRegion
import com.lomicron.eu4.parsers.ClausewitzParser.Fields._
import com.lomicron.eu4.parsers.{ClausewitzParser, ConfigField}
import com.lomicron.eu4.repository.api.map.SuperRegionRepository
import com.lomicron.eu4.repository.api.resources.{LocalisationRepository, ResourceRepository}
import com.lomicron.eu4.repository.api.RepositoryFactory
import com.lomicron.utils.collection.CollectionUtils._
import com.lomicron.utils.json.JsonMapper.{objectNode, patchFieldValue}
import com.typesafe.scalalogging.LazyLogging

object SuperRegionParser extends LazyLogging {

  def apply(repos: RepositoryFactory,
            evalEntityFields: Boolean = false)
  : SuperRegionRepository =
    apply(repos.resources, repos.localisations, repos.geography.superregions, evalEntityFields)

  def apply
  (files: ResourceRepository,
   localisation: LocalisationRepository,
   superregions: SuperRegionRepository,
   evalEntityFields: Boolean): SuperRegionRepository = {

    val jsonNodes = files
      .getSuperregions
      .map(ClausewitzParser.parse)
      .map(o => {
        if (o._2.nonEmpty) logger.warn(s"Encountered ${o._2.size} errors while parsing superregions: ${o._2}")
        o._1.fields.toStream
      })
      .getOrElse(LazyList.empty)
      .map(e => e.getKey -> e.getValue).toMap
      .mapValuesEx(ClausewitzParser.objToEmptyArray)
      .filterValues(n => {
        if (!n.isInstanceOf[ArrayNode])
          logger.warn(s"Expected super-region ArrayNodes but encountered ${n.toString}")
        n.isInstanceOf[ArrayNode]
      })
      .mapValuesEx(_.asInstanceOf[ArrayNode])
      .mapValuesEx(objectNode.set(regionIdsKey, _).asInstanceOf[ObjectNode])
      .mapKVtoValue((id, sRegion) => patchFieldValue(sRegion, idKey, TextNode.valueOf(id)))
      .mapKVtoValue(localisation.findAndSetAsLocName)
      .values.toSeq

    if (evalEntityFields)
      ConfigField.printCaseClass("SuperRegion", jsonNodes)

    jsonNodes.map(SuperRegion.fromJson).foreach(superregions.create)

    superregions
  }

}
