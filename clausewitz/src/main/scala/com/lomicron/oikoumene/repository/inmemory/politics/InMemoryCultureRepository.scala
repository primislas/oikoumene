package com.lomicron.oikoumene.repository.inmemory.politics

import com.lomicron.oikoumene.model.politics.{Culture, CultureGroup}
import com.lomicron.oikoumene.repository.api.politics.{CultureGroupRepository, CultureRepository}
import com.lomicron.oikoumene.repository.inmemory.InMemoryEntityRepository

import scala.util.Try

case class InMemoryCultureRepository(cultureGroups: CultureGroupRepository)
  extends InMemoryEntityRepository[Culture]
    with CultureRepository {

  override def createGroup(entity: CultureGroup): Try[CultureGroup] = cultureGroups.create(entity)

  override def updateGroup(entity: CultureGroup): Try[CultureGroup] = cultureGroups.update(entity)

  override def findGroup(key: String): Try[CultureGroup] = cultureGroups.find(key)

  override def findAllGroups: Seq[CultureGroup] = cultureGroups.findAll

  override def removeGroup(key: String): Try[CultureGroup] = cultureGroups.remove(key)

}
