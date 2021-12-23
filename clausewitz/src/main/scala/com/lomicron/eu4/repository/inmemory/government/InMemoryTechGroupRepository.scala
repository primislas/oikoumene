package com.lomicron.eu4.repository.inmemory.government

import com.lomicron.eu4.model.government.TechGroup
import com.lomicron.eu4.repository.api.government.TechnologyGroupRepository
import com.lomicron.oikoumene.repository.inmemory.InMemoryEntityRepository

case class InMemoryTechGroupRepository()
  extends InMemoryEntityRepository[TechGroup]
    with TechnologyGroupRepository {

  override def setId(entity: TechGroup, id: String): TechGroup = entity.copy(id = id)

}
