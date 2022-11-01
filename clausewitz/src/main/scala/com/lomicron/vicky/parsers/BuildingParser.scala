package com.lomicron.vicky.parsers

import com.lomicron.oikoumene.parsers.{ClausewitzParser, ConfigField}
import com.lomicron.oikoumene.repository.api.resources.LocalisationRepository
import com.lomicron.vicky.model.Building
import com.lomicron.vicky.repository.api.{BuildingRepository, RepositoryFactory, ResourceRepository}
import com.typesafe.scalalogging.LazyLogging

object BuildingParser extends LazyLogging {

  def apply(repos: RepositoryFactory, evalEntityFields: Boolean = false): BuildingRepository =
    apply(repos.resources, repos.localisations, repos.buildings, evalEntityFields)

  def apply
  (
    files: ResourceRepository,
    localisation: LocalisationRepository,
    buildingsRepo: BuildingRepository,
    evalEntityFields: Boolean
  ): BuildingRepository = {

    val buildings = ClausewitzParser
      .parseFileFieldsAsEntities(files.getBuildings)
      .map(localisation.setBuildingLocalisation)

    if (evalEntityFields)
      ConfigField.printCaseClass("Building", buildings)

    buildings
      .map(Building.fromJson)
      .foreach(buildingsRepo.create)
    buildingsRepo
  }

}
