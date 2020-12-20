package com.lomicron.oikoumene.parsers.politics

import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.oikoumene.model.politics.RulerPersonality
import com.lomicron.oikoumene.parsers.ClausewitzParser.Fields
import com.lomicron.oikoumene.parsers.{ClausewitzParser, ConfigField}
import com.lomicron.oikoumene.repository.api.RepositoryFactory
import com.lomicron.oikoumene.repository.api.politics.RulerPersonalityRepository
import com.lomicron.utils.json.JsonMapper.{ObjectNodeEx, objectNode}

object RulerPersonalityParser {

  val nonModifierFields = Set(
    Fields.idKey, Fields.localisation, Fields.sourceFile, Fields.modifier,
    "heir_allow", "ruler_allow", "consort_allow", "allow", "chance",
    "nation_designer_cost", "custom_ai_explanation"
  )

  def apply(repos: RepositoryFactory,
            evalEntityFields: Boolean = false)
  : RulerPersonalityRepository = {
    val configs = repos.resources.getRulerPersonalities
    val nodes = ClausewitzParser
      .parseFileFieldsAsEntities(configs)
      .map(parsePersonality)
      .map(repos.localisations.setLocalisation)

    if (evalEntityFields)
      ConfigField.printCaseClass("RulerPersonality", nodes)

    nodes
      .map(RulerPersonality.fromJson)
      .foreach(repos.rulerPersonalities.create)

    repos.rulerPersonalities
  }

  def parsePersonality(json: ObjectNode): ObjectNode = {
    val modifier = json.entrySeq()
      .filterNot(e => nonModifierFields.contains(e.getKey))
      .foldLeft(objectNode)(_ setEx _)
    modifier.fieldSeq().foreach(json.remove)
    json.setEx(Fields.modifier, modifier)
  }

}
