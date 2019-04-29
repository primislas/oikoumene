package com.lomicron.oikoumene.parsers.diplomacy

import com.lomicron.oikoumene.parsers.{ClausewitzParser, ConfigField}
import com.lomicron.oikoumene.repository.api.politics.DiplomacyRepository
import com.lomicron.oikoumene.repository.api.{RepositoryFactory, ResourceRepository}
import com.lomicron.utils.json.JsonMapper._
import com.typesafe.scalalogging.LazyLogging

object WarGoalParser extends LazyLogging {

  def apply(repos: RepositoryFactory): DiplomacyRepository = apply(repos.resources, repos.diplomacy)

  def apply
  (files: ResourceRepository,
   diplomacyRepo: DiplomacyRepository
  ): DiplomacyRepository = {

    val warGoals = ClausewitzParser
      .parseFileFieldsAsEntities(files.getWarGoalTypes)

    val attackerConfs = warGoals.flatMap(_.getObject("attacker"))
    val defenderConfs = warGoals.flatMap(_.getObject("defender"))
    val provinceConditions = warGoals.flatMap(_.getObject("allowed_provinces"))
    val conditions = ClausewitzParser.parseNestedConditions(provinceConditions)
    ConfigField.printCaseClass("ProvinceCondition", conditions)
    ConfigField.printCaseClass("PeaceDealModifiers", attackerConfs ++ defenderConfs)
    ConfigField.printCaseClass("WarGoalType", warGoals)

    diplomacyRepo
  }

}
