package com.lomicron.vicky.parsers

import com.lomicron.oikoumene.parsers.{ClausewitzParser, ConfigField}
import com.lomicron.oikoumene.repository.api.resources.LocalisationRepository
import com.lomicron.vicky.model.politics.PopType
import com.lomicron.vicky.repository.api.{PopTypeRepository, RepositoryFactory, ResourceRepository}
import com.typesafe.scalalogging.LazyLogging

object PopTypeParser extends LazyLogging {

  def apply(repos: RepositoryFactory, evalEntityFields: Boolean = false): PopTypeRepository =
    apply(repos.resources, repos.localisations, repos.popTypes, evalEntityFields)

  def apply
  (
    files: ResourceRepository,
    localisation: LocalisationRepository,
    buildingsRepo: PopTypeRepository,
    evalEntityFields: Boolean
  ): PopTypeRepository = {

    val buildings = ClausewitzParser
      .parseFilesAsEntities(files.getPopTypes)
      .map(localisation.setLocalisation)

    if (evalEntityFields)
      ConfigField.printCaseClass("PopType", buildings)

    buildings
      .map(PopType.fromJson)
      .foreach(buildingsRepo.create)

    buildingsRepo
  }

}
