package com.lomicron.oikoumene.model.trade

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.oikoumene.model.Entity
import com.lomicron.utils.json.FromJson

@JsonCreator
case class Price(id: String = Entity.UNDEFINED, basePrice: BigDecimal = BigDecimal(0))

object Price extends FromJson[Price]
