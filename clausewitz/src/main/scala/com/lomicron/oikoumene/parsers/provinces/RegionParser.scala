package com.lomicron.oikoumene.parsers.provinces

import com.fasterxml.jackson.databind.node.{ObjectNode, TextNode}
import com.lomicron.oikoumene.model.provinces.Region
import com.lomicron.oikoumene.parsers.ClausewitzParser.Fields.idKey
import com.lomicron.oikoumene.parsers.{ClausewitzParser, ConfigField}
import com.lomicron.oikoumene.repository.api.map.RegionRepository
import com.lomicron.oikoumene.repository.api.{LocalisationRepository, RepositoryFactory, ResourceRepository}
import com.lomicron.utils.collection.CollectionUtils._
import com.lomicron.utils.json.JsonMapper.patchFieldValue
import com.typesafe.scalalogging.LazyLogging

object RegionParser extends LazyLogging {

  def apply(repos: RepositoryFactory,
            evalEntityFields: Boolean = false)
  : RegionRepository =
    apply(repos.resources, repos.localisations, repos.geography.regions, evalEntityFields)


  def apply
  (files: ResourceRepository,
   localisation: LocalisationRepository,
   regions: RegionRepository,
   evalEntityFields: Boolean): RegionRepository = {

    val jsonNodes = files
      .getRegions
      .map(ClausewitzParser.parse)
      .map(o => {
        if (o._2.nonEmpty) logger.warn(s"Encountered ${o._2.size} errors while parsing regions: ${o._2}")
        o._1.fields.toStream
      })
      .getOrElse(Stream.empty)
      .map(e => e.getKey -> e.getValue).toMap
      .filterValues(n => {
        if (!n.isInstanceOf[ObjectNode])
          logger.warn(s"Expected region ObjectNode but encountered ${n.toString}")
        n.isInstanceOf[ObjectNode]
      })
      .mapValues(_.asInstanceOf[ObjectNode])
      .mapKVtoValue((id, region) => patchFieldValue(region, idKey, TextNode.valueOf(id)))
      .mapKVtoValue(localisation.findAndSetAsLocName)
      .values.toSeq

    if (evalEntityFields) ConfigField.printCaseClass("Region", jsonNodes)

    jsonNodes.map(Region.fromJson).foreach(regions.create)

    regions
  }

}
