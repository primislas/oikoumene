package com.lomicron.oikoumene.repository.inmemory.map

import com.lomicron.oikoumene.repository.api.map.ClimateRepository
import com.lomicron.oikoumene.repository.inmemory.InMemoryObjectNodeRepository

object InMemoryClimateRepository
  extends InMemoryObjectNodeRepository
    with ClimateRepository {

}
