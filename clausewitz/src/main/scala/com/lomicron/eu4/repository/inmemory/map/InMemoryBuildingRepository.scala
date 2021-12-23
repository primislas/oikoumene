package com.lomicron.eu4.repository.inmemory.map

import com.lomicron.eu4.model.provinces.Building
import com.lomicron.eu4.repository.api.map.BuildingRepository
import com.lomicron.oikoumene.repository.inmemory.InMemoryEntityRepository

object InMemoryBuildingRepository
  extends InMemoryEntityRepository[Building]
    with BuildingRepository {
  override def setId(entity: Building, id: String): Building = entity.copy(id = id)
}
