package com.lomicron.oikoumene.repository.inmemory.politics

import com.lomicron.oikoumene.model.politics.{Religion, ReligionGroup}
import com.lomicron.oikoumene.repository.api.politics.{ReligionGroupRepository, ReligionRepository}
import com.lomicron.oikoumene.repository.inmemory.InMemoryEntityRepository

import scala.util.Try

case class InMemoryReligionRepository(religionGroups: ReligionGroupRepository)
  extends InMemoryEntityRepository[Religion]
    with ReligionRepository {

  override def createGroup(entity: ReligionGroup): Try[ReligionGroup] = religionGroups.create(entity)

  override def updateGroup(entity: ReligionGroup): Try[ReligionGroup] = religionGroups.update(entity)

  override def findGroup(key: String): Try[ReligionGroup] = religionGroups.find(key)

  override def findAllGroups: Seq[ReligionGroup] = religionGroups.findAll

  override def removeGroup(key: String): Try[ReligionGroup] = religionGroups.remove(key)

  override def setId(entity: Religion, id: String): Religion = entity.copy(id = id)

}
