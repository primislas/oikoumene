package com.lomicron.eu4.parsers.government

import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.eu4.model.government.{TechGroup, Technology}
import com.lomicron.eu4.repository.api.RepositoryFactory
import com.lomicron.eu4.repository.api.government.TechnologyRepository
import com.lomicron.oikoumene.parsers.{ClausewitzParser, ConfigField}
import com.lomicron.utils.json.JsonMapper.{JsonNodeEx, ObjectNodeEx, arrayNodeOf, objectNode}
import com.typesafe.scalalogging.LazyLogging

object TechnologyParser extends LazyLogging {

  private val CPFs = ClausewitzParser.Fields

  object Fields {
    val monarchPower = "monarch_power"
    val aheadOfTime = "ahead_of_time"
    val technology = "technology"
    val governments = "governments"
    val buildings = "buildingIds"
    val enable = "enable"
    val all: Set[String] = Set(
      CPFs.idKey, CPFs.localisation, CPFs.sourceFile, CPFs.modifier,
      monarchPower, aheadOfTime, governments, buildings, technology, enable
    )

    val groups = "groups"
  }

  def apply(repos: RepositoryFactory, evalEntityFields: Boolean = false): TechnologyRepository = {
    val files = repos.resources
    val localisation = repos.localisations
    val techRepo = repos.technology
    val buildingIds = repos.buildings.ids
    val govIds = repos.governments.ids

    val techConfigs = files.getTechnologies
    val confJsons = ClausewitzParser
      .parseFilesAsEntities(techConfigs)
      .flatMap(parseTechnologyConfig(_, govIds, buildingIds))
      .map(localisation.setLocalisation)

    if (evalEntityFields)
      ConfigField.printCaseClass("Technology", confJsons)

    confJsons
      .map(Technology.fromJson)
      .foreach(techRepo.create)

    val groupConfig = files
      .getTechGroupConfig
      .map(ClausewitzParser.parseAndLogErrors)
    val groups = groupConfig
      .flatMap(_.getObject(Fields.groups))
      .map(ClausewitzParser.fieldsToObjects(_, CPFs.idKey))
      .getOrElse(Seq.empty)
      .map(localisation.setLocalisation)

    groups.map(TechGroup.fromJson).foreach(techRepo.groups.create)

    if (evalEntityFields)
      ConfigField.printCaseClass("TechGroup", groups)

    // TODO groupConfig["tables"]

    techRepo
  }

  def parseTechnologyConfig(o: ObjectNode, govIds: Set[String], buildingIds: Set[String]): Option[ObjectNode] = {
    val mpOpt = o.getString(Fields.monarchPower)
    if (mpOpt.isEmpty) {
      logger.warn(s"Technology file has no ${Fields.monarchPower} configured: ${o.getString(CPFs.sourceFile)}")
      None
    } else {
      mpOpt
        .map(mp => {
          val techLevels = o
            .getSeqOfObjects(Fields.technology)
            .zipWithIndex
            .map(ti => ti._1.setEx(CPFs.idKey, s"$mp${ti._2}"))
            .map(parseTechBuildings(_, buildingIds))
            .map(parseGovernments(_, govIds))
            .map(parseModifiers)
          o
            .setEx(Fields.technology, arrayNodeOf(techLevels))
            .setEx(CPFs.idKey, mp)
        })
    }
  }

  def parseTechBuildings(o: ObjectNode, buildingIds: Set[String]): ObjectNode =
    parseCategory(o, buildingIds, Fields.buildings)

  def parseGovernments(o: ObjectNode, governmentIds: Set[String]): ObjectNode =
    parseCategory(o, governmentIds, Fields.governments)

  def parseModifiers(o: ObjectNode): ObjectNode = {
    val modifier = o
      .entrySeq()
      .filterNot(e => Fields.all.contains(e.getKey))
      .foldLeft(objectNode)(_ setEx _)
    modifier.fieldSeq().foreach(o.remove)
    o.setEx(CPFs.modifier, modifier)
  }

  def parseCategory(o: ObjectNode, categoryIds: Set[String], categoryKey: String): ObjectNode = {
    val enabledEntities = o
      .entrySeq()
      .filter(e => categoryIds.contains(e.getKey))
      .foldLeft(objectNode)(_ setEx _)
    enabledEntities.fieldSeq().foreach(o.removeEx)
    o.setEx(categoryKey, enabledEntities)
  }

}
