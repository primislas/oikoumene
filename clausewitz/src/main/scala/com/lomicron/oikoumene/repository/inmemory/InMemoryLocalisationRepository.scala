package com.lomicron.oikoumene.repository.inmemory

import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.oikoumene.model.localisation.LocalisationEntry
import com.lomicron.oikoumene.parsers.localisation.LocalisationParser
import com.lomicron.oikoumene.repository.api.resources.LocalisationRepository

case class InMemoryLocalisationRepository() extends LocalisationRepository { self =>

  private var localisation = Map[String, LocalisationEntry]()
  private var tags = Map[String, ObjectNode]()
  private var provinces = Map[Int, ObjectNode]()

  override def upsertEntries(es: Seq[LocalisationEntry]): LocalisationRepository = {
    localisation = localisation ++ es.map(e => e.key -> e)
    tags = tags ++ LocalisationParser.parseTags(es)
    provinces = provinces ++ LocalisationParser.parseProvinces(es)
    self
  }

  override def findEntry(key: String): Option[LocalisationEntry] =
    localisation.get(key)

  def fetchAllEntries: Seq[LocalisationEntry] =
    localisation.values.toSeq

  override def searchEntries(keyPattern: String): Seq[LocalisationEntry] =
    localisation.view.filterKeys(_.matches(keyPattern)).values.toSeq

  override def findTag(key: String): Option[ObjectNode] =
    tags.get(key)

  override def fetchTags: Map[String, ObjectNode] = tags

  override def fetchProvince(provId: Int): Option[ObjectNode] =
    provinces.get(provId)

  override def fetchProvinces: Map[Int, ObjectNode] = provinces

}
