package com.lomicron.imperator.parsers.provinces

import com.lomicron.imperator.model.provinces.PopType
import com.lomicron.imperator.repository.api.{PopTypeRepository, RepositoryFactory}
import com.lomicron.oikoumene.parsers.{ClausewitzParser, ConfigField}

object PopTypeParser {

  def apply(repos: RepositoryFactory): PopTypeRepository = {
    val files = repos.resources.getPopTypes
    val es = ClausewitzParser
      .parseFileFieldsAsEntities(files)
    //      .map(localisation.setLocalisation)
    ConfigField.printCaseClass("PopType", es)

    val repo = repos.popTypes
    es.map(PopType.fromJson).foreach(repo.create)

    repo
  }

}
