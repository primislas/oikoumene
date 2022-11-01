package com.lomicron.vicky.repository.inmemory

import com.lomicron.oikoumene.repository.inmemory.InMemoryEntityRepository
import com.lomicron.vicky.model.production.Building
import com.lomicron.vicky.repository.api.BuildingRepository

object InMemoryBuildingRepository
  extends InMemoryEntityRepository[Building]
    with BuildingRepository {

  override def setId(entity: Building, id: String): Building =
    entity.copy(id = id)

}
