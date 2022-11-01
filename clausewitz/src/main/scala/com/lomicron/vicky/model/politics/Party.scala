package com.lomicron.vicky.model.politics

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.oikoumene.model.Entity
import com.lomicron.utils.json.FromJson
import com.lomicron.utils.parsing.tokenizer.Date

case class Party
(
  // hits = 2299, isOptional = false, sample = "ABU_conservative"
  name: String = Entity.UNDEFINED,
  // hits = 2299, isOptional = false, sample = "conservative"
  ideology: String = Entity.UNDEFINED,
  // hits = 2299, isOptional = false, sample = "1820.1.1"
  startDate: Date = Date.zero,
  // hits = 2299, isOptional = false, sample = "1892.1.1"
  endDate: Date = Date.zero,
  // hits = 2299, isOptional = false, sample = "residency"
  citizenshipPolicy: String = Entity.UNDEFINED,
  // hits = 2299, isOptional = false, sample = "interventionism"
  economicPolicy: String = Entity.UNDEFINED,
  // hits = 2299, isOptional = false, sample = "moralism"
  religiousPolicy: String = Entity.UNDEFINED,
  // hits = 2299, isOptional = false, sample = "protectionism"
  tradePolicy: String = Entity.UNDEFINED,
  // hits = 2299, isOptional = false, sample = "anti_military"
  warPolicy: String = Entity.UNDEFINED,
) {
  @JsonCreator def this() = this(Entity.UNDEFINED)
}

object Party extends FromJson[Party]
