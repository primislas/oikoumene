package com.lomicron.eu4.repository.inmemory.politics

import com.lomicron.eu4.model.politics.{Culture, CultureGroup}
import com.lomicron.eu4.repository.api.politics.{CultureGroupRepository, CultureRepository}
import com.lomicron.oikoumene.repository.inmemory.InMemoryEntityRepository

case class InMemoryCultureRepository(cultureGroups: CultureGroupRepository)
  extends InMemoryEntityRepository[Culture]
    with CultureRepository {

  override def createGroup(entity: CultureGroup): CultureGroup = cultureGroups.create(entity)

  override def updateGroup(entity: CultureGroup): CultureGroup = cultureGroups.update(entity)

  override def findGroup(key: String): Option[CultureGroup] = cultureGroups.find(key)

  override def findAllGroups: Seq[CultureGroup] = cultureGroups.findAll

  override def removeGroup(key: String): Option[CultureGroup] = cultureGroups.remove(key)

  override def setId(entity: Culture, id: String): Culture = entity.copy(id = id)

}
