package com.lomicron.oikoumene.model.politics

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.oikoumene.model.Entity
import com.lomicron.utils.json.FromJson

case class RulerModifier
(// hits = 1, isOptional = false, sample = "shahrukhs_empire"
 name: String = Entity.UNDEFINED,
) {
  @JsonCreator def this() = this(Entity.UNDEFINED)
}

object RulerModifier extends FromJson[RulerModifier]