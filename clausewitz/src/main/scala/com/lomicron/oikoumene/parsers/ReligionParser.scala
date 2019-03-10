package com.lomicron.oikoumene.parsers

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.{ObjectNode, TextNode}
import com.lomicron.oikoumene.engine.Oikoumene.idKey
import com.lomicron.oikoumene.repository.api.politics.ReligionRepository
import com.lomicron.oikoumene.repository.api.{LocalisationRepository, ResourceRepository}
import com.lomicron.utils.collection.CollectionUtils._
import com.lomicron.utils.json.JsonMapper.{arrayNodeOf, mergeFieldValue}
import com.typesafe.scalalogging.LazyLogging

object ReligionParser extends LazyLogging {

  def apply
  (files: ResourceRepository,
   localisation: LocalisationRepository,
   religionsRepo: ReligionRepository): ReligionRepository = {

    files
      .getReligions
      .map(ClausewitzParser.parse)
      .map(o => {
        if (o._2.nonEmpty) logger.warn(s"Encountered ${o._2.size} errors while parsing religion: ${o._2}")
        o._1.fields.toStream
      })
      .getOrElse(Stream.empty)
      .map(e => e.getKey -> e.getValue).toMap
      .filterKeyValue((id, n) => {
        if (!n.isInstanceOf[ObjectNode])
          logger.warn(s"Expected religion group ObjectNode but at '$id' encountered ${n.toString}")
        n.isInstanceOf[ObjectNode]
      })
      .mapValues(_.asInstanceOf[ObjectNode])
      .mapKVtoValue((id, religionGroup) => mergeFieldValue(religionGroup, idKey, TextNode.valueOf(id)))
      .mapKVtoValue(localisation.findAndSetAsLocName)
      .values
      .map(parseReligions)
      .foreach { case (group: ObjectNode, religions: Seq[ObjectNode]) =>
        religionsRepo.createGroup(group)
        religions.foreach(r => localisation.findAndSetAsLocName(r.get("id").asText(), r))
        religionsRepo.create(religions)
      }

    religionsRepo
  }

  private def parseReligions(religionGroup: ObjectNode): (ObjectNode, Seq[ObjectNode]) = {
    val religions = religionGroup.fields.toStream
      .map(e => e.getKey -> e.getValue).toMap
      .filterKeyValue((_, v) => isReligion(v))
      .mapValues(religion => religion.asInstanceOf[ObjectNode])
      .mapKVtoValue((id, religion) => mergeFieldValue(religion, idKey, TextNode.valueOf(id)))
      .values
      .map(religion => mergeFieldValue(religion, "religion_group", religionGroup.get("id")))
      .toSeq
    religions.map(_.get("id")).map(_.asText()).foreach(religionGroup.remove)
    val idsArray = arrayNodeOf(religions.map(_.get("id")))
    religionGroup.set("religion_ids", idsArray)
    (religionGroup, religions)
  }

  private def isReligion(node: JsonNode) =
    node.isInstanceOf[ObjectNode] && node.has("icon") && node.has("color")

}
