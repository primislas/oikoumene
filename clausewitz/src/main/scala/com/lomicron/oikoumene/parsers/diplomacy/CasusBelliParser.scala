package com.lomicron.oikoumene.parsers.diplomacy

import com.lomicron.oikoumene.model.diplomacy.CasusBelli
import com.lomicron.oikoumene.parsers.{ClausewitzParser, ConfigField}
import com.lomicron.oikoumene.repository.api.RepositoryFactory
import com.lomicron.oikoumene.repository.api.diplomacy.CasusBelliRepository
import com.lomicron.oikoumene.repository.api.resources.{LocalisationRepository, ResourceRepository}
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
