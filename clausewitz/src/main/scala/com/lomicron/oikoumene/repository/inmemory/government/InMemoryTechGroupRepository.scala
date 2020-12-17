package com.lomicron.oikoumene.repository.inmemory.government

import com.lomicron.oikoumene.model.government.TechGroup
import com.lomicron.oikoumene.repository.api.government.TechnologyGroupRepository
import com.lomicron.oikoumene.repository.inmemory.InMemoryEntityRepository

case class InMemoryTechGroupRepository()
  extends InMemoryEntityRepository[TechGroup]
    with TechnologyGroupRepository {

  override def setId(entity: TechGroup, id: String): TechGroup = entity.copy(id = id)

}
