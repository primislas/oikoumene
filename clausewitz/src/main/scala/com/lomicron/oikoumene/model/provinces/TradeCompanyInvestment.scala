package com.lomicron.oikoumene.model.provinces

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.oikoumene.model.Entity.UNDEFINED

case class TradeCompanyInvestment
(investment: String = UNDEFINED,
 investor: String = UNDEFINED) {

  @JsonCreator def this() = this(UNDEFINED)

}
