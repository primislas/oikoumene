package com.lomicron.oikoumene.parsers

import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.oikoumene.parsers.ClausewitzParser.{parse, rollUpEvents}
import com.lomicron.utils.collection.CollectionUtils._
import com.lomicron.utils.json.JsonMapper.{mergeFieldValue, objectNode, patch}
import com.typesafe.scalalogging.LazyLogging

import scala.util.matching.Regex

object TagParser extends LazyLogging {

  val tagNamePat: Regex =
    "^\\s*(?<tag>[a-zA-Z]{3}):(?<version>\\d+)\\s*\"(?<name>.*)\"".r
  val tagNameAdjPat: Regex =
    "^\\s*(?<tag>[a-zA-Z]{3})_ADJ:(?<version>\\d+)\\s*\"(?<name>.*)\"".r

  def apply(tags: Map[String, String],
            countries: Map[String, String],
            histories: Map[String, String],
            names: Map[String, String])
  : Map[String, ObjectNode] = {

    logger.info("Loading country tags...")
    val countryByTag = parseCountries(tags, countries)
    logger.info(s"Loaded ${countryByTag.size} tag definitions")
    val nameByTag = parseTagNames(tags, names)
    logger.info(s"Loaded ${nameByTag.size} tag localisations")
    val historyByTag = parseCountryHistories(tags, histories)
    logger.info(s"Loaded ${historyByTag.size} tag histories")
    val parsedTags = countryByTag
      .mapKVtoValue((tag, country) => historyByTag
        .get(tag)
        .map(mergeFieldValue(country, "history", _))
        .getOrElse(country))
      .mapKVtoValue((tag, country) => nameByTag
        .get(tag)
        .map(mergeFieldValue(country, "localisation", _))
        .getOrElse(country))
    logger.info(s"Loaded ${parsedTags.size} tags")

    parsedTags
  }

  def parseCountries(tags: Map[String, String],
                     countries: Map[String, String]):
  Map[String, ObjectNode] = {

    def tagToCoutry(tag: String) =
      countries.get(tag).map(parse)

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

  def parseCountryHistories(tags: Map[String, String],
                            histories: Map[String, String]):
  Map[String, ObjectNode] =
    tags
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
        histAndErrors._1.put("tag", tag)
      })
      .mapValuesEx(rollUpEvents)

  def parseTagNames(tags: Map[String, String],
                    names: Map[String, String]):
  Map[String, ObjectNode] =
    names
      .values
      .flatMap(parseNames)
      .toMap

  def parseNames(locFile: String): Seq[(String, ObjectNode)] =
    locFile
      .lines
      .toStream
      .flatMap(parseName)
      .groupBy(_._1)
      .mapValuesEx(_.map(_._2).reduce(patch))
      .toSeq

  def parseName(line: String): Option[(String, ObjectNode)] =
    line match {
      case tagNamePat(tag, _, name) =>
        Option(tag, objectNode.put("name", name))
      case tagNameAdjPat(tag, _, adj) =>
        Option(tag, objectNode.put("nameAdj", adj))
      case _ => Option.empty
    }

}
