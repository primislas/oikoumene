package com.lomicron.oikoumene.repository.api

import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.oikoumene.model.localisation.LocalisationEntry

trait LocalisationRepository {

  def findEntry(key: String): Option[LocalisationEntry]

  def findTag(key: String): Option[ObjectNode]

  def fetchTags: Map[String, ObjectNode]


}
