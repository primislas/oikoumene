package com.lomicron.oikoumene.repository.api.politics

import com.lomicron.oikoumene.model.politics.{Religion, ReligionGroup}
import com.lomicron.oikoumene.repository.api.AbstractRepository

import scala.util.{Success, Try}

trait ReligionRepository extends AbstractRepository[String, Religion] {

  def createGroup(entity: ReligionGroup): Try[ReligionGroup]

  def createGroups(entities: Seq[ReligionGroup]): Seq[Try[ReligionGroup]] =
    entities.map(createGroup)

  def updateGroup(entity: ReligionGroup): Try[ReligionGroup]

  def upsertGroup(entity: ReligionGroup): Try[ReligionGroup] =
    updateGroup(entity) match {
      case s: Success[ReligionGroup] => s
      case _ => createGroup(entity)
    }

  def findGroup(key: String): Try[ReligionGroup]

  def findAllGroups: Seq[ReligionGroup]

  def removeGroup(key: String): Try[ReligionGroup]

  def removeGroups(keys: Seq[String]): Seq[Try[ReligionGroup]] =
    keys.map(removeGroup)

}
