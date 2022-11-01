package com.lomicron.vicky.repository.inmemory

import com.lomicron.oikoumene.repository.inmemory.InMemoryEntityRepository
import com.lomicron.vicky.model.military.Unit
import com.lomicron.vicky.repository.api.UnitRepository

object InMemoryUnitRepository
  extends InMemoryEntityRepository[Unit]
    with UnitRepository {

  override def setId(entity: Unit, id: String): Unit =
    entity.copy(id = id)

}
