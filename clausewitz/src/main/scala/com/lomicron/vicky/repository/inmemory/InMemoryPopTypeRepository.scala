package com.lomicron.vicky.repository.inmemory

import com.lomicron.oikoumene.repository.inmemory.InMemoryEntityRepository
import com.lomicron.vicky.model.politics.PopType
import com.lomicron.vicky.repository.api.PopTypeRepository

object InMemoryPopTypeRepository
  extends InMemoryEntityRepository[PopType]
    with PopTypeRepository {

  override def setId(entity: PopType, id: String): PopType =
    entity.copy(id = id)

}
