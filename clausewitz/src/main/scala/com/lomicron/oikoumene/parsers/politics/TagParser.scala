package com.lomicron.oikoumene.parsers.politics

import com.fasterxml.jackson.databind.node.{ObjectNode, TextNode}
import com.lomicron.oikoumene.parsers.ClausewitzParser
import com.lomicron.oikoumene.parsers.ClausewitzParser.Fields.idKey
import com.lomicron.oikoumene.parsers.ClausewitzParser.{parse, rollUpEvents}
import com.lomicron.oikoumene.repository.api.politics.TagRepository
import com.lomicron.oikoumene.repository.api.{LocalisationRepository, RepositoryFactory, ResourceRepository}
import com.lomicron.utils.collection.CollectionUtils._
import com.lomicron.utils.json.JsonMapper.patchFieldValue
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
    val parsedTags = TagParser(filesByTags, countries, histories, names)
      .mapKVtoValue((id, tag) => tag.set(idKey, TextNode.valueOf(id)).asInstanceOf[ObjectNode])
    tags.create(parsedTags.values.to[Seq])
    tags
  }


  def apply
  (tags: Map[String, String],
   countries: Map[String, String],
   histories: Map[String, String],
   names: Map[String, ObjectNode]):
  Map[String, ObjectNode] = {

    logger.info("Loading country tags...")

    val countryByTag = parseCountries(tags, countries)
    logger.info(s"Loaded ${countryByTag.size} tag definitions")

    val historyByTag = parseCountryHistories(tags, histories)
    logger.info(s"Loaded ${historyByTag.size} tag histories")

    val parsedTags = countryByTag
      .mapKVtoValue((tag, country) => historyByTag
        .get(tag)
        .map(patchFieldValue(country, "history", _))
        .getOrElse(country))
      .mapKVtoValue((tag, country) => names
        .get(tag)
        .map(patchFieldValue(country, "localisation", _))
        .getOrElse(country))
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
      .mapValuesEx(rollUpEvents)

}
