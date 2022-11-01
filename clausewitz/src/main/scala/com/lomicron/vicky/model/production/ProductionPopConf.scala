package com.lomicron.vicky.model.production

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.oikoumene.model.Entity
import com.lomicron.utils.json.FromJson

case class ProductionPopConf
(
  // hits = 46, isOptional = false, sample = "input"
  effect: String = Entity.UNDEFINED,
  // hits = 46, isOptional = false, sample = "capitalists"
  poptype: String = Entity.UNDEFINED,
  // hits = 12, isOptional = true, sample = 0.8
  amount: Option[BigDecimal] = None,
  // hits = 8, isOptional = true, sample = -2.5
  effectMultiplier: Option[BigDecimal] = None,
) {
  @JsonCreator def this() = this(Entity.UNDEFINED)
}

object ProductionPopConf extends FromJson[ProductionPopConf]
