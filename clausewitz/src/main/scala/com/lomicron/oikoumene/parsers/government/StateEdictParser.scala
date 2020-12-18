package com.lomicron.oikoumene.parsers.government

import com.lomicron.oikoumene.model.government.StateEdict
import com.lomicron.oikoumene.parsers.ClausewitzParser.Fields
import com.lomicron.oikoumene.parsers.{ClausewitzParser, ConfigField}
import com.lomicron.oikoumene.repository.api.RepositoryFactory
import com.lomicron.oikoumene.repository.api.government.StateEdictRepository
import com.typesafe.scalalogging.LazyLogging

object StateEdictParser extends LazyLogging {

  val edictFields = Set(
    Fields.idKey, Fields.sourceFile, Fields.localisation, Fields.color,
    Fields.modifier, Fields.potential, Fields.allow, Fields.aiWillDo
  )

  def apply(repos: RepositoryFactory, evalEntityFields: Boolean = false): StateEdictRepository = {
    val files = repos.resources
    val localisation = repos.localisations

    val configs = files.getStateEdicts
    val confJsons = ClausewitzParser
      .parseFileFieldsAsEntities(configs)
      .map(localisation.setLocalisation)

    if (evalEntityFields)
      ConfigField.printCaseClass("StateEdict", confJsons)

    val edictsRepo = repos.stateEdicts
    confJsons.map(StateEdict.fromJson).foreach(edictsRepo.create)

    edictsRepo
  }

}
