package com.lomicron.oikoumene.repository.inmemory.politics

import com.lomicron.oikoumene.model.politics.CultureGroup
import com.lomicron.oikoumene.repository.api.politics.CultureGroupRepository
import com.lomicron.oikoumene.repository.inmemory.InMemoryEntityRepository

case class InMemoryCultureGroupRepository()
  extends InMemoryEntityRepository[CultureGroup]
    with CultureGroupRepository {

}
