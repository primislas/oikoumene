package com.lomicron.oikoumene.parsers.provinces

import com.fasterxml.jackson.databind.node.{ArrayNode, ObjectNode, TextNode}
import com.lomicron.oikoumene.model.provinces.Area
import com.lomicron.oikoumene.parsers.ClausewitzParser.Fields._
import com.lomicron.oikoumene.parsers.{ClausewitzParser, ConfigField}
import com.lomicron.oikoumene.repository.api.map.AreaRepository
import com.lomicron.oikoumene.repository.api.{LocalisationRepository, RepositoryFactory, ResourceRepository}
import com.lomicron.utils.collection.CollectionUtils._
import com.lomicron.utils.json.JsonMapper.{objectNode, patchFieldValue}
import com.lomicron.utils.parsing.scopes.ObjectScope
import com.lomicron.utils.parsing.serialization.BaseDeserializer
import com.typesafe.scalalogging.LazyLogging

object AreaParser extends LazyLogging {

  def apply(repos: RepositoryFactory,
            evalEntityFields: Boolean = false)
  : AreaRepository =
    apply(repos.resources, repos.localisations, repos.geography.areas, evalEntityFields)

  def apply
  (files: ResourceRepository,
   localisation: LocalisationRepository,
   areaRepo: AreaRepository,
   evalEntityFields: Boolean): AreaRepository = {

    val jsonNodes = files
      .getAreas
      .map(ClausewitzParser.parse(_, BaseDeserializer))
      .map(o => {
        if (o._2.nonEmpty) logger.warn(s"Encountered ${o._2.size} errors while parsing areas: ${o._2}")
        o._1.fields.toStream
      })
      .getOrElse(Stream.empty)
      .map(e => e.getKey -> e.getValue)
      .toMap
      .filterValues(_.size() > 0)
      .mapValues {
        case o@(_: ArrayNode) =>
          val area = objectNode
          area.set(provinceIdsKey, o)
          area
        case area: ObjectNode =>
          val ids = area.remove(ObjectScope.arrayKey)
          Option(ids).foreach(area.set(provinceIdsKey, _))
          area
        case default =>
          logger.warn("Unexpected area definition, omitting: {}", default.toString)
          objectNode
      }
      .mapKVtoValue((id, area) => patchFieldValue(area, idKey, TextNode.valueOf(id)))
      .mapKVtoValue(localisation.findAndSetAsLocName)
      .values.toSeq

    if (evalEntityFields) ConfigField.printCaseClass("Area", jsonNodes)

    jsonNodes.map(Area.fromJson).foreach(areaRepo.create)

    areaRepo
  }

}
