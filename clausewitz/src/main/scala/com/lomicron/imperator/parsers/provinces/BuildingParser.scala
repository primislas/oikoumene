package com.lomicron.imperator.parsers.provinces

import com.lomicron.imperator.model.provinces.Building
import com.lomicron.imperator.repository.api.{BuildingRepository, RepositoryFactory}
import com.lomicron.oikoumene.parsers.{ClausewitzParser, ConfigField}
import com.typesafe.scalalogging.LazyLogging

object BuildingParser extends LazyLogging {

  def apply(repos: RepositoryFactory): BuildingRepository = {
    val files = repos.resources.getBuildings
    val es = ClausewitzParser
      .parseFileFieldsAsEntities(files)
    //      .map(localisation.setLocalisation)
    ConfigField.printCaseClass("Building", es)

    val repo = repos.buildings
    es.map(Building.fromJson).foreach(repo.create)


    repo
  }

}
