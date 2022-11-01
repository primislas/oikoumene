package com.lomicron.vicky.parsers.politics

import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.oikoumene.model.politics.Tag
import com.lomicron.oikoumene.parsers.ClausewitzParser.{Fields, parse}
import com.lomicron.oikoumene.parsers.politics.TagFileConf
import com.lomicron.oikoumene.parsers.{ClausewitzParser, ConfigField}
import com.lomicron.oikoumene.repository.api.politics.TagRepository
import com.lomicron.oikoumene.repository.api.resources.{GameFile, LocalisationRepository}
import com.lomicron.utils.collection.CollectionUtils._
import com.lomicron.utils.json.JsonMapper
import com.lomicron.utils.json.JsonMapper.{JsonNodeEx, ObjectNodeEx, textNode}
import com.lomicron.vicky.repository.api.{RepositoryFactory, ResourceRepository}
import com.typesafe.scalalogging.LazyLogging

import java.nio.file.Paths
import scala.collection.immutable.TreeMap
import scala.collection.parallel.CollectionConverters.seqIsParallelizable

object TagParser extends LazyLogging {

  def apply(repos: RepositoryFactory,
            evalEntityFields: Boolean = false)
  : TagRepository =
    apply(repos.resources, repos.localisations, repos.tags, evalEntityFields)

  def apply
  (files: ResourceRepository,
   localisation: LocalisationRepository,
   tags: TagRepository,
   evalEntityFields: Boolean
  ): TagRepository = {

    val filesByTags = parseTagFileMapping(files)
    val tagConfs = files.getCountryConfigs(filesByTags)
    val parsedTagNodes = tagConfs.par.map(parseTag(_, files, localisation)).to(Seq)

    if (evalEntityFields) printClassDefinitions(parsedTagNodes)

    parsedTagNodes
      .par
      .map(Tag.fromJson)
      .map(_.atStart)
      .to(Seq)
      .foreach(tags.create)

    tags
  }

  def parseTagFileMapping(files: ResourceRepository): Map[String, GameFile] =
    files
      .getCountryTags
      .flatMap(fc => fc.content.map(ClausewitzParser.parse).map(_._1))
      .flatMap(obj => obj.fields.toStream.filter(_.getValue.isTextual).map(e => (e.getKey, e.getValue.asText)))
      .map(kv => (kv._1, historyGameFile(kv._2)))
      .foldLeft(TreeMap[String, GameFile]())(_ + _)

  private def historyGameFile(path: String): GameFile = {
    val commonPath = Paths.get(s"common/$path")
    val relDir = commonPath.getParent.toString
    GameFile.of(commonPath, relDir)
  }

  def parseTag(conf: TagFileConf, resources: ResourceRepository, localisation: LocalisationRepository): ObjectNode = {
    val country = parseCountryConf(conf, resources)
    val history = parseCountryHistory(conf, resources)
    history.foreach(country.setEx(Fields.history, _))
    val mergedTagConf = localisation.findAndSetAsLocName(conf.tag, country)

    mergedTagConf
  }

  def parseCountryConf(conf: TagFileConf, resources: ResourceRepository): ObjectNode = {
    if (conf.country.isEmpty) logger.warn(s"Tag ${conf.tag} has no country configuration")
    conf
      .country
      .map(resources.getResource)
      .flatMap(fc => fc.content.flatMap(parse(_)))
      .map(parsingRes => {
        val errors = parsingRes._2
        if (errors.nonEmpty)
          logger.warn(s"Encountered errors parsing country configuration for tag '${conf.tag}': $errors")
        parsingRes._1
      })
      .map(o => o.setEx(Fields.idKey, textNode(conf.tag)))
      .map(ClausewitzParser.removeEmptyObjects)
      .getOrElse(JsonMapper.objectNode)
  }

  def parseCountryHistory(conf: TagFileConf, resources: ResourceRepository): Option[ObjectNode] =
    conf
      .history
      .map(resources.getResource)
      .flatMap(parseCountryHistory)

  def parseCountryHistory(fc: GameFile): Option[ObjectNode] = {
    fc
      .content
      .map(c => {
        val fname = fc.name
        val (unparsedHist, errors) = parse(c)
        if (errors.nonEmpty)
          logger.warn(s"Encountered errors parsing country history '$fname': $errors")
        ClausewitzParser.parseHistory(unparsedHist, fname)
      })
  }

  def printClassDefinitions(tags: Seq[ObjectNode]): Unit = {
    val histories = tags.flatMap(_.getSeq("history")).flatMap(_.asObject)
    val tagEvents = histories.flatMap(_.getObject("init"))
      .concat(histories.flatMap(_.getSeq("events")).flatMap(_.asObject))
    val parties = tags.flatMap(_.getSeq("party")).flatMap(_.asObject)
    ConfigField.printCaseClass("TagUpdate", tagEvents)
    ConfigField.printCaseClass("Party", parties)
    ConfigField.printCaseClass("Tag", tags)
  }

}
