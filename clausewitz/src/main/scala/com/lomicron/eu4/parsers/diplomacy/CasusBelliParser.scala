package com.lomicron.eu4.parsers.diplomacy

import com.lomicron.eu4.model.diplomacy.CasusBelli
import com.lomicron.eu4.parsers.{ClausewitzParser, ConfigField}
import com.lomicron.eu4.repository.api.RepositoryFactory
import com.lomicron.eu4.repository.api.diplomacy.CasusBelliRepository
import com.lomicron.eu4.repository.api.resources.{LocalisationRepository, ResourceRepository}
import com.lomicron.utils.json.JsonMapper._

object CasusBelliParser {

  def apply(repos: RepositoryFactory): CasusBelliRepository =
    apply(repos.resources, repos.localisations, repos.casusBelli)

  def apply
  (files: ResourceRepository,
   localisation: LocalisationRepository,
   cbRepo: CasusBelliRepository,
   evalEntityFields: Boolean = false
  ): CasusBelliRepository = {
    val configs = files.getCasusBelliTypes
    val cbs = ClausewitzParser
      .parseFileFieldsAsEntities(configs)
      .map(localisation.setLocalisation)

    if (evalEntityFields) {
      val prereqs = cbs.flatMap(_.getObject("prerequisites"))
      val conditions = ClausewitzParser.parseNestedConditions(prereqs)
      ConfigField.printCaseClass("TagCondition", conditions)
      ConfigField.printCaseClass("CasusBelli", cbs)
    }

    val parsedCbs = cbs.map(CasusBelli.fromJson)
    parsedCbs.foreach(cbRepo.create)

    cbRepo
  }

}
