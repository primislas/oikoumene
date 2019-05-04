package com.lomicron.oikoumene.parsers.provinces

import com.fasterxml.jackson.databind.node.{ObjectNode, TextNode}
import com.lomicron.oikoumene.model.provinces.ColonialRegion
import com.lomicron.oikoumene.parsers.ClausewitzParser.Fields._
import com.lomicron.oikoumene.parsers.{ClausewitzParser, ConfigField}
import com.lomicron.oikoumene.repository.api.map.ColonialRegionRepository
import com.lomicron.oikoumene.repository.api.{LocalisationRepository, RepositoryFactory, ResourceRepository}
import com.lomicron.utils.collection.CollectionUtils._
import com.lomicron.utils.json.JsonMapper
import com.lomicron.utils.json.JsonMapper.patchFieldValue
import com.typesafe.scalalogging.LazyLogging

object ColonialRegionParser extends LazyLogging {

  def apply(repos: RepositoryFactory,
            evalEntityFields: Boolean = false)
  : ColonialRegionRepository =
    apply(repos.resources, repos.localisations, repos.geography.colonies, evalEntityFields)

  def apply
  (files: ResourceRepository,
   localisation: LocalisationRepository,
   colonialRegions: ColonialRegionRepository,
   evalEntityFields: Boolean)
  : ColonialRegionRepository = {

    val jsonNodes = files
      .getColonialRegions
      .map(ClausewitzParser.parse)
      .map(o => {
        if (o._2.nonEmpty) logger.warn(s"Encountered ${o._2.size} errors while parsing terrain: ${o._2}")
        o._1.fields.toStream
      })
      .getOrElse(Stream.empty)
      .map(e => e.getKey -> e.getValue).toMap
      .filterValues(n => {
        if (!n.isInstanceOf[ObjectNode])
          logger.warn(s"Expected colonial region ObjectNodes but encountered ${n.toString}")
        n.isInstanceOf[ObjectNode]
      })
      .mapValues(_.asInstanceOf[ObjectNode])
      .mapValues(JsonMapper.renameField(_, "provinces", provinceIdsKey))
      .mapKVtoValue((id, sRegion) => patchFieldValue(sRegion, idKey, TextNode.valueOf(id)))
      .mapKVtoValue(localisation.findAndSetAsLocName)
      .values.toSeq

    if (evalEntityFields)
      ConfigField.printCaseClass("ColonialRegion", jsonNodes)

    jsonNodes.map(ColonialRegion.fromJson).foreach(colonialRegions.create)

    colonialRegions
  }

}
