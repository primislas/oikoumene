package com.lomicron.imperator.repository.inmemory

import com.lomicron.imperator.model.provinces.PopType
import com.lomicron.imperator.repository.api.PopTypeRepository
import com.lomicron.oikoumene.repository.inmemory.InMemoryEntityRepository

object InMemoryPopTypeTypeRepository
  extends InMemoryEntityRepository[PopType]
    with PopTypeRepository {

  override def setId(entity: PopType, id: String): PopType = entity.copy(id = id)

}
