package com.lomicron.oikoumene.parsers.politics

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.oikoumene.model.politics.{Religion, ReligionGroup}
import com.lomicron.oikoumene.parsers.ClausewitzParser.Fields.idKey
import com.lomicron.oikoumene.parsers.{ClausewitzParser, ConfigField}
import com.lomicron.oikoumene.repository.api.RepositoryFactory
import com.lomicron.oikoumene.repository.api.politics.ReligionRepository
import com.lomicron.oikoumene.repository.api.resources.{LocalisationRepository, ResourceRepository}
import com.lomicron.utils.collection.CollectionUtils._
import com.lomicron.utils.json.JsonMapper._
import com.typesafe.scalalogging.LazyLogging

import scala.collection.immutable.ListMap

object ReligionParser extends LazyLogging {

  def apply(repos: RepositoryFactory,
            evalEntityFields: Boolean = false): ReligionRepository =
    apply(repos.resources, repos.localisations, repos.religions, evalEntityFields)

  def apply
  (files: ResourceRepository,
   localisation: LocalisationRepository,
   religionsRepo: ReligionRepository,
   evalEntityFields: Boolean): ReligionRepository = {

    val relFiles = files.getReligions
    val groupsAndRels = ClausewitzParser
      .parseFileFieldsAsEntities(relFiles)
      .map(parseReligions)
    val groups = groupsAndRels.map(_._1).map(localisation.setLocalisation)
    val religions = groupsAndRels.flatMap(_._2).map(localisation.setLocalisation)

    if (evalEntityFields) {
      ConfigField.printCaseClass("ReligionGroup", groups)
      ConfigField.printCaseClass("Religion", religions)
    }

    groups.map(ReligionGroup.fromJson).foreach(religionsRepo.createGroup)
    religions.map(Religion.fromJson).foreach(religionsRepo.create)

    religionsRepo
  }

  private def parseReligions(religionGroup: ObjectNode): (ObjectNode, Seq[ObjectNode]) = {
    val religions = religionGroup.fields.toStream
      .map(e => e.getKey -> e.getValue).foldLeft(ListMap[String, JsonNode]())(_ + _)
      .filterKeyValue((_, v) => isReligion(v))
      .mapValues(religion => religion.asInstanceOf[ObjectNode])
      .mapKVtoValue((id, religion) => religion.setEx(idKey, id))
      .values
      .map(religion => religion.setEx("religion_group", religionGroup.get("id")))
      .toSeq
    religions.map(_.get("id")).map(_.asText()).foreach(religionGroup.remove)
    val idsArray = arrayNodeOf(religions.map(_.get("id")))
    religionGroup.set("religion_ids", idsArray)
    (religionGroup, religions)
  }

  private def isReligion(node: JsonNode) =
    node.isInstanceOf[ObjectNode] && node.has("icon") && node.has("color")

}
