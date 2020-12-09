package com.lomicron.oikoumene.parsers.modifiers

import com.lomicron.oikoumene.parsers.government.IdeaParser.parseIdeaGroup
import com.lomicron.oikoumene.parsers.{ClausewitzParser, ConfigField}
import com.lomicron.oikoumene.repository.api.RepositoryFactory
import com.lomicron.utils.json.JsonMapper.{ArrayNodeEx, JsonNodeEx, ObjectNodeEx}

object ModifierAnalyzer {

  def apply(repos: RepositoryFactory): Seq[ConfigField] = {
    val files = repos.resources

    val eventModifiers = ClausewitzParser
      .parseFileFieldsAsEntities(files.getEventModifiers)
    val buildingModifiers = ClausewitzParser
      .parseFileFieldsAsEntities(files.getBuildings)
      .flatMap(_.getObject("modifier"))
    val reformModifiers = ClausewitzParser
      .parseFileFieldsAsEntities(files.getGovernmentReforms)
      .flatMap(_.getObject("modifiers"))
    val ideaModifiers = ClausewitzParser
      .parseFileFieldsAsEntities(files.getIdeas)
      .map(parseIdeaGroup)
      .flatMap(_.getArray("ideas"))
      .flatMap(_.toSeq)
      .flatMap(_.asObject)
      .flatMap(_.getObject("modifiers"))

    val modifiers = eventModifiers ++ buildingModifiers ++ reformModifiers ++ ideaModifiers
    ConfigField.printMapClass("Modifier", modifiers)

    Seq.empty
  }

}
