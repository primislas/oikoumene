package com.lomicron.eu4.repository.inmemory.diplomacy

import com.lomicron.eu4.model.diplomacy.War
import com.lomicron.eu4.repository.api.diplomacy.WarHistoryRepository
import com.lomicron.oikoumene.repository.inmemory.InMemoryIntRepository

case class InMemoryWarRepository()
  extends InMemoryIntRepository[War](_.id)
    with WarHistoryRepository {

  override def setId(entity: War, id: Int): War = entity.copy(id = Option(id))

}
