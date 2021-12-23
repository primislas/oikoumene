package com.lomicron.eu4.repository.api.trade

import com.lomicron.eu4.model.trade.TradeNode
import com.lomicron.oikoumene.repository.api.AbstractRepository

trait TradeNodeRepository extends AbstractRepository[String, TradeNode] {

  def ofProvince(provinceId: Int): Option[TradeNode] =
    findAll.find(_.members.contains(provinceId))

}
