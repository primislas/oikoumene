package com.lomicron.oikoumene.model.government

import com.lomicron.oikoumene.model.Entity
import com.lomicron.oikoumene.model.events.TagCondition
import com.lomicron.oikoumene.model.localisation.Localisation
import com.lomicron.oikoumene.model.modifiers.Modifier
import com.lomicron.utils.json.FromJson

case class IdeaGroup
(
  // hits = 404, isOptional = false, sample = "aristocracy_ideas"
  id: String = Entity.UNDEFINED,
  // hits = 403, isOptional = true, sample = {"name":"Aristocratic Ideas"}
  localisation: Localisation = Localisation.empty,
  // hits = 404, isOptional = false, sample = "00_basic_ideas.txt"
  sourceFile: String = Entity.UNDEFINED,
  // hits = 384, isOptional = true, sample = {"land_morale":0.1,"core_creation":-0.10}
  start: Modifier = Modifier.empty,
  // hits = 404, isOptional = false, sample = [{"id":"noble_knights","modifiers":{"cavalry_power":0.10,"cavalry_cost":-0.10}},{"id":"military_traditions","modifiers":{"mil_tech_cost_modifier":-0.1}},{"id":"local_nobility","modifiers":{"global_autonomy":-0.025,"yearly_absolutism":0.1}},{"id":"serfdom","modifiers":{"global_manpower_modifier":0.33}},{"id":"noble_officers","modifiers":{"army_tradition_decay":-0.01,"navy_tradition_decay":-0.01}},{"id":"international_nobility","modifiers":{"diplomats":1,"free_leader_pool":1}},{"id":"noble_resilience","modifiers":{"possible_mercenaries":0.20}},{"id":"ai_will_do","modifiers":{"factor":0.45,"modifier":{"factor":0.5,"is_subject":true}}}]
  ideas: Seq[Idea] = Seq.empty,
  // hits = 403, isOptional = true, sample = {"leader_siege":1}
  bonus: Option[Modifier] = None,
  // hits = 388, isOptional = true, sample = {"OR":{"has_government_attribute":"enables_aristocratic_idea_group","government":["theocracy","tribal"]}}
  trigger: Option[TagCondition] = None,
  // hits = 385, isOptional = true, sample = true
  free: Boolean = false,
  // hits = 19, isOptional = true, sample = {"factor":0.45,"modifier":{"factor":0.5,"is_subject":true}}
  aiWillDo: Option[TagCondition] = None,
  // hits = 19, isOptional = true, sample = "MIL"
  category: Option[String] = None,
) extends Entity {
}

object IdeaGroup extends FromJson[IdeaGroup]
