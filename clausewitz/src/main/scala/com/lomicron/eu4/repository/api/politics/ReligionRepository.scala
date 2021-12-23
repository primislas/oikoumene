package com.lomicron.eu4.repository.api.politics

import com.lomicron.eu4.model.politics.{Religion, ReligionGroup}
import com.lomicron.oikoumene.repository.api.AbstractRepository

import scala.util.{Success, Try}

trait ReligionRepository extends AbstractRepository[String, Religion] {

  def createGroup(entity: ReligionGroup): ReligionGroup

  def createGroups(entities: Seq[ReligionGroup]): Seq[ReligionGroup] =
    entities.map(createGroup)

  def updateGroup(entity: ReligionGroup): ReligionGroup

  def upsertGroup(entity: ReligionGroup): ReligionGroup =
    Try(updateGroup(entity)) match {
      case Success(rg) => rg
      case _ => createGroup(entity)
    }

  def findGroup(key: String): Option[ReligionGroup]

  def findAllGroups: Seq[ReligionGroup]

  def removeGroup(key: String): Option[ReligionGroup]

  def removeGroups(keys: Seq[String]): Seq[ReligionGroup] =
    keys.flatMap(removeGroup)

  def groupOf(religionId: String): Option[ReligionGroup] =
    findAllGroups.find(_.hasReligion(religionId))

}
