package com.lomicron.eu4.model.trade

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.oikoumene.model.Entity
import com.lomicron.utils.json.FromJson

@JsonCreator
case class TradeNodeRoute
(
  // hits = 141, isOptional = false, sample = "zanzibar"
  name: String = Entity.UNDEFINED,
  // hits = 141, isOptional = false, sample = [3351.000000,607.000000,3388.000000,610.000000,3416.000000,580.000000]
  control: Seq[BigDecimal] = Seq.empty,
  // hits = 141, isOptional = false, sample = [1273,1202]
  path: Seq[Int] = Seq.empty,
)

object TradeNodeRoute extends FromJson[TradeNodeRoute]
