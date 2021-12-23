package com.lomicron.eu4.parsers.government

import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.eu4.model.government.Policy
import com.lomicron.eu4.parsers.ClausewitzParser.Fields
import com.lomicron.eu4.parsers.{ClausewitzParser, ConfigField}
import com.lomicron.eu4.repository.api.RepositoryFactory
import com.lomicron.eu4.repository.api.government.PolicyRepository
import com.lomicron.utils.json.JsonMapper.{ObjectNodeEx, objectNode}
import com.typesafe.scalalogging.LazyLogging

object PolicyParser extends LazyLogging {

  val monarchPower = "monarch_power"
  val groups = "groups"
  val policyFields = Set(
    Fields.idKey, Fields.sourceFile, Fields.localisation, monarchPower,
    Fields.potential, Fields.allow, Fields.aiWillDo, Fields.modifier
  )

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
      .filterNot(e => policyFields.contains(e.getKey))
      .foldLeft(objectNode)(_ setEx _)
    modifier.fieldSeq().foreach(o.remove)
    o.setEx(Fields.modifier, modifier)
  }

}
