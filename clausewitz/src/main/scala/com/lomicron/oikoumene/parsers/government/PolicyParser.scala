package com.lomicron.oikoumene.parsers.government

import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.oikoumene.model.government.Policy
import com.lomicron.oikoumene.parsers.{ClausewitzParser, ConfigField}
import com.lomicron.oikoumene.repository.api.RepositoryFactory
import com.lomicron.oikoumene.repository.api.government.PolicyRepository
import com.lomicron.utils.json.JsonMapper.{ObjectNodeEx, objectNode}
import com.typesafe.scalalogging.LazyLogging

object PolicyParser extends LazyLogging {

  private val CPFs = ClausewitzParser.Fields

  object Fields {
    val monarchPower = "monarch_power"
    val potential = "potential"
    val allow = "technology"
    val aiWillDo = "ai_will_do"
    val all: Set[String] = Set(CPFs.idKey, CPFs.sourceFile, CPFs.localisation,
      monarchPower, potential, allow, aiWillDo, CPFs.modifier)

    val groups = "groups"
  }

  def apply(repos: RepositoryFactory, evalEntityFields: Boolean = false): PolicyRepository = {
    val files = repos.resources
    val localisation = repos.localisations

    val configs = files.getPolicies
    val confJsons = ClausewitzParser
      .parseFileFieldsAsEntities(configs)
      .map(parsePolicy)
      .map(localisation.setLocalisation)

    if (evalEntityFields)
      ConfigField.printCaseClass("Policy", confJsons)

    val policies = repos.policies
    confJsons.map(Policy.fromJson).foreach(policies.create)

    policies
  }

  def parsePolicy(o: ObjectNode): ObjectNode = {
    val modifier = o
      .entrySeq()
      .filterNot(e => Fields.all.contains(e.getKey))
      .foldLeft(objectNode)(_ setEx _)
    modifier.fieldSeq().foreach(o.remove)
    o.setEx(CPFs.modifier, modifier)
  }

}
