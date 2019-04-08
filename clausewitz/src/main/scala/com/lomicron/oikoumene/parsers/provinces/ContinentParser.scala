package com.lomicron.oikoumene.parsers.provinces

import com.fasterxml.jackson.databind.node.{ArrayNode, ObjectNode, TextNode}
import com.lomicron.oikoumene.parsers.ClausewitzParser.Fields._
import com.lomicron.oikoumene.parsers.{ClausewitzParser, ConfigField}
import com.lomicron.oikoumene.repository.api.map.ContinentRepository
import com.lomicron.oikoumene.repository.api.{LocalisationRepository, RepositoryFactory, ResourceRepository}
import com.lomicron.utils.collection.CollectionUtils._
import com.lomicron.utils.json.JsonMapper.{objectNode, patchFieldValue}
import com.typesafe.scalalogging.LazyLogging

object ContinentParser extends LazyLogging {

  def apply(repos: RepositoryFactory): ContinentRepository =
    apply(repos.resources, repos.localisations, repos.geography.continent)

  def apply
  (files: ResourceRepository,
   localisation: LocalisationRepository,
   continents: ContinentRepository): ContinentRepository = {

    files
      .getContinents
      .map(ClausewitzParser.parse)
      .map(o => {
        if (o._2.nonEmpty) logger.warn(s"Encountered ${o._2.size} errors while parsing superregions: ${o._2}")
        o._1.fields.toStream
      })
      .getOrElse(Stream.empty)
      .map(e => e.getKey -> e.getValue).toMap
      .filterValues(n => {
        if (!n.isInstanceOf[ArrayNode])
          logger.warn(s"Expected super-region ArrayNodes but encountered ${n.toString}")
        n.isInstanceOf[ArrayNode]
      })
      .mapValues(_.asInstanceOf[ArrayNode])
      .mapValues(objectNode.set(provinceIdsKey, _).asInstanceOf[ObjectNode])
      .mapKVtoValue((id, sRegion) => patchFieldValue(sRegion, idKey, TextNode.valueOf(id)))
      .mapKVtoValue(localisation.findAndSetAsLocName)
      .values
      .foreach(continents.create)

    ConfigField.printCaseClass("Continent", continents.findAll)

    continents
  }

}
