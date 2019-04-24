package com.lomicron.oikoumene.repository.inmemory.map

import com.lomicron.oikoumene.model.provinces.Climate
import com.lomicron.oikoumene.repository.api.map.ClimateRepository
import com.lomicron.oikoumene.repository.inmemory.InMemoryEntityRepository

object InMemoryClimateRepository
  extends InMemoryEntityRepository[Climate]
    with ClimateRepository {

}
