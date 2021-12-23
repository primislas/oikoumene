package com.lomicron.eu4.repository.inmemory.trade

import com.lomicron.eu4.model.trade.TradeNode
import com.lomicron.eu4.repository.api.trade.TradeNodeRepository
import com.lomicron.oikoumene.repository.inmemory.InMemoryEntityRepository

case class InMemoryTradeNodeRepository()
extends InMemoryEntityRepository[TradeNode]
with TradeNodeRepository {

  override def setId(entity: TradeNode, id: String): TradeNode =
    entity.copy(id = id)

}
