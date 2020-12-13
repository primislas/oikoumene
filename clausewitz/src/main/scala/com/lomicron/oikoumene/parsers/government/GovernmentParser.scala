package com.lomicron.oikoumene.parsers.government

import com.fasterxml.jackson.databind.node.{ArrayNode, ObjectNode}
import com.lomicron.oikoumene.model.government.{Government, LegacyGovernmentMapping}
import com.lomicron.oikoumene.parsers.ClausewitzParser.{Fields, setLocalisation}
import com.lomicron.oikoumene.parsers.{ClausewitzParser, ConfigField}
import com.lomicron.oikoumene.repository.api.RepositoryFactory
import com.lomicron.oikoumene.repository.api.government.GovernmentRepository
import com.lomicron.oikoumene.repository.api.resources.{LocalisationRepository, ResourceRepository}
import com.lomicron.utils.collection.CollectionUtils.SeqEx
import com.lomicron.utils.json.JsonMapper.{JsonNodeEx, ObjectNodeEx, arrayNodeOf}

object GovernmentParser {

  val preDharmaMapping = "pre_dharma_mapping"
  val reformLevels = "reform_levels"

  def apply(
             repos: RepositoryFactory,
             evalEntityFields: Boolean = false
           ): GovernmentRepository =
    apply(repos.resources, repos.localisations, repos.governments, evalEntityFields)

  def apply
  (files: ResourceRepository,
   localisation: LocalisationRepository,
   govRepo: GovernmentRepository,
   evalEntityFields: Boolean): GovernmentRepository = {

    val govFiles = files.getGovernments
    val govConfigs = ClausewitzParser.parseFileFieldsAsEntities(govFiles)
    val govs = govConfigs
      .filterNot(_.getString(Fields.idKey).contains(preDharmaMapping))
      .map(parseGovernment)
      .map(setLocalisation(_, localisation))
    val preDharmaMappingConfigs = govConfigs.filter(_.getString(Fields.idKey).contains(preDharmaMapping))

    if (evalEntityFields)
      ConfigField.printCaseClass("Government", govs)

    govs.map(Government.fromJson).foreach(govRepo.create)
    val preDharmaMappings = parsePreDharmaMapping(preDharmaMappingConfigs)
      .map(LegacyGovernmentMapping.fromJson)
      .toMapEx(lgm => lgm.id -> lgm)
    govRepo.legacyMapping(preDharmaMappings)

    govRepo
  }

  def parseGovernment(o: ObjectNode): ObjectNode = {
    o
      .getObject(reformLevels)
      .map(parseReformLevels)
      .map(o.setEx(reformLevels, _))
      .getOrElse(o)
  }

  def parseReformLevels(rls: ObjectNode): ArrayNode = {
    val parsedSeq = rls.fieldSeq().flatMap(e => {
      val (k, v) = (e.getKey, e.getValue)
      v.asObject.map(rl => rl.setEx(Fields.idKey, k))
    })
    arrayNodeOf(parsedSeq)
  }

  def parsePreDharmaMapping(ms: Seq[ObjectNode]): Seq[ObjectNode] =
    ms.flatMap(parsePreDharmaMapping)

  def parsePreDharmaMapping(o: ObjectNode): Seq[ObjectNode] =
    o.fieldSeq().flatMap(e => e.getValue.asObject.map(_.setEx(Fields.idKey, e.getKey)))

}
