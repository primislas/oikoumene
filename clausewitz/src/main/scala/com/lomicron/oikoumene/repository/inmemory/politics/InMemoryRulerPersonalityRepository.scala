package com.lomicron.oikoumene.repository.inmemory.politics

import com.lomicron.oikoumene.model.politics.RulerPersonality
import com.lomicron.oikoumene.repository.api.politics.RulerPersonalityRepository
import com.lomicron.oikoumene.repository.inmemory.InMemoryEntityRepository

case class InMemoryRulerPersonalityRepository()
  extends InMemoryEntityRepository[RulerPersonality]
    with RulerPersonalityRepository {

  override def setId(entity: RulerPersonality, id: String): RulerPersonality = entity.copy(id = id)

}
