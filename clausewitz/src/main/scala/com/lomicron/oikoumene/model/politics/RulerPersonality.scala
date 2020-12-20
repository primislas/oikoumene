package com.lomicron.oikoumene.model.politics

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.oikoumene.model.localisation.Localisation
import com.lomicron.oikoumene.model.modifiers.Modifier
import com.lomicron.oikoumene.model.{Entity, EntityWithModifier}
import com.lomicron.utils.json.FromJson

case class RulerPersonality
(
  // hits = 48, isOptional = false, sample = "just_personality"
  id: String = Entity.UNDEFINED,
  // hits = 48, isOptional = false, sample = {"name":"Just"}
  localisation: Localisation = Localisation.empty,
  // hits = 48, isOptional = false, sample = "00_core.txt"
  sourceFile: String = Entity.UNDEFINED,
  modifier: Option[Modifier] = None,
  // hits = 48, isOptional = false, sample = 2
  nationDesignerCost: BigDecimal = BigDecimal(0),
  // hits = 47, isOptional = true, sample = {"allow":{"NOT":{"heir_has_personality":"cruel_personality"}},"chance":{"modifier":{"factor":1,"heir_ADM":6}}}
  heirAllow: Option[ObjectNode] = None,
  // hits = 47, isOptional = true, sample = {"allow":{"NOT":{"ruler_has_personality":"cruel_personality"}},"chance":{"modifier":{"factor":1,"ADM":6}}}
  rulerAllow: Option[ObjectNode] = None,
  // hits = 46, isOptional = true, sample = {"OR":{"government":["republic","monarchy"]},"NOT":[{"has_reform":"celestial_empire"},{"government":"tribal"},{"government":"native"}]}
  allow: Option[ObjectNode] = None,
  // hits = 46, isOptional = true, sample = {}
  chance: Option[ObjectNode] = None,
  // hits = 46, isOptional = true, sample = {"allow":{"NOT":{"consort_has_personality":"cruel_personality"}},"chance":{"modifier":{"factor":1,"consort_ADM":6}}}
  consortAllow: Option[ObjectNode] = None,
  // hits = 7, isOptional = true, sample = 65
  giftChance: Option[BigDecimal] = None,
  // hits = 6, isOptional = true, sample = "AI_RULER_PICK_IDEA_GROUPS"
  customAiExplanation: Option[String] = None,
) extends EntityWithModifier {
  @JsonCreator def this() = this(Entity.UNDEFINED)
}

object RulerPersonality extends FromJson[RulerPersonality]
