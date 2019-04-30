package com.lomicron.oikoumene.repository.inmemory.diplomacy

import com.lomicron.oikoumene.model.diplomacy.War
import com.lomicron.oikoumene.repository.api.diplomacy.WarHistoryRepository
import com.lomicron.oikoumene.repository.inmemory.InMemoryIntRepository

case class InMemoryWarRepository()
  extends InMemoryIntRepository[War](_.id)
    with WarHistoryRepository {

  override def setId(entity: War, id: Int): War = entity.copy(id = Option(id))

}
