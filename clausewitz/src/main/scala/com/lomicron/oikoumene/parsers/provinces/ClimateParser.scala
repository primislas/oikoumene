package com.lomicron.oikoumene.parsers.provinces

import com.fasterxml.jackson.databind.node.ArrayNode
import com.lomicron.oikoumene.model.provinces.Climate
import com.lomicron.oikoumene.parsers.ClausewitzParser.Fields._
import com.lomicron.oikoumene.parsers.{ClausewitzParser, ConfigField}
import com.lomicron.oikoumene.repository.api.RepositoryFactory
import com.lomicron.oikoumene.repository.api.map.ClimateRepository
import com.lomicron.oikoumene.repository.api.resources.{LocalisationRepository, ResourceRepository}
import com.lomicron.utils.collection.CollectionUtils._
import com.lomicron.utils.json.JsonMapper.{ObjectNodeEx, objectNode, patchFieldValue}
import com.typesafe.scalalogging.LazyLogging

object ClimateParser extends LazyLogging {

  def apply(repos: RepositoryFactory,
            evalEntityFields: Boolean = false)
  : ClimateRepository =
    apply(repos.resources, repos.localisations, repos.geography.climate, evalEntityFields)

  def apply
  (files: ResourceRepository,
   localisation: LocalisationRepository,
   climates: ClimateRepository,
   evalEntityFields: Boolean)
  : ClimateRepository = {

    val climateSettings = files
      .getClimate
      .map(ClausewitzParser.parse)
      .map(o => {
        if (o._2.nonEmpty) logger.warn(s"Encountered ${o._2.size} errors while parsing climate: ${o._2}")
        o._1.fields.toStream
      })
      .getOrElse(Stream.empty)
      .map(e => e.getKey -> e.getValue).toMap

    val jsonNodes = climateSettings
      .filterValues(_.isInstanceOf[ArrayNode])
      .mapValues(_.asInstanceOf[ArrayNode])
      .mapValues(patchFieldValue(objectNode, provinceIdsKey, _))
      .mapKVtoValue((id, region) => region.setEx(idKey, id))
      .mapKVtoValue(localisation.findAndSetAsLocName)
      .values.toSeq

    if (evalEntityFields)
      ConfigField.printCaseClass("Climate", jsonNodes)

    jsonNodes.map(Climate.fromJson).foreach(climates.create)
    climateSettings
      .get("equator_y_on_province_image")
      .filter(_.isInt)
      .map(_.intValue())
      .foreach(climates.equatorYOnProvinceImage)

    climates
  }

}
