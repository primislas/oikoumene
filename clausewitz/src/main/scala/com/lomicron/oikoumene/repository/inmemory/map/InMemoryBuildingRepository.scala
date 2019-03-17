package com.lomicron.oikoumene.repository.inmemory.map

import com.lomicron.oikoumene.repository.api.map.BuildingRepository
import com.lomicron.oikoumene.repository.inmemory.InMemoryObjectNodeRepository

object InMemoryBuildingRepository
  extends InMemoryObjectNodeRepository
    with BuildingRepository {

}
