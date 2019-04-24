package com.lomicron.oikoumene.model.politics

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.oikoumene.model.Entity
import com.lomicron.utils.json.FromJson

case class CountryModifier
(// hits = 1, isOptional = false, sample = -1
 duration: Int = 0,
 // hits = 1, isOptional = false, sample = "tur_janissary"
 name: String = Entity.UNDEFINED,
) {
  @JsonCreator def this() = this(0)
}

object CountryModifier extends FromJson[CountryModifier]