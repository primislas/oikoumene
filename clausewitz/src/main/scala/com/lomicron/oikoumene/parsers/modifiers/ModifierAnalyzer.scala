package com.lomicron.oikoumene.parsers.modifiers

import com.lomicron.oikoumene.parsers.ClausewitzParser.Fields
import com.lomicron.oikoumene.parsers.government.IdeaParser.parseIdeaGroup
import com.lomicron.oikoumene.parsers.provinces.TerrainParser
import com.lomicron.oikoumene.parsers.{ClausewitzParser, ConfigField}
import com.lomicron.oikoumene.repository.api.RepositoryFactory
import com.lomicron.utils.json.JsonMapper.{ArrayNodeEx, JsonNodeEx, ObjectNodeEx}
import com.lomicron.utils.parsing.JsonParser

object ModifierAnalyzer {

  def apply(repos: RepositoryFactory): Seq[ConfigField] = {
    val files = repos.resources

    val events = ClausewitzParser
      .parseFileFieldsAsEntities(files.getEventModifiers)
    val static = ClausewitzParser
      .parseFileFieldsAsEntities(files.getStaticModifiers)
    val terrain = TerrainParser(repos).terrain.findAll.flatMap(_.modifier).map(_.conf)
    val buildings = ClausewitzParser
      .parseFileFieldsAsEntities(files.getBuildings)
      .flatMap(_.getObject("modifier"))
    val reforms = ClausewitzParser
      .parseFileFieldsAsEntities(files.getGovernmentReforms)
      .flatMap(_.getObject("modifiers"))
    val ideas = ClausewitzParser
      .parseFileFieldsAsEntities(files.getIdeas)
      .map(parseIdeaGroup)
      .flatMap(_.getArray("ideas"))
      .flatMap(_.toSeq)
      .flatMap(_.asObject)
      .flatMap(_.getObject("modifiers"))

    val modifiers = events ++ static ++ buildings ++ reforms ++ ideas ++ terrain
//    ConfigField.printMapClass("Modifier", modifiers)
//
    val staticIds = static.flatMap(_.getString(Fields.idKey)).toSet
//    staticIds.foreach(id => println(s"""  def ${JsonParser.camelCase(id)}: Option[Modifier] = find("$id").toOption"""))
    staticIds.foreach(id => println(s"""  val ${JsonParser.camelCase(id)}: String = "$id""""))

    Seq.empty
  }

}
