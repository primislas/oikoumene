package com.lomicron.imperator.parsers.provinces

import com.lomicron.imperator.repository.api.{ProvinceRepository, RepositoryFactory}
import com.lomicron.eu4.parsers.{ClausewitzParser, ConfigField}
import com.typesafe.scalalogging.LazyLogging

object ProvinceSetupParser extends LazyLogging {

  def apply(repos: RepositoryFactory): ProvinceRepository = {
    val provFiles = repos.resources.getProvinceSetup
    val ps = ClausewitzParser
      .parseFileFieldsAsEntities(provFiles)
//      .map(localisation.setLocalisation)
//      .map(parseTradeGood)
    ConfigField.printCaseClass("Province", ps)

    repos.provinces
  }

}
