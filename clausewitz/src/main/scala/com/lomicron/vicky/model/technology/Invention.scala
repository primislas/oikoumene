package com.lomicron.vicky.model.technology

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.oikoumene.model.Entity
import com.lomicron.oikoumene.model.localisation.Localisation
import com.lomicron.utils.json.{FromJson, JsonMapper}
import com.lomicron.vicky.model.Limit

case class Invention
(
  // hits = 387, isOptional = false, sample = "post_napoleonic_army_doctrine"
  id: String = Entity.UNDEFINED,
  // hits = 378, isOptional = true, sample = {"name":"Post Napoleonic Army Doctrine"}
  localisation: Localisation = Localisation.empty,
  // hits = 387, isOptional = false, sample = {"base":2}
  chance: ObjectNode = JsonMapper.objectNode,
  // hits = 387, isOptional = false, sample = {"post_napoleonic_thought":1}
  limit: Limit = Limit.empty,
  // hits = 376, isOptional = true, sample = {"infantry":{"defence":1},"guard":{"defence":1},"engineer":{"defence":1},"cuirassier":{"defence":1},"dragoon":{"defence":1},"hussar":{"defence":1},"cavalry":{"defence":1},"irregular":{"defence":1}}
  effect: Option[ObjectNode] = None,
  // hits = 173, isOptional = true, sample = false
  news: Boolean = true,
  // hits = 11, isOptional = true, sample = 0.01
  corePopConsciousnessModifier: Option[BigDecimal] = None,
  // hits = 2, isOptional = true, sample = 0.1
  politicalReformDesire: Option[BigDecimal] = None,
  // hits = 1, isOptional = true, sample = -0.01
  corePopMilitancyModifier: Option[BigDecimal] = None,
  // hits = 1, isOptional = true, sample = 0.05
  orgRegain: Option[BigDecimal] = None,
  // hits = 1, isOptional = true, sample = 0.05
  socialReformDesire: Option[BigDecimal] = None,
) extends Entity {
  @JsonCreator def this() = this(Entity.UNDEFINED)
}

object Invention extends FromJson[Invention]
