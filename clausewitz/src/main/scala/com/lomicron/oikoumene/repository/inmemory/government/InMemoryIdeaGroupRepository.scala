package com.lomicron.oikoumene.repository.inmemory.government

import com.lomicron.oikoumene.model.government.IdeaGroup
import com.lomicron.oikoumene.repository.api.government.IdeaGroupRepository
import com.lomicron.oikoumene.repository.inmemory.InMemoryEntityRepository

case class InMemoryIdeaGroupRepository()
  extends InMemoryEntityRepository[IdeaGroup]
    with IdeaGroupRepository {

  override def setId(entity: IdeaGroup, id: String): IdeaGroup = entity.copy(id = id)

}
