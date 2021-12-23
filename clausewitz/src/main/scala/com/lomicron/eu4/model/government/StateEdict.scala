package com.lomicron.eu4.model.government

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.oikoumene.model.{Color, Entity}
import com.lomicron.oikoumene.model.localisation.Localisation
import com.lomicron.utils.json.{FromJson, JsonMapper}

@JsonCreator
case class StateEdict
(
  // hits = 10, isOptional = false, sample = "edict_defensive_edict"
  id: String = Entity.UNDEFINED,
  // hits = 10, isOptional = false, sample = {"name":"Defensive Edict"}
  localisation: Localisation = Localisation.empty,
  // hits = 10, isOptional = false, sample = "defensive_edict.txt"
  sourceFile: String = Entity.UNDEFINED,
  // hits = 10, isOptional = false, sample = [46,114,55]
  color: Color = Color.black,
  // hits = 10, isOptional = false, sample = {"local_defensiveness":0.33}
  modifier: ObjectNode = JsonMapper.objectNode,
  // hits = 10, isOptional = false, sample = {"always":true}
  potential: ObjectNode = JsonMapper.objectNode,
  // hits = 10, isOptional = false, sample = {"always":true}
  allow: ObjectNode = JsonMapper.objectNode,
  // hits = 10, isOptional = false, sample = {"factor":0}
  aiWillDo: ObjectNode = JsonMapper.objectNode,
) extends Entity

object StateEdict extends FromJson[StateEdict]
