package com.lomicron.oikoumene.parsers.localisation

import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.oikoumene.model.localisation.LocalisationEntry
import com.lomicron.oikoumene.repository.api.RepositoryFactory
import com.lomicron.utils.json.JsonMapper.{objectNode, patch}
import com.lomicron.utils.collection.CollectionUtils.MapEx

import scala.util.matching.Regex

object LocalisationParser {

  val tagNamePat: Regex =
    "^(?<tag>[a-zA-Z]{3})$".r
  val tagNameAdjPat: Regex =
    "^(?<tag>[a-zA-Z]{3})_ADJ$".r
  val provNamePat: Regex =
    "^PROV(?<id>\\d+)$".r

  def apply(repos: RepositoryFactory)
  : Seq[LocalisationEntry] = {
    val resources = repos.resources
    val localisationEntries = resources
      .getLocalisation(resources.SupportedLanguages.english)
      .reverse
    repos.localisations.upsertEntries(localisationEntries)
    localisationEntries
  }

  def parseTags(es: Seq[LocalisationEntry]): Map[String, ObjectNode] =
    parseNames(es, parseTagName)

  def parseProvinces(es: Seq[LocalisationEntry]): Map[Int, ObjectNode] =
    parseNames(es, parseProvinceName)

  def parseNames[T]
  (es: Seq[LocalisationEntry], parserF: LocalisationEntry => Option[(T, ObjectNode)])
  : Map[T, ObjectNode] =
    es
      .flatMap(parserF(_))
      .groupBy(_._1)
      .mapValuesEx(_.map(_._2).reduce(patch))

  def parseTagName(entry: LocalisationEntry):
  Option[(String, ObjectNode)] =
    entry.key match {
      case tagNamePat(tag) =>
        Option(tag.toUpperCase, objectNode.put("name", entry.text))
      case tagNameAdjPat(tag) =>
        Option(tag.toUpperCase, objectNode.put("nameAdj", entry.text))
      case _ => Option.empty
    }

  def parseProvinceName(entry: LocalisationEntry):
  Option[(Int, ObjectNode)] =
    entry.key match {
      case provNamePat(id) => Some((id.toInt, objectNode.put("name", entry.text)))
      case _ => None
    }


}
