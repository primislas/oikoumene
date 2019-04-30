package com.lomicron.oikoumene.model.diplomacy

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.oikoumene.model.Entity
import com.lomicron.utils.json.FromJson

case class WarGoal
(
  // hits = 345, isOptional = false, sample = "cb_conquest"
  casusBelli: String = Entity.UNDEFINED,
  // hits = 345, isOptional = false, sample = "take_claim"
  `type`: String = Entity.UNDEFINED,
  // hits = 232, isOptional = true, sample = 522
  province: Option[Int] = None,
  // hits = 67, isOptional = true, sample = "ARA"
  tag: Option[String] = None,
) {
  @JsonCreator def this() = this(Entity.UNDEFINED)
}

object WarGoal extends FromJson[WarGoal]
