package com.lomicron.oikoumene.model.diplomacy

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.utils.json.FromJson
import com.lomicron.utils.parsing.tokenizer.Date

case class WarEvent
(
  // hits = 1237, isOptional = false, sample = {"year":1743,"month":1,"day":1}
  date: Date = Date.zero,
  // hits = 395, isOptional = true, sample = "PER"
  addAttacker: Seq[String] = Seq.empty,
  // hits = 421, isOptional = true, sample = "MUG"
  addDefender: Seq[String] = Seq.empty,
  // hits = 401, isOptional = true, sample = "PER"
  remAttacker: Seq[String] = Seq.empty,
  // hits = 401, isOptional = true, sample = "MUG"
  remDefender: Seq[String] = Seq.empty,
  // hits = 347, isOptional = true, sample = {"name":"Ceresole","location":103,"attacker":{"commander":"Francois de Vendome","infantry":15000,"cavalry":2000,"artillery":20,"losses":12,"country":"FRA"},"defender":{"commander":"Marques del Vasto","infantry":18000,"cavalry":1500,"artillery":25,"losses":26,"country":"SPA"},"result":true}
  battle: Option[Battle] = None,
) {
  @JsonCreator def this() = this(Date.zero)
}

object WarEvent extends FromJson[WarEvent]