package com.lomicron.eu4.model.diplomacy

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.oikoumene.model.Entity
import com.lomicron.utils.json.FromJson

@JsonCreator
case class War
(
  id: Option[Int],
  // hits = 348, isOptional = false, sample = "OttomanPersianWar.txt"
  sourceFile: String = Entity.UNDEFINED,
  // hits = 348, isOptional = false, sample = [{"add_attacker":"PER","add_defender":"MUG","date":{"year":1743,"month":1,"day":1}},{"rem_attacker":"PER","rem_defender":"MUG","date":{"year":1746,"month":1,"day":1}}]
  events: Seq[WarEvent] = Seq.empty,
  // hits = 346, isOptional = true, sample = "Ottoman-Persian War"
  name: Option[String] = None,
  // hits = 345, isOptional = true, sample = {"type":"take_claim","casus_belli":"cb_conquest","province":522}
  warGoal: Option[WarGoal] = None,
  // hits = 4, isOptional = true, sample = "CAS"
  succession: Option[String] = None,
)

object War extends FromJson[War]
