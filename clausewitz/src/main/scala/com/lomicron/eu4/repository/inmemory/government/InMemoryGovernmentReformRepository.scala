package com.lomicron.eu4.repository.inmemory.government

import com.lomicron.eu4.model.government.GovernmentReform
import com.lomicron.eu4.repository.api.government.GovernmentReformRepository
import com.lomicron.oikoumene.repository.inmemory.InMemoryEntityRepository

case class InMemoryGovernmentReformRepository()
  extends InMemoryEntityRepository[GovernmentReform]
    with GovernmentReformRepository {

  override def setId(entity: GovernmentReform, id: String): GovernmentReform = entity.copy(id = id)

}
