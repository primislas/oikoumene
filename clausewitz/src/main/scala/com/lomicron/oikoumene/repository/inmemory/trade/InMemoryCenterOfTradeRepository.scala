package com.lomicron.oikoumene.repository.inmemory.trade

import com.lomicron.oikoumene.model.trade.CenterOfTrade
import com.lomicron.oikoumene.repository.api.trade.CenterOfTradeRepository
import com.lomicron.oikoumene.repository.inmemory.InMemoryEntityRepository

case class InMemoryCenterOfTradeRepository()
  extends InMemoryEntityRepository[CenterOfTrade]
    with CenterOfTradeRepository {

  override def setId(entity: CenterOfTrade, id: String): CenterOfTrade =
    entity.copy(id = id)

}
