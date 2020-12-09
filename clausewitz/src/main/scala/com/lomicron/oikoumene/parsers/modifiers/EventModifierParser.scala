package com.lomicron.oikoumene.parsers.modifiers

import com.lomicron.oikoumene.model.modifiers.Modifier
import com.lomicron.oikoumene.parsers.ClausewitzParser.setLocalisation
import com.lomicron.oikoumene.parsers.{ClausewitzParser, ConfigField}
import com.lomicron.oikoumene.repository.api.RepositoryFactory
import com.lomicron.oikoumene.repository.api.modifiers.EventModifierRepository
import com.lomicron.oikoumene.repository.api.resources.{LocalisationRepository, ResourceRepository}

object EventModifierParser {

  def apply(
             repos: RepositoryFactory,
             evalEntityFields: Boolean = false
           ): EventModifierRepository =
    apply(repos.resources, repos.localisations, repos.eventModifiers, evalEntityFields)

  def apply
  (
    files: ResourceRepository,
    localisation: LocalisationRepository,
    modifiersRepo: EventModifierRepository,
    evalEntityFields: Boolean
  ): EventModifierRepository = {
    val configs = files.getEventModifiers
    val eventModifiers = ClausewitzParser
      .parseFileFieldsAsEntities(configs)
      .map(setLocalisation(_, localisation))

    if (evalEntityFields)
      ConfigField.printMapClass("Modifier", eventModifiers)

    eventModifiers.map(Modifier.fromJson).foreach(modifiersRepo.create)

    modifiersRepo
  }

}
