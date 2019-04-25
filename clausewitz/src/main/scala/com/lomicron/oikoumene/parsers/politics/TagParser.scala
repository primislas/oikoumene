package com.lomicron.oikoumene.parsers.politics

import com.fasterxml.jackson.databind.node.{ObjectNode, TextNode}
import com.lomicron.oikoumene.model.politics.Tag
import com.lomicron.oikoumene.parsers.ClausewitzParser.Fields.idKey
import com.lomicron.oikoumene.parsers.ClausewitzParser.{parse, parseDates, parseEvents}
import com.lomicron.oikoumene.parsers.{ClausewitzParser, ConfigField}
import com.lomicron.oikoumene.repository.api.politics.TagRepository
import com.lomicron.oikoumene.repository.api.{LocalisationRepository, RepositoryFactory, ResourceRepository}
import com.lomicron.utils.collection.CollectionUtils._
import com.lomicron.utils.json.JsonMapper
import com.lomicron.utils.json.JsonMapper.{ArrayNodeEx, JsonNodeEx, ObjectNodeEx, patchFieldValue}
import com.typesafe.scalalogging.LazyLogging

import scala.collection.immutable.TreeMap

object TagParser extends LazyLogging {

  def apply(repos: RepositoryFactory): TagRepository =
    apply(repos.resources, repos.localisations, repos.tags)

  def apply
  (files: ResourceRepository,
   localisation: LocalisationRepository,
   tags: TagRepository
  ): TagRepository = {

    val filesByTags = files
      .getCountryTags
      .map(contentsByFile => ClausewitzParser.parse(contentsByFile._2)._1)
      .flatMap(obj => obj.fields.toStream.map(e => (e.getKey, e.getValue.asText)))
      .map(kv => (kv._1, s"common/${kv._2}"))
      .foldLeft(TreeMap[String, String]())(_ + _)
    val countries = files.getCountries(filesByTags)
    val histories = files.getCountryHistory
    val names = localisation.fetchTags
    val parsedTagNodes = TagParser(filesByTags, countries, histories, names)

    val tagEvents = parsedTagNodes.flatMap(_.getArray("history")).flatMap(_.toSeq).flatMap(_.asObject)

    def parseLeaderDates(o: ObjectNode): ObjectNode = o.getObject("leader").map(parseDates).getOrElse(o)

    val monarchs = tagEvents.flatMap(_.getObject("monarch")).map(parseDates).map(parseLeaderDates)
    val queens = tagEvents.flatMap(_.getObject("queen")).map(parseDates)
    val heirs = tagEvents.flatMap(_.getObject("heir")).map(parseDates).map(parseLeaderDates)
    val leaders = tagEvents.flatMap(_.getObject("leader")).map(parseDates)
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
    ConfigField.printCaseClass("Tag", parsedTagNodes)

    parsedTagNodes
      .map(Tag.fromJson)
      .map(_.atStart())
      .foreach(tags.create)

    tags
  }


  def apply
  (tags: Map[String, String],
   countries: Map[String, String],
   histories: Map[String, String],
   names: Map[String, ObjectNode]):
  Seq[ObjectNode] = {

    logger.info("Loading country tags...")

    val countryByTag = parseCountries(tags, countries)
    logger.info(s"Loaded ${countryByTag.size} tag definitions")

    val historyByTag = parseCountryHistories(tags, histories)
    logger.info(s"Loaded ${historyByTag.size} tag histories")

    val parsedTags = countryByTag
      .mapKVtoValue((tag, country) => historyByTag
        .get(tag)
        .map(history => Seq(history) ++ parseEvents(history))
        .map(JsonMapper.arrayNodeOf)
        .map(patchFieldValue(country, "history", _))
        .getOrElse(country))
      .mapKVtoValue((tag, country) => names
        .get(tag)
        .map(patchFieldValue(country, "localisation", _))
        .getOrElse(country))
      .mapValues(ClausewitzParser.parseColor)
      .mapValues(ClausewitzParser.parseColor(_, "revolutionary_colors"))
      .mapKVtoValue((id, tag) => tag.setEx(idKey, TextNode.valueOf(id)))
      .values.toList
    logger.info(s"Loaded ${parsedTags.size} tags")

    parsedTags
  }

  def parseCountries
  (tags: Map[String, String],
   countries: Map[String, String]):
  Map[String, ObjectNode] = {

    def tagToCoutry(tag: String) = {
      countries.get(tag).map(parse)
    }

    tags
      .mapKeyToValue(tagToCoutry)
      .filterKeyValue((tag, opt) => {
        if (opt.isEmpty)
          logger.warn(s"Tag $tag has no country configuration")
        opt.nonEmpty
      })
      .mapValuesEx(_.get)
      .mapKVtoValue((tag, t2) => {
        val errors = t2._2
        if (errors.nonEmpty)
          logger.warn(s"Encountered errors parsing country configuration for tag '$tag': $errors")
        t2._1
      })
  }

  def parseCountryHistories
  (tags: Map[String, String],
   histories: Map[String, String]
  ): Map[String, ObjectNode]
  = tags
    .mapKeyToValue(histories.get(_).map(parse))
    .filterKeyValue((tag, hist) => {
      if (hist.isEmpty)
        logger.warn(s"Tag $tag has no history configuration")
      hist.nonEmpty
    })
    .mapValuesEx(_.get)
    .mapKVtoValue((tag, histAndErrors) => {
      val errors = histAndErrors._2
      if (errors.nonEmpty)
        logger.warn(s"Encountered errors parsing country history for tag '$tag': $errors")
      histAndErrors._1
    })

}