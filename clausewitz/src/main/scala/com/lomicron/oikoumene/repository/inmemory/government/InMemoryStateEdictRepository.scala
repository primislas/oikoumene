package com.lomicron.oikoumene.repository.inmemory.government

import com.lomicron.oikoumene.model.government.StateEdict
import com.lomicron.oikoumene.repository.api.government.StateEdictRepository
import com.lomicron.oikoumene.repository.inmemory.InMemoryEntityRepository

case class InMemoryStateEdictRepository()
  extends InMemoryEntityRepository[StateEdict]
    with StateEdictRepository {

  override def setId(entity: StateEdict, id: String): StateEdict = entity.copy(id = id)

}
