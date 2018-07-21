package com.lomicron.oikoumene.repository.inmemory

import java.util.concurrent.ConcurrentHashMap

import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.oikoumene.repository.api.TagRepository

import scala.collection.JavaConverters._
import scala.util.Try

// TODO ObjectNodes are mutable, so the current implementation is not safe
// However it's merely a placeholder until class model is implemented
// instead of ObjectNodes
class InMemoryTagRepository extends TagRepository {

  private val tags = new ConcurrentHashMap[String, ObjectNode]()

  override def create(entity: ObjectNode): Try[ObjectNode] =
    Try(tags.put(entity.get("tag").asText(), entity))

  override def update(entity: ObjectNode): Try[ObjectNode] =
    Try(tags.put(entity.get("tag").asText(), entity))

  override def find(key: String): Try[ObjectNode] =
    Try(tags.get(key))

  override def findAll: Try[Seq[ObjectNode]] =
    Try(tags.values().asScala.toSeq)

  override def remove(key: String): Try[ObjectNode] =
    Try(tags.remove(key))

}
