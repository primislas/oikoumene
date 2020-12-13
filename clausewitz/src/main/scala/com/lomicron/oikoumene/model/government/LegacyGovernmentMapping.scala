package com.lomicron.oikoumene.model.government

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.oikoumene.model.Entity
import com.lomicron.utils.json.FromJson

case class LegacyGovernmentMapping(id: String, government: String, legacyGovernment: String) {
  @JsonCreator def this() = this(Entity.UNDEFINED, Entity.UNDEFINED, Entity.UNDEFINED)
}

object LegacyGovernmentMapping extends FromJson[LegacyGovernmentMapping]
