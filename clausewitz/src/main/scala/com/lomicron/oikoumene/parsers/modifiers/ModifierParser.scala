package com.lomicron.oikoumene.parsers.modifiers

import com.lomicron.oikoumene.model.modifiers.Modifier
import com.lomicron.oikoumene.parsers.ClausewitzParser.setLocalisation
import com.lomicron.oikoumene.parsers.{ClausewitzParser, ConfigField}
import com.lomicron.oikoumene.repository.api.RepositoryFactory
import com.lomicron.oikoumene.repository.api.modifiers.ModifierRepository
import com.lomicron.oikoumene.repository.api.resources.{LocalisationRepository, ResourceRepository}

object ModifierParser {

  def apply(
             repos: RepositoryFactory,
             evalEntityFields: Boolean = false
           ): ModifierRepository =
    apply(repos.resources, repos.localisations, repos.eventModifiers, evalEntityFields)

  def apply
  (
    files: ResourceRepository,
    localisation: LocalisationRepository,
    modifiersRepo: ModifierRepository,
    evalEntityFields: Boolean
  ): ModifierRepository = {
    val eventModifiers = files.getEventModifiers
    val staticModifiers = files.getStaticModifiers
    val configs = eventModifiers ++ staticModifiers
    val modifiers = ClausewitzParser
      .parseFileFieldsAsEntities(configs)
      .map(setLocalisation(_, localisation))

    if (evalEntityFields)
      ConfigField.printMapClass("Modifier", modifiers)

    modifiers.map(Modifier.fromJson).foreach(modifiersRepo.create)

    modifiersRepo
  }

}
