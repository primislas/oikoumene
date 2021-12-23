package com.lomicron.eu4.repository.api.politics

import com.lomicron.eu4.model.politics.{Culture, CultureGroup}
import com.lomicron.oikoumene.repository.api.AbstractRepository

import scala.util.{Success, Try}

trait CultureRepository extends AbstractRepository[String, Culture] {

  def createGroup(entity: CultureGroup): CultureGroup

  def createGroups(entities: Seq[CultureGroup]): Seq[CultureGroup] =
    entities.map(createGroup)

  def updateGroup(entity: CultureGroup): CultureGroup

  def upsertGroup(entity: CultureGroup): CultureGroup =
    Try(updateGroup(entity)) match {
      case Success(cg) => cg
      case _ => createGroup(entity)
    }

  def findGroup(key: String): Option[CultureGroup]

  def findAllGroups: Seq[CultureGroup]

  def removeGroup(key: String): Option[CultureGroup]

  def removeGroups(keys: Seq[String]): Seq[CultureGroup] =
    keys.flatMap(removeGroup)

  def groupOf(cultureId: String): Option[CultureGroup] =
    findAllGroups.find(_.hasCulture(cultureId))

}
