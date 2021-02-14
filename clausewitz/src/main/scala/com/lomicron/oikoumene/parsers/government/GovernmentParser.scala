package com.lomicron.oikoumene.parsers.government

import com.fasterxml.jackson.databind.node.{ArrayNode, ObjectNode}
import com.lomicron.oikoumene.model.government.{Government, LegacyGovernmentMapping}
import com.lomicron.oikoumene.model.modifiers.Modifier
import com.lomicron.oikoumene.parsers.ClausewitzParser.Fields
import com.lomicron.oikoumene.parsers.{ClausewitzParser, ConfigField}
import com.lomicron.oikoumene.repository.api.RepositoryFactory
import com.lomicron.oikoumene.repository.api.government.GovernmentRepository
import com.lomicron.oikoumene.repository.api.modifiers.ModifierRepository
import com.lomicron.oikoumene.repository.api.resources.{LocalisationRepository, ResourceRepository}
import com.lomicron.utils.collection.CollectionUtils.SeqEx
import com.lomicron.utils.json.JsonMapper.{JsonNodeEx, ObjectNodeEx, arrayNodeOf}

object GovernmentParser {

  val preDharmaMapping = "pre_dharma_mapping"
  val reformLevels = "reform_levels"

  def apply
  (
    repos: RepositoryFactory,
    evalEntityFields: Boolean = false
  ): GovernmentRepository =
    apply(repos.resources, repos.modifiers, repos.localisations, repos.governments, evalEntityFields)

  def apply
  (
    files: ResourceRepository,
    modifiers: ModifierRepository,
    localisation: LocalisationRepository,
    govRepo: GovernmentRepository,
    evalEntityFields: Boolean
  ): GovernmentRepository = {

    val govFiles = files.getGovernments
    val govConfigs = ClausewitzParser.parseFileFieldsAsEntities(govFiles)
    val govs = govConfigs
      .filterNot(_.getString(Fields.idKey).contains(preDharmaMapping))
      .map(parseGovernment)
      .map(localisation.setLocalisation)
    val preDharmaMappingConfigs = govConfigs.filter(_.getString(Fields.idKey).contains(preDharmaMapping))

    if (evalEntityFields)
      ConfigField.printCaseClass("Government", govs)

    govs.map(Government.fromJson).foreach(govRepo.create)
    val preDharmaMappings = parsePreDharmaMapping(preDharmaMappingConfigs)
      .map(LegacyGovernmentMapping.fromJson)
      .toMapEx(lgm => lgm.id -> lgm)
    govRepo.legacyMapping(preDharmaMappings)
    parseGovernmentRanks(files, modifiers)

    govRepo
  }

  def parseGovernment(o: ObjectNode): ObjectNode = {
    o
      .getObject(reformLevels)
      .map(parseReformLevels)
      .map(o.setEx(reformLevels, _))
      .getOrElse(o)
    ClausewitzParser.removeEmptyObjects(o)
  }

  def parseReformLevels(rls: ObjectNode): ArrayNode = {
    val parsedSeq = rls.entrySeq().flatMap(e => {
      val (k, v) = (e.getKey, e.getValue)
      v.asObject.map(rl => rl.setEx(Fields.idKey, k))
    })
    arrayNodeOf(parsedSeq)
  }

  def parsePreDharmaMapping(ms: Seq[ObjectNode]): Seq[ObjectNode] =
    ms.flatMap(parsePreDharmaMapping)

  def parsePreDharmaMapping(o: ObjectNode): Seq[ObjectNode] =
    o.entrySeq().flatMap(e => e.getValue.asObject.map(_.setEx(Fields.idKey, e.getKey)))

  def parseGovernmentRanks(files: ResourceRepository, modifiers: ModifierRepository): Seq[Modifier] = {
    val govRankConfs = files.getGovernmentRanks
    val ranks = ClausewitzParser
      .parseFileFieldsAsEntities(govRankConfs)
      .map(gr => gr.getString(Fields.idKey).map(id => gr.setEx(Fields.idKey, s"configured_gov_rank_$id")).getOrElse(gr))
      .map(Modifier.fromJson)
    ranks.foreach(modifiers.create)
    ranks
  }

}
