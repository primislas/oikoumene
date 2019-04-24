package com.lomicron.oikoumene.model.politics

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.oikoumene.model.Entity
import com.lomicron.oikoumene.model.localisation.Localisation
import com.lomicron.utils.json.FromJson

case class PriceModifier
(
  // hits = 10, isOptional = false, sample = "gems"
  tradeGoods: String = Entity.UNDEFINED,
  // hits = 10, isOptional = false, sample = "FACETING"
  key: String = Entity.UNDEFINED,
  // hits = 10, isOptional = false, sample = -1
  duration: BigDecimal = BigDecimal(0),
  // hits = 10, isOptional = false, sample = 0.25
  value: BigDecimal = BigDecimal(0),
) {
  @JsonCreator def this() = this(Entity.UNDEFINED)
}

object PriceModifier extends FromJson[PriceModifier]