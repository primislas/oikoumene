package com.lomicron.oikoumene.repository.inmemory.government

import com.lomicron.oikoumene.model.government.Policy
import com.lomicron.oikoumene.repository.api.government.PolicyRepository
import com.lomicron.oikoumene.repository.inmemory.InMemoryEntityRepository

case class InMemoryPolicyRepository()
  extends InMemoryEntityRepository[Policy]
    with PolicyRepository {

  override def setId(entity: Policy, id: String): Policy = entity.copy(id = id)

}
