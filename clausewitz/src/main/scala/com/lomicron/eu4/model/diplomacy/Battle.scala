package com.lomicron.eu4.model.diplomacy

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.oikoumene.model.Entity
import com.lomicron.utils.json.FromJson

@JsonCreator
case class Battle
(
  id: Option[Int],
  // hits = 347, isOptional = false, sample = {"commander":"Francois de Vendome","infantry":15000,"cavalry":2000,"artillery":20,"losses":12,"country":"FRA"}
  attacker: Army = Army.empty,
  // hits = 347, isOptional = false, sample = {"commander":"Marques del Vasto","infantry":18000,"cavalry":1500,"artillery":25,"losses":26,"country":"SPA"}
  defender: Army = Army.empty,
  // hits = 347, isOptional = false, sample = 103
  location: Int = 0,
  // hits = 347, isOptional = false, sample = "Ceresole"
  name: String = Entity.UNDEFINED,
  // hits = 347, isOptional = false, sample = true
  result: Boolean = false,
)

object Battle extends FromJson[Battle]
