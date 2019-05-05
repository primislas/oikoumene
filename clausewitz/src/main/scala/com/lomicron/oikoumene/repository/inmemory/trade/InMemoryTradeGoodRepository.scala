package com.lomicron.oikoumene.repository.inmemory.trade

import com.lomicron.oikoumene.model.trade.TradeGood
import com.lomicron.oikoumene.repository.api.trade.TradeGoodRepository
import com.lomicron.oikoumene.repository.inmemory.InMemoryEntityRepository

case class InMemoryTradeGoodRepository()
  extends InMemoryEntityRepository[TradeGood]
    with TradeGoodRepository {

  override def setId(entity: TradeGood, id: String): TradeGood =
    entity.copy(id = id)

}
