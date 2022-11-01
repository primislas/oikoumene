package com.lomicron.vicky.model.production

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.oikoumene.model.{Color, Entity}
import com.lomicron.oikoumene.model.localisation.Localisation
import com.lomicron.utils.json.FromJson

case class TradeGood
(
  // hits = 48, isOptional = false, sample = "ammunition"
  id: String = Entity.UNDEFINED,
  // hits = 48, isOptional = false, sample = {"name":"Ammunition"}
  localisation: Localisation = Localisation.empty,
  // hits = 48, isOptional = false, sample = "military_goods"
  category: String = Entity.UNDEFINED,
  // hits = 48, isOptional = false, sample = [208,202,127]
  color: Color = Color.black,
  // hits = 48, isOptional = false, sample = 17.50
  cost: BigDecimal = BigDecimal(0),
  // hits = 15, isOptional = true, sample = false
  availableFromStart: Boolean = true,
  // hits = 2, isOptional = true, sample = true
  overseasPenalty: Boolean = false,
  // hits = 1, isOptional = true, sample = true
  money: Boolean = false,
  // hits = 1, isOptional = true, sample = false
  tradeable: Boolean = true,
) extends Entity {
  @JsonCreator def this() = this(Entity.UNDEFINED)
}

object TradeGood extends FromJson[TradeGood]
