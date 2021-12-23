package com.lomicron.eu4.repository.inmemory.government

import com.lomicron.eu4.model.government.Policy
import com.lomicron.eu4.repository.api.government.PolicyRepository
import com.lomicron.oikoumene.repository.inmemory.InMemoryEntityRepository

case class InMemoryPolicyRepository()
  extends InMemoryEntityRepository[Policy]
    with PolicyRepository {

  override def setId(entity: Policy, id: String): Policy = entity.copy(id = id)

}
