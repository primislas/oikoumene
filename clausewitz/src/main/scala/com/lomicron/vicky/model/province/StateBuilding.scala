package com.lomicron.vicky.model.province

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.oikoumene.model.Entity
import com.lomicron.utils.json.FromJson

case class StateBuilding
(
  // hits = 206, isOptional = false, sample = "fabric_factory"
  building: String = Entity.UNDEFINED,
  // hits = 206, isOptional = false, sample = 1
  level: Int = 0,
  // hits = 206, isOptional = false, sample = true
  upgrade: Boolean = false,
) {
  @JsonCreator def this() = this(Entity.UNDEFINED)
}

object StateBuilding extends FromJson[StateBuilding]
