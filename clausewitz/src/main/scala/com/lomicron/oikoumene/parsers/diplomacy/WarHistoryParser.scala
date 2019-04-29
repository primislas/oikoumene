package com.lomicron.oikoumene.parsers.diplomacy

import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.oikoumene.model.diplomacy.War
import com.lomicron.oikoumene.parsers.{ClausewitzParser, ConfigField}
import com.lomicron.oikoumene.repository.api.politics.WarHistoryRepository
import com.lomicron.oikoumene.repository.api.{RepositoryFactory, ResourceRepository}
import com.lomicron.utils.json.JsonMapper._
import com.typesafe.scalalogging.LazyLogging

object WarHistoryParser extends LazyLogging {

  def apply(repos: RepositoryFactory): WarHistoryRepository = apply(repos.resources, repos.warHistory)

  def apply
  (files: ResourceRepository,
   warRepo: WarHistoryRepository
  ): WarHistoryRepository = {

    val wars = ClausewitzParser
      .parseFilesAsEntities(files.getWarGoalTypes)
      .map(parseWarConfig)

    val events = wars.flatMap(_.getArray("events")).flatMap(_.toSeq).flatMap(_.asObject)
    val battles = events.flatMap(_.getObject("battle"))
    val armies = battles.flatMap(_.getObject("attacker")) ++ battles.flatMap(_.getObject("defender"))
    val warGoals = wars.flatMap(_.getObject("war_goal"))
    ConfigField.printCaseClass("War", wars)
    ConfigField.printCaseClass("WarGoal", warGoals)
    ConfigField.printCaseClass("WarEvent", events)
    ConfigField.printCaseClass("Battle", battles)
    ConfigField.printCaseClass("Army", armies)

    val parsedWars = wars.map(War.fromJson)

    warRepo
  }

  def parseWarConfig(config: ObjectNode): ObjectNode = {
    val events = ClausewitzParser.parseEvents(config)
    config.setEx("events", arrayNodeOf(events))
  }

}
