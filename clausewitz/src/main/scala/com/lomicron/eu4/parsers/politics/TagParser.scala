package com.lomicron.eu4.parsers.politics

import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.eu4.model.politics.Tag
import com.lomicron.eu4.repository.api.RepositoryFactory
import com.lomicron.eu4.repository.api.politics.TagRepository
import com.lomicron.eu4.repository.api.resources.ResourceRepository
import com.lomicron.oikoumene.parsers.ClausewitzParser.{Fields, parse, parseAndLogErrors}
import com.lomicron.oikoumene.parsers.politics.TagConf
import com.lomicron.oikoumene.parsers.{ClausewitzParser, ConfigField}
import com.lomicron.oikoumene.repository.api.resources.{GameFile, LocalisationRepository}
import com.lomicron.utils.collection.CollectionUtils._
import com.lomicron.utils.json.JsonMapper
import com.lomicron.utils.json.JsonMapper.{ArrayNodeEx, JsonNodeEx, ObjectNodeEx, textNode}
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
      .flatMap(_.atStart)
      .to(Seq)
      .foreach(tags.create)

    tags
  }

  def parseTagFileMapping(files: ResourceRepository): Map[String, GameFile] =
    files
      .getCountryTags
      .flatMap(fc => fc.content.map(ClausewitzParser.parse).map(_._1))
      .flatMap(obj => obj.fields.toStream.map(e => (e.getKey, e.getValue.asText)))
      .map(kv => (kv._1, historyGameFile(kv._2)))
      .foldLeft(TreeMap[String, GameFile]())(_ + _)

  private def historyGameFile(path: String): GameFile = {
    val commonPath = Paths.get(s"common/$path")
    val relDir = commonPath.getParent.toString
    GameFile.of(commonPath, relDir)
  }

  def parseTag(conf: TagConf, resources: ResourceRepository, localisation: LocalisationRepository): ObjectNode = {
    val country = parseCountryConf(conf, resources)
    val history = parseCountryHistory(conf, resources)
    history.foreach(country.setEx(Fields.history, _))
    val mergedTagConf = localisation.findAndSetAsLocName(conf.tag, country)

    mergedTagConf
  }

  def parseCountryConf(conf: TagConf, resources: ResourceRepository): ObjectNode = {
    if (conf.country.isEmpty) logger.warn(s"Tag ${conf.tag} has no country configuration")
    conf
      .country
      .map(resources.getResource)
      .flatMap(fc => fc.content.flatMap(parseAndLogErrors))
      .map(o => o.setEx(Fields.idKey, textNode(conf.tag)))
      .map(ClausewitzParser.removeEmptyObjects)
      .getOrElse(JsonMapper.objectNode)
  }

  def parseCountryHistory(conf: TagConf, resources: ResourceRepository): Option[ObjectNode] =
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
    val tagEvents = tags.flatMap(_.getArray("history")).flatMap(_.toSeq).flatMap(_.asObject)
    val monarchs = tagEvents.flatMap(_.getObject("monarch"))
    val queens = tagEvents.flatMap(_.getObject("queen"))
    val heirs = tagEvents.flatMap(_.getObject("heir"))
    val leaders = tagEvents.flatMap(_.getObject("leader"))
    val countryModifiers = tagEvents.flatMap(_.getObject("add_country_modifier"))
    val rulerModifiers = tagEvents.flatMap(_.getObject("add_ruler_modifier"))
    val priceModifiers = tagEvents.flatMap(_.getObject("change_price"))
    ConfigField.printCaseClass("TagUpdate", tagEvents)
    ConfigField.printCaseClass("Monarch", monarchs)
    ConfigField.printCaseClass("Queen", queens)
    ConfigField.printCaseClass("Heir", heirs)
    ConfigField.printCaseClass("Leader", leaders)
    ConfigField.printCaseClass("CountryModifier", countryModifiers)
    ConfigField.printCaseClass("RulerModifier", rulerModifiers)
    ConfigField.printCaseClass("PriceModifier", priceModifiers)
    ConfigField.printCaseClass("Tag", tags)
  }

}
