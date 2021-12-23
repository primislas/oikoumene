package com.lomicron.imperator.parsers.provinces

import com.lomicron.eu4.parsers.{ClausewitzParser, ConfigField}
import com.lomicron.imperator.repository.api.{BuildingRepository, ProvinceRepository, RepositoryFactory}
import com.typesafe.scalalogging.LazyLogging

object BuildingParser extends LazyLogging {

  def apply(repos: RepositoryFactory): BuildingRepository = {
    val provFiles = repos.resources.getBuildings
    val ps = ClausewitzParser
      .parseFileFieldsAsEntities(provFiles)
    //      .map(localisation.setLocalisation)
    //      .map(parseTradeGood)
    ConfigField.printCaseClass("Province", ps)

    repos.buildings
  }

}
