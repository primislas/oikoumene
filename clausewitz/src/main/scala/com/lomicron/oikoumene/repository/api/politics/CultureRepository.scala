package com.lomicron.oikoumene.repository.api.politics

import com.lomicron.oikoumene.model.politics.{Culture, CultureGroup}
import com.lomicron.oikoumene.repository.api.AbstractRepository

import scala.util.{Success, Try}

trait CultureRepository extends AbstractRepository[String, Culture] {

  def createGroup(entity: CultureGroup): Try[CultureGroup]

  def createGroups(entities: Seq[CultureGroup]): Seq[Try[CultureGroup]] =
    entities.map(createGroup)

  def updateGroup(entity: CultureGroup): Try[CultureGroup]

  def upsertGroup(entity: CultureGroup): Try[CultureGroup] =
    updateGroup(entity) match {
      case s: Success[CultureGroup] => s
      case _ => createGroup(entity)
    }

  def findGroup(key: String): Try[CultureGroup]

  def findAllGroups: Seq[CultureGroup]

  def removeGroup(key: String): Try[CultureGroup]

  def removeGroups(keys: Seq[String]): Seq[Try[CultureGroup]] =
    keys.map(removeGroup)

  def groupOf(cultureId: String): Option[CultureGroup] =
    findAllGroups.find(_.hasCulture(cultureId))

}
