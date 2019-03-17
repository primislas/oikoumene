package com.lomicron.oikoumene.repository.inmemory.politics

import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.oikoumene.repository.api.politics.{CultureGroupRepository, CultureRepository}
import com.lomicron.oikoumene.repository.inmemory.InMemoryCrudRepository

import scala.util.Try

case class InMemoryCultureRepository(cultureGroups: CultureGroupRepository)
  extends InMemoryCrudRepository[String, ObjectNode](o => o.get("id").asText())
    with CultureRepository {

  override def createGroup(entity: ObjectNode): Try[ObjectNode] = cultureGroups.create(entity)

  override def updateGroup(entity: ObjectNode): Try[ObjectNode] = cultureGroups.update(entity)

  override def findGroup(key: String): Try[ObjectNode] = cultureGroups.find(key)

  override def findAllGroups: Seq[ObjectNode] = cultureGroups.findAll

  override def removeGroup(key: String): Try[ObjectNode] = cultureGroups.remove(key)

}
