package com.lomicron.oikoumene.repository.inmemory.map

import com.lomicron.oikoumene.model.provinces.Building
import com.lomicron.oikoumene.repository.api.map.BuildingRepository
import com.lomicron.oikoumene.repository.inmemory.InMemoryEntityRepository

object InMemoryBuildingRepository
  extends InMemoryEntityRepository[Building]
    with BuildingRepository {

}
