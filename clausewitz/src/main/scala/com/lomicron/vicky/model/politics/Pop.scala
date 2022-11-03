package com.lomicron.vicky.model.politics

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.oikoumene.model.Entity
import com.lomicron.utils.json.FromJson

case class Pop
(
  // hits = 7258, isOptional = false, sample = "bureaucrats"
  popType: String = Entity.UNDEFINED,
  // hits = 7258, isOptional = false, sample = "pashtun"
  culture: String = Entity.UNDEFINED,
  // hits = 7258, isOptional = false, sample = "sunni"
  religion: String = Entity.UNDEFINED,
  // hits = 7258, isOptional = false, sample = 825
  size: Int = 0,
  // hits = 75, isOptional = true, sample = 9
  militancy: Int = 0,
  // hits = 6, isOptional = true, sample = "carlist_rebels"
  rebelType: Option[String] = None,
) {
  @JsonCreator def this() = this(Entity.UNDEFINED)
}
