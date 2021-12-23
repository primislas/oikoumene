package com.lomicron.eu4.repository.inmemory.government

import com.lomicron.eu4.model.government.{Government, LegacyGovernmentMapping}
import com.lomicron.eu4.repository.api.government.GovernmentRepository
import com.lomicron.oikoumene.repository.inmemory.InMemoryEntityRepository

case class InMemoryGovernmentRepository()
  extends InMemoryEntityRepository[Government]
    with GovernmentRepository { self =>

  private var preDharmaMapping: Map[String, LegacyGovernmentMapping] = Map.empty

  override def setId(entity: Government, id: String): Government = entity.copy(id = id)

  override def legacyMapping(mapping: Map[String, LegacyGovernmentMapping]): GovernmentRepository = {
    preDharmaMapping = mapping
    self
  }

  override def legacyMapping: Map[String, LegacyGovernmentMapping] =
    preDharmaMapping
}
