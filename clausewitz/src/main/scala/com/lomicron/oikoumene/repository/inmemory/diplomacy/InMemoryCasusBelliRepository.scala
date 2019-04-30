package com.lomicron.oikoumene.repository.inmemory.diplomacy

import com.lomicron.oikoumene.model.diplomacy.CasusBelli
import com.lomicron.oikoumene.repository.api.diplomacy.CasusBelliRepository
import com.lomicron.oikoumene.repository.inmemory.InMemoryEntityRepository

case class InMemoryCasusBelliRepository()
  extends InMemoryEntityRepository[CasusBelli]
    with CasusBelliRepository {

  override def setId(entity: CasusBelli, id: String): CasusBelli =
    entity.copy(id = id)

}
