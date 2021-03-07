package com.lomicron.oikoumene.model.government

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.oikoumene.model.Entity
import com.lomicron.oikoumene.model.localisation.Localisation
import com.lomicron.utils.json.FromJson

@JsonCreator
case class TechGroup
(
  // hits = 15, isOptional = false, sample = "western"
  id: String = Entity.UNDEFINED,
  localisation: Localisation = Localisation.empty,
  // hits = 15, isOptional = false, sample = 0
  startCostModifier: BigDecimal = BigDecimal(0),
  // hits = 15, isOptional = false, sample = 3
  startLevel: Int = 0,
  // hits = 7, isOptional = true, sample = {"trigger":{"capital_scope":{"OR":{"continent":["new_world","north_america","south_america","oceania"]}}},"value":75}
  nationDesignerCost: Option[ObjectNode] = None,
  // hits = 4, isOptional = true, sample = true
  isPrimitive: Boolean = false,
  // hits = 4, isOptional = true, sample = "sub_saharan"
  nationDesignerUnitType: Option[String] = None,
  // hits = 2, isOptional = true, sample = {"has_reform":"steppe_horde"}
  nationDesignerTrigger: Option[ObjectNode] = None,
) extends Entity

object TechGroup extends FromJson[TechGroup]
