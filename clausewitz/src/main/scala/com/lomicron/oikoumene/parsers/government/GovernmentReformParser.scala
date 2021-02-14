package com.lomicron.oikoumene.parsers.government

import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.oikoumene.model.government.GovernmentReform
import com.lomicron.oikoumene.parsers.{ClausewitzParser, ConfigField}
import com.lomicron.oikoumene.repository.api.RepositoryFactory
import com.lomicron.oikoumene.repository.api.government.GovernmentReformRepository
import com.lomicron.oikoumene.repository.api.resources.{LocalisationRepository, ResourceRepository}
import com.lomicron.utils.json.JsonMapper.{ArrayNodeEx, ObjectNodeEx}

object GovernmentReformParser {

  def apply(
             repos: RepositoryFactory,
             evalEntityFields: Boolean = false
           ): GovernmentReformRepository =
    apply(repos.resources, repos.localisations, repos.governmentReforms, evalEntityFields)

  def apply
  (files: ResourceRepository,
   localisation: LocalisationRepository,
   reformRepo: GovernmentReformRepository,
   evalEntityFields: Boolean): GovernmentReformRepository = {

    val reformFiles = files.getGovernmentReforms
    val reformConfigs = ClausewitzParser.parseFileFieldsAsEntities(reformFiles)
    val reforms = reformConfigs
      .map(parseGovernmenReform)
      .map(localisation.setLocalisation)

    if (evalEntityFields)
      ConfigField.printCaseClass("GovernmentReform", reforms)

    reforms.map(GovernmentReform.fromJson).foreach(reformRepo.create)

    reformRepo
  }

  def parseGovernmenReform(o: ObjectNode): ObjectNode = {
    o
      .getArray("lock_level_when_selected")
      .map(_.toSeq)
      .flatMap(_.lastOption)
      .foreach(n => o.setEx("lock_level_when_selected", n))
    ClausewitzParser.removeEmptyObjects(o)
  }

}
