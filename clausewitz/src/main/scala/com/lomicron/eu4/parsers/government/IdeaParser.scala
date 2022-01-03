package com.lomicron.eu4.parsers.government

import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.eu4.model.government.IdeaGroup
import com.lomicron.eu4.repository.api.RepositoryFactory
import com.lomicron.eu4.repository.api.government.IdeaGroupRepository
import com.lomicron.eu4.repository.api.resources.ResourceRepository
import com.lomicron.oikoumene.parsers.{ClausewitzParser, ConfigField}
import com.lomicron.oikoumene.repository.api.resources.LocalisationRepository
import com.lomicron.utils.collection.CollectionUtils._
import com.lomicron.utils.json.JsonMapper._

object IdeaParser {

  val ideaGroupFields: Set[String] = Set(
    "id", "localisation", "source_file", "start", "bonus",
    "ideas", "trigger", "free", "ai_will_do", "category"
  )

  def apply(
             repos: RepositoryFactory,
             evalEntityFields: Boolean = false
           ): IdeaGroupRepository =
    apply(repos.resources, repos.localisations, repos.ideas, evalEntityFields)

  def apply
  (files: ResourceRepository,
   localisation: LocalisationRepository,
   ideaRepo: IdeaGroupRepository,
   evalEntityFields: Boolean): IdeaGroupRepository = {

    val configs = files.getIdeas
    val ideaGroups = ClausewitzParser
      .parseFileFieldsAsEntities(configs)
      .map(parseIdeaGroup)
      .map(localisation.setLocalisation)
    val ideas = ideaGroups
      .flatMap(_.getArray("ideas"))
      .flatMap(_.toSeq)
      .flatMap(_.asObject)
      .map(localisation.setLocalisation)

    if (evalEntityFields) {
      val modifiers = ideas.flatMap(_.getObject("modifier"))
      val factors = ideaGroups.flatMap(_.getObject("ai_will_do"))
      val tagConditions = ClausewitzParser.parseNestedConditions(factors)

      ConfigField.printCaseClass("IdeaGroup", ideaGroups)
      ConfigField.printCaseClass("Idea", ideas)
      ConfigField.printCaseClass("TagModifier", modifiers)
      ConfigField.printCaseClass("TagCondition", tagConditions)
    }

    ideaGroups.map(IdeaGroup.fromJson).foreach(ideaRepo.create)
    ideaRepo
  }

  def parseIdeaGroup(ig: ObjectNode): ObjectNode = {
    val ideaFields = ig.fieldNames.toSeq.filterNot(ideaGroupFields.contains)
    val ideas = ideaFields
      .flatMap(i => ig
        .getObject(i)
        .map(modifiers => objectNode.setEx("id", i).setEx("modifier", modifiers))
      )
    ideaFields.foreach(ig.remove)
    ig.setEx("ideas", ideas)
  }


}
