package com.lomicron.eu4.model.provinces

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.oikoumene.model.Entity.UNDEFINED

@JsonCreator
case class TradeCompanyInvestment
(
  investment: String = UNDEFINED,
  investor: String = UNDEFINED
)
