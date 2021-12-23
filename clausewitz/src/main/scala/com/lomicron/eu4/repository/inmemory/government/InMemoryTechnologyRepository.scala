package com.lomicron.eu4.repository.inmemory.government

import com.lomicron.eu4.model.government.Technology
import com.lomicron.eu4.repository.api.government.{TechnologyGroupRepository, TechnologyRepository}
import com.lomicron.oikoumene.repository.inmemory.InMemoryEntityRepository

case class InMemoryTechnologyRepository(groups: TechnologyGroupRepository)
  extends InMemoryEntityRepository[Technology]
    with TechnologyRepository {

  override def setId(entity: Technology, id: String): Technology = entity.copy(id = id)

}
