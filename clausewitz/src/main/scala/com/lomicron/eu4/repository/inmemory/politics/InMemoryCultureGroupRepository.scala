package com.lomicron.eu4.repository.inmemory.politics

import com.lomicron.eu4.model.politics.CultureGroup
import com.lomicron.eu4.repository.api.politics.CultureGroupRepository
import com.lomicron.oikoumene.repository.inmemory.InMemoryEntityRepository

case class InMemoryCultureGroupRepository()
  extends InMemoryEntityRepository[CultureGroup]
    with CultureGroupRepository {

  override def setId(entity: CultureGroup, id: String): CultureGroup = entity.copy(id = id)

}
