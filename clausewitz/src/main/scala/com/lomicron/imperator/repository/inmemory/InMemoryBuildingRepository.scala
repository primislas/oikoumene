package com.lomicron.imperator.repository.inmemory

import com.lomicron.imperator.model.provinces.Building
import com.lomicron.imperator.repository.api.BuildingRepository
import com.lomicron.oikoumene.repository.inmemory.InMemoryEntityRepository

object InMemoryBuildingRepository
  extends InMemoryEntityRepository[Building]
    with BuildingRepository {
  override def setId(entity: Building, id: String): Building = entity.copy(id = id)
}

