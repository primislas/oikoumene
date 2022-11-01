package com.lomicron.vicky.repository.inmemory

import com.lomicron.oikoumene.repository.inmemory.InMemoryEntityRepository
import com.lomicron.vicky.model.technology.Invention
import com.lomicron.vicky.repository.api.TechnologyRepository

case class InMemoryTechnologyRepository()
  extends InMemoryEntityRepository[Invention]
    with TechnologyRepository {

  override def setId(entity: Invention, id: String): Invention =
    entity.copy(id = id)

}
