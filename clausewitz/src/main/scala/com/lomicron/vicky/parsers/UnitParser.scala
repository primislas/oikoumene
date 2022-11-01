package com.lomicron.vicky.parsers

import com.lomicron.oikoumene.parsers.{ClausewitzParser, ConfigField}
import com.lomicron.oikoumene.repository.api.resources.LocalisationRepository
import com.lomicron.vicky.model.military.Unit
import com.lomicron.vicky.repository.api.{RepositoryFactory, ResourceRepository, UnitRepository}
import com.typesafe.scalalogging.LazyLogging

object UnitParser extends LazyLogging {

  def apply(repos: RepositoryFactory, evalEntityFields: Boolean = false): UnitRepository =
    apply(repos.resources, repos.localisations, repos.units, evalEntityFields)

  def apply
  (
    files: ResourceRepository,
    localisation: LocalisationRepository,
    unitsRepo: UnitRepository,
    evalEntityFields: Boolean
  ): UnitRepository = {

    val units = ClausewitzParser
      .parseFileFieldsAsEntities(files.getUnits)
      .map(localisation.setLocalisation)

    if (evalEntityFields)
      ConfigField.printCaseClass("Unit", units)

    units
      .map(Unit.fromJson)
      .foreach(unitsRepo.create)

    unitsRepo
  }

}
