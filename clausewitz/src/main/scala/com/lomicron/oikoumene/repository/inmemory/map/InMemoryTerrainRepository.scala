package com.lomicron.oikoumene.repository.inmemory.map

import com.lomicron.oikoumene.repository.api.map.TerrainRepository
import com.lomicron.oikoumene.repository.inmemory.InMemoryObjectNodeRepository

object InMemoryTerrainRepository
  extends InMemoryObjectNodeRepository
    with TerrainRepository {

}
