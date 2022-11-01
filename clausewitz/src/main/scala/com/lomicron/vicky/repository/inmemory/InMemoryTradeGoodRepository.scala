package com.lomicron.vicky.repository.inmemory

import com.lomicron.oikoumene.repository.inmemory.InMemoryEntityRepository
import com.lomicron.vicky.model.production.TradeGood
import com.lomicron.vicky.repository.api.TradeGoodRepository

object InMemoryTradeGoodRepository
  extends InMemoryEntityRepository[TradeGood]
    with TradeGoodRepository {

  override def setId(entity: TradeGood, id: String): TradeGood =
    entity.copy(id = id)

}
