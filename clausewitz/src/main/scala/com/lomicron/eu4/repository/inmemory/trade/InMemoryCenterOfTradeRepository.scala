package com.lomicron.eu4.repository.inmemory.trade

import com.lomicron.eu4.model.trade.CenterOfTrade
import com.lomicron.eu4.repository.api.trade.CenterOfTradeRepository
import com.lomicron.oikoumene.repository.inmemory.InMemoryEntityRepository

case class InMemoryCenterOfTradeRepository()
  extends InMemoryEntityRepository[CenterOfTrade]
    with CenterOfTradeRepository {

  override def setId(entity: CenterOfTrade, id: String): CenterOfTrade =
    entity.copy(id = id)

}
