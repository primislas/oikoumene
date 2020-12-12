package com.lomicron.oikoumene.model.trade

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.oikoumene.model.Entity
import com.lomicron.utils.json.FromJson

case class Price(id: String, basePrice: BigDecimal = BigDecimal(0)) {
  @JsonCreator def this() = this(Entity.UNDEFINED)
}

object Price extends FromJson[Price]
