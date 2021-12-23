package com.lomicron.imperator.parsers.provinces

import com.lomicron.imperator.model.provinces.Province
import com.lomicron.imperator.repository.api.{ProvinceRepository, RepositoryFactory}
import com.lomicron.oikoumene.parsers.{ClausewitzParser, ConfigField}
import com.typesafe.scalalogging.LazyLogging

object ProvinceSetupParser extends LazyLogging {

  def apply(repos: RepositoryFactory): ProvinceRepository = {
    val buildings = BuildingParser(repos)
    val popTypes = PopTypeParser(repos)

    val files = repos.resources.getProvinceSetup
    val es = ClausewitzParser
      .parseFileFieldsAsEntities(files)
//      .map(localisation.setLocalisation)
//      .map(parseTradeGood)
    ConfigField.printCaseClass("Province", es)

    val repo = repos.provinces
    es.map(Province.fromJson).foreach(repo.create)

    repos.provinces
  }

}
