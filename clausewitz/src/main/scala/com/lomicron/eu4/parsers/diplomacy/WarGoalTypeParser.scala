package com.lomicron.eu4.parsers.diplomacy

import com.lomicron.eu4.model.diplomacy.WarGoalType
import com.lomicron.eu4.repository.api.RepositoryFactory
import com.lomicron.eu4.repository.api.diplomacy.WarGoalTypeRepository
import com.lomicron.eu4.repository.api.resources.ResourceRepository
import com.lomicron.oikoumene.parsers.{ClausewitzParser, ConfigField}
import com.lomicron.oikoumene.repository.api.resources.LocalisationRepository
import com.lomicron.utils.json.JsonMapper._
import com.typesafe.scalalogging.LazyLogging

object WarGoalTypeParser extends LazyLogging {

  def apply(repos: RepositoryFactory): WarGoalTypeRepository =
    apply(repos.resources, repos.localisations, repos.warGoalTypes)

  def apply
  (files: ResourceRepository,
   localisation: LocalisationRepository,
   warGoalTypesRepo: WarGoalTypeRepository,
   evalEntityFields: Boolean = false
  ): WarGoalTypeRepository = {

    val warGoals = ClausewitzParser
      .parseFileFieldsAsEntities(files.getWarGoalTypes)
      .map(localisation.setLocalisation)

    if (evalEntityFields) {
      val attackerConfs = warGoals.flatMap(_.getObject("attacker"))
      val defenderConfs = warGoals.flatMap(_.getObject("defender"))
      val provinceConditions = warGoals.flatMap(_.getObject("allowed_provinces"))
      val conditions = ClausewitzParser.parseNestedConditions(provinceConditions)
      ConfigField.printCaseClass("ProvinceCondition", conditions)
      ConfigField.printCaseClass("PeaceDealModifiers", attackerConfs ++ defenderConfs)
      ConfigField.printCaseClass("WarGoalType", warGoals)
    }

    val parsedTypes = warGoals.map(WarGoalType.fromJson)
    parsedTypes.foreach(warGoalTypesRepo.create)

    warGoalTypesRepo
  }

}
