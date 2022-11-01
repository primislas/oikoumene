package com.lomicron.vicky.repository.inmemory

import com.lomicron.oikoumene.repository.inmemory.InMemoryEntityRepository
import com.lomicron.vicky.model.production.ProductionType
import com.lomicron.vicky.repository.api.ProductionTypeRepository

object InMemoryProductionTypeRepository
  extends InMemoryEntityRepository[ProductionType]
    with ProductionTypeRepository {

  override def setId(entity: ProductionType, id: String): ProductionType =
    entity.copy(id = id)

}
