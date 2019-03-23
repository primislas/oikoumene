package com.lomicron.oikoumene.parsers

import com.fasterxml.jackson.databind.node.{ObjectNode, TextNode}
import com.lomicron.oikoumene.engine.Oikoumene.idKey
import com.lomicron.oikoumene.repository.api.politics.CultureRepository
import com.lomicron.oikoumene.repository.api.{LocalisationRepository, ResourceRepository}
import com.lomicron.utils.collection.CollectionUtils._
import com.lomicron.utils.json.JsonMapper.{arrayNodeOf, patchFieldValue}
import com.typesafe.scalalogging.LazyLogging

object CultureParser extends LazyLogging {

  def apply
  (files: ResourceRepository,
   localisation: LocalisationRepository,
   cultureRepo: CultureRepository): CultureRepository = {

    files
      .getCultures
      .map(ClausewitzParser.parse)
      .map(o => {
        if (o._2.nonEmpty) logger.warn(s"Encountered ${o._2.size} errors while parsing cultures: ${o._2}")
        o._1.fields.toStream
      })
      .getOrElse(Stream.empty)
      .map(e => e.getKey -> e.getValue).toMap
      .filterKeyValue((id, n) => {
        if (!n.isInstanceOf[ObjectNode])
          logger.warn(s"Expected culture group ObjectNode but at '$id' encountered ${n.toString}")
        n.isInstanceOf[ObjectNode]
      })
      .mapValues(_.asInstanceOf[ObjectNode])
      .mapKVtoValue((id, religionGroup) => patchFieldValue(religionGroup, idKey, TextNode.valueOf(id)))
      .mapKVtoValue(localisation.findAndSetAsLocName)
      .values
      .map(parseCultures)
      .foreach { case (group: ObjectNode, cultures: Seq[ObjectNode]) =>
        cultureRepo.createGroup(group)
        cultures.foreach(c => localisation.findAndSetAsLocName(c.get("id").asText(), c))
        cultureRepo.create(cultures)
      }

    cultureRepo
  }

  private def parseCultures(cultureGroup: ObjectNode): (ObjectNode, Seq[ObjectNode]) = {
    val cultures = cultureGroup.fields.toStream
      .map(e => e.getKey -> e.getValue).toMap
      .filterKeyValue((f, _) => isCultureField(f))
      .mapValues(culture => culture.asInstanceOf[ObjectNode])
      .mapKVtoValue((id, culture) => patchFieldValue(culture, idKey, TextNode.valueOf(id)))
      .values
      .map(culture => patchFieldValue(culture, "culture_group_id", cultureGroup.get("id")))
      .toSeq
    cultures.map(_.get("id")).map(_.asText()).foreach(cultureGroup.remove)
    val idsArray = arrayNodeOf(cultures.map(_.get("id")))
    cultureGroup.set("culture_ids", idsArray)
    (cultureGroup, cultures)
  }

  private val cultureGroupFields = Set("id", "localisation",
    "culture_ids", "graphical_culture", "male_names",
    "female_names", "dynasty_names")

  private def isCultureField(field: String) = !cultureGroupFields.contains(field)

}