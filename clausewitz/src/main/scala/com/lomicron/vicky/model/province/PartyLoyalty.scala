package com.lomicron.vicky.model.province

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.oikoumene.model.Entity
import com.lomicron.utils.json.FromJson

case class PartyLoyalty
(
  // hits = 20, isOptional = false, sample = "conservative"
  ideology: String = Entity.UNDEFINED,
  // hits = 20, isOptional = false, sample = 10
  loyaltyValue: Int = 0,
) {
  @JsonCreator def this() = this(Entity.UNDEFINED)
}

object PartyLoyalty extends FromJson[PartyLoyalty]
