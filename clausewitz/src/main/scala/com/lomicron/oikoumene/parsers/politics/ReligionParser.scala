package com.lomicron.oikoumene.parsers.politics

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.{ObjectNode, TextNode}
import com.lomicron.oikoumene.model.politics.{Religion, ReligionGroup}
import com.lomicron.oikoumene.parsers.ClausewitzParser.Fields.idKey
import com.lomicron.oikoumene.parsers.{ClausewitzParser, ConfigField}
import com.lomicron.oikoumene.repository.api.politics.ReligionRepository
import com.lomicron.oikoumene.repository.api.{LocalisationRepository, RepositoryFactory, ResourceRepository}
import com.lomicron.utils.collection.CollectionUtils._
import com.lomicron.utils.json.JsonMapper.{arrayNodeOf, patchFieldValue}
import com.typesafe.scalalogging.LazyLogging

object ReligionParser extends LazyLogging {

  def apply(repos: RepositoryFactory): ReligionRepository =
    apply(repos.resources, repos.localisations, repos.religions)

  def apply
  (files: ResourceRepository,
   localisation: LocalisationRepository,
   religionsRepo: ReligionRepository): ReligionRepository = {

    val jsonNodes = files
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
      .mapKVtoValue((id, religionGroup) => patchFieldValue(religionGroup, idKey, TextNode.valueOf(id)))
      .mapKVtoValue(localisation.findAndSetAsLocName)
      .values.toSeq
      .map(parseReligions)

    val groups = jsonNodes.map(_._1)
    val religions = jsonNodes.flatMap(_._2)
      .map(r => localisation.findAndSetAsLocName(r.get("id").asText(), r))

    ConfigField.printCaseClass("ReligionGroup", groups)
    ConfigField.printCaseClass("Religion", religions)

    groups.map(ReligionGroup.fromJson).foreach(religionsRepo.createGroup)
    religions.map(Religion.fromJson).foreach(religionsRepo.create)

    religionsRepo
  }

  private def parseReligions(religionGroup: ObjectNode): (ObjectNode, Seq[ObjectNode]) = {
    val religions = religionGroup.fields.toStream
      .map(e => e.getKey -> e.getValue).toMap
      .filterKeyValue((_, v) => isReligion(v))
      .mapValues(religion => religion.asInstanceOf[ObjectNode])
      .mapKVtoValue((id, religion) => patchFieldValue(religion, idKey, TextNode.valueOf(id)))
      .values
      .map(religion => patchFieldValue(religion, "religion_group", religionGroup.get("id")))
      .toSeq
    religions.map(_.get("id")).map(_.asText()).foreach(religionGroup.remove)
    val idsArray = arrayNodeOf(religions.map(_.get("id")))
    religionGroup.set("religion_ids", idsArray)
    (religionGroup, religions)
  }

  private def isReligion(node: JsonNode) =
    node.isInstanceOf[ObjectNode] && node.has("icon") && node.has("color")

}
