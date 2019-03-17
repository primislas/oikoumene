package com.lomicron.oikoumene.repository.inmemory.politics

import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.oikoumene.repository.api.politics.{ReligionGroupRepository, ReligionRepository}
import com.lomicron.oikoumene.repository.inmemory.InMemoryCrudRepository

import scala.util.Try

case class InMemoryReligionRepository(religionGroups: ReligionGroupRepository)
  extends InMemoryCrudRepository[String, ObjectNode](o => o.get("id").asText())
    with ReligionRepository {

  override def createGroup(entity: ObjectNode): Try[ObjectNode] = religionGroups.create(entity)

  override def updateGroup(entity: ObjectNode): Try[ObjectNode] = religionGroups.update(entity)

  override def findGroup(key: String): Try[ObjectNode] = religionGroups.find(key)

  override def findAllGroups: Seq[ObjectNode] = religionGroups.findAll

  override def removeGroup(key: String): Try[ObjectNode] = religionGroups.remove(key)
}
