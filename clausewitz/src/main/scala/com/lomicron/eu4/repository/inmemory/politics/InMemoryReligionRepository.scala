package com.lomicron.eu4.repository.inmemory.politics

import com.lomicron.eu4.model.politics.{Religion, ReligionGroup}
import com.lomicron.eu4.repository.api.politics.{ReligionGroupRepository, ReligionRepository}
import com.lomicron.oikoumene.repository.inmemory.InMemoryEntityRepository

case class InMemoryReligionRepository(religionGroups: ReligionGroupRepository)
  extends InMemoryEntityRepository[Religion]
    with ReligionRepository {

  override def createGroup(entity: ReligionGroup): ReligionGroup = religionGroups.create(entity)

  override def updateGroup(entity: ReligionGroup): ReligionGroup = religionGroups.update(entity)

  override def findGroup(key: String): Option[ReligionGroup] = religionGroups.find(key)

  override def findAllGroups: Seq[ReligionGroup] = religionGroups.findAll

  override def removeGroup(key: String): Option[ReligionGroup] = religionGroups.remove(key)

  override def setId(entity: Religion, id: String): Religion = entity.copy(id = id)

}
