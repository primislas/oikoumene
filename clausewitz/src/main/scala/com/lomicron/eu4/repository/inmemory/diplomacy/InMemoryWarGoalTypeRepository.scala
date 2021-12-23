package com.lomicron.eu4.repository.inmemory.diplomacy

import com.lomicron.eu4.model.diplomacy.WarGoalType
import com.lomicron.eu4.repository.api.diplomacy.WarGoalTypeRepository
import com.lomicron.oikoumene.repository.inmemory.InMemoryEntityRepository

case class InMemoryWarGoalTypeRepository()
  extends InMemoryEntityRepository[WarGoalType]
    with WarGoalTypeRepository {

  override def setId(entity: WarGoalType, id: String): WarGoalType = entity.copy(id = id)

}
