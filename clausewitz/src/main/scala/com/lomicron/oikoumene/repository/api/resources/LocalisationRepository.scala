package com.lomicron.oikoumene.repository.api.resources

import com.fasterxml.jackson.databind.node.{ObjectNode, TextNode}
import com.lomicron.oikoumene.model.localisation.LocalisationEntry
import com.lomicron.utils.json.JsonMapper
import com.lomicron.utils.json.JsonMapper.patchFieldValue

trait LocalisationRepository {

  def upsertEntries(es: Seq[LocalisationEntry]): LocalisationRepository

  def findEntry(key: String): Option[LocalisationEntry]

  def fetchAllEntries: Seq[LocalisationEntry]

  def searchEntries(keyPattern: String): Seq[LocalisationEntry]

  def findTag(key: String): Option[ObjectNode]

  def fetchTags: Map[String, ObjectNode]

  def fetchProvinces: Map[Int, ObjectNode]

  def findAndSetAsLocName(key: String, target: ObjectNode): ObjectNode =
    findEntry(key)
      .map(_.text)
      .map(TextNode.valueOf)
      .map(name => {
        if (target.has("localisation"))
          patchFieldValue(target.get("localisation").asInstanceOf[ObjectNode], "name", name)
        else {
          val l = patchFieldValue(JsonMapper.objectNode, "name", name)
          target.set("localisation", l)
        }
        target
      })
      .getOrElse(target)


}
