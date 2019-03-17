package com.lomicron.oikoumene.repository.api.politics

import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.oikoumene.repository.api.AbstractObjectNodeRepository

import scala.util.{Success, Try}

trait CultureRepository extends AbstractObjectNodeRepository {

  def createGroup(entity: ObjectNode): Try[ObjectNode]

  def createGroups(entities: Seq[ObjectNode]): Seq[Try[ObjectNode]] =
    entities.map(createGroup)

  def updateGroup(entity: ObjectNode): Try[ObjectNode]

  def upsertGroup(entity: ObjectNode): Try[ObjectNode] =
    updateGroup(entity) match {
      case s: Success[ObjectNode] => s
      case _ => createGroup(entity)
    }

  def findGroup(key: String): Try[ObjectNode]

  def findAllGroups: Seq[ObjectNode]

  def removeGroup(key: String): Try[ObjectNode]

  def removeGroups(keys: Seq[String]): Seq[Try[ObjectNode]] =
    keys.map(removeGroup)

}
