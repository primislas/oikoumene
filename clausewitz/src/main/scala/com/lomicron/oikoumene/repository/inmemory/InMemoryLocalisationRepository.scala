package com.lomicron.oikoumene.repository.inmemory

import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.oikoumene.model.localisation.LocalisationEntry
import com.lomicron.oikoumene.repository.api.{LocalisationRepository, ResourceRepository}
import com.lomicron.utils.collection.CollectionUtils._
import com.lomicron.utils.json.JsonMapper.{objectNode, patch}

import scala.util.matching.Regex

class InMemoryLocalisationRepository extends LocalisationRepository {

  private var localisation = Map[String, LocalisationEntry]()
  private var tags = Map[String, ObjectNode]()
  private var provinces = Map[Int, ObjectNode]()

  override def findEntry(key: String): Option[LocalisationEntry] =
    localisation.get(key)

  override def searchEntries(keyPattern: String): Seq[LocalisationEntry] =
    localisation.filterKeys(_.matches(keyPattern)).values.toSeq

  override def findTag(key: String): Option[ObjectNode] =
    tags.get(key)

  override def fetchTags: Map[String, ObjectNode] = tags

  override def fetchProvinces: Map[Int, ObjectNode] = provinces

}

object InMemoryLocalisationRepository {

  val tagNamePat: Regex =
    "^(?<tag>[a-zA-Z]{3})$".r
  val tagNameAdjPat: Regex =
    "^(?<tag>[a-zA-Z]{3})_ADJ$".r
  val provNamePat: Regex =
    "^PROV(?<id>\\d+)$".r

  def apply(resourceRepo: ResourceRepository)
  : LocalisationRepository = {
    val repo = new InMemoryLocalisationRepository()

    repo.localisation = resourceRepo
      .getLocalisation(resourceRepo.SupportedLanguages.english)
      .toMapEx(e => e.key -> e)

    repo.tags = repo
      .localisation
      .values
      .flatMap(parseTagName)
      .groupBy(_._1)
      .mapValuesEx(_.map(_._2).reduce(patch))

    repo.provinces = repo
      .localisation
      .values
      .flatMap(parseProvinceName)
      .groupBy(_._1)
      .mapValuesEx(_.map(_._2).reduce(patch))

    repo
  }

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
