package com.lomicron.vicky.parsers

import com.lomicron.oikoumene.parsers.{ClausewitzParser, ConfigField}
import com.lomicron.oikoumene.repository.api.resources.LocalisationRepository
import com.lomicron.utils.json.JsonMapper.JsonNodeEx
import com.lomicron.vicky.model.technology.Invention
import com.lomicron.vicky.repository.api.{RepositoryFactory, ResourceRepository, TechnologyRepository}
import com.typesafe.scalalogging.LazyLogging

object InventionParser extends LazyLogging {

  def apply(repos: RepositoryFactory, evalEntityFields: Boolean = false): TechnologyRepository =
    apply(repos.resources, repos.localisations, repos.technology, evalEntityFields)

  def apply
  (
    files: ResourceRepository,
    localisation: LocalisationRepository,
    technologyRepo: TechnologyRepository,
    evalEntityFields: Boolean
  ): TechnologyRepository = {

    val inventions = ClausewitzParser
      .parseFileFieldsAsEntities(files.getInventions)
      .map(localisation.setLocalisation)

    if (evalEntityFields) {
      ConfigField.printCaseClass("Invention", inventions)
      val limits = inventions.flatMap(_.getObject("limit"))
      ConfigField.printCaseClass("Limit", limits)
      val chances = inventions.flatMap(_.getObject("chance"))
      ConfigField.printCaseClass("Chance", chances)
    }

    inventions
      .map(Invention.fromJson)
      .foreach(technologyRepo.create)
    technologyRepo
  }

}
