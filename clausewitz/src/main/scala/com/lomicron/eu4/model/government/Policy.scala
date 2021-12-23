package com.lomicron.eu4.model.government

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.oikoumene.model.Entity
import com.lomicron.oikoumene.model.localisation.Localisation
import com.lomicron.utils.json.{FromJson, JsonMapper}

@JsonCreator
case class Policy
(
  // hits = 120, isOptional = false, sample = "the_combination_act"
  id: String = Entity.UNDEFINED,
  // hits = 120, isOptional = false, sample = {"name":"The Combination Act"}
  localisation: Localisation = Localisation.empty,
  // hits = 120, isOptional = false, sample = "00_adm.txt"
  sourceFile: String = Entity.UNDEFINED,
  // hits = 120, isOptional = false, sample = {"factor":1,"modifier":[{"factor":0,"NOT":{"production_income_percentage":0.1}},{"factor":1.5,"production_income_percentage":0.2},{"factor":1.5,"production_income_percentage":0.3},{"factor":1.5,"production_income_percentage":0.4},{"factor":1.5,"production_income_percentage":0.5}]}
  aiWillDo: ObjectNode = JsonMapper.objectNode,
  // hits = 120, isOptional = false, sample = {"allow":{"full_idea_group":["aristocracy_ideas","innovativeness_ideas"]},"production_efficiency":0.20}
  modifier: ObjectNode = JsonMapper.objectNode,
  // hits = 120, isOptional = false, sample = "ADM"
  monarchPower: String = Entity.UNDEFINED,
  // hits = 120, isOptional = false, sample = {"has_idea_group":["aristocracy_ideas","innovativeness_ideas"]}
  potential: ObjectNode = JsonMapper.objectNode,
) extends Entity

object Policy extends FromJson[Policy]
