package com.lomicron.oikoumene.repository.api.trade

import com.lomicron.oikoumene.model.trade.TradeNode
import com.lomicron.oikoumene.repository.api.AbstractRepository

trait TradeNodeRepository extends AbstractRepository[String, TradeNode] {

  def ofProvince(provinceId: Int): Option[TradeNode] =
    findAll.find(_.members.contains(provinceId))

}
