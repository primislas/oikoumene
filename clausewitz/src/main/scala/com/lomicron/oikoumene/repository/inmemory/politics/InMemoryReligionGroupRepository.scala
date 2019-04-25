package com.lomicron.oikoumene.repository.inmemory.politics

import com.lomicron.oikoumene.model.politics.ReligionGroup
import com.lomicron.oikoumene.repository.api.politics.ReligionGroupRepository
import com.lomicron.oikoumene.repository.inmemory.InMemoryEntityRepository

case class InMemoryReligionGroupRepository()
  extends InMemoryEntityRepository[ReligionGroup]
    with ReligionGroupRepository {

  override def setId(entity: ReligionGroup, id: String): ReligionGroup = entity.copy(id = id)

}
