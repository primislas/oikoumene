package com.lomicron.imperator.model.provinces

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.oikoumene.model.Entity
import com.lomicron.utils.json.FromJson

case class ProvinceUpdate
(
  // hits = 647, isOptional = false, sample = "1"
  id: String = Entity.UNDEFINED,
  // hits = 323, isOptional = true, sample = {"modifier":"rhodian_glass_workshops","always":true}
  modifier: Option[ObjectNode] = None,
  // hits = 305, isOptional = true, sample = {"commerce_building":1,"court_building":1,"town_hall_building":1,"military_building":2}
  buildings: Option[ObjectNode] = None,
  // hits = 83, isOptional = true, sample = {"treasures":[16,198]}
  treasureSlots: Option[ObjectNode] = None,
) {
  @JsonCreator def this() = this(Entity.UNDEFINED)
  def withId(id: String): ProvinceUpdate = copy(id = id)
}

object ProvinceUpdate extends FromJson[ProvinceUpdate]
