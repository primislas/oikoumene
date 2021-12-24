package com.lomicron.imperator.model.provinces

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.oikoumene.model.Entity
import com.lomicron.oikoumene.model.localisation.Localisation
import com.lomicron.utils.json.{FromJson, JsonMapper}

case class Province
(
  // hits = 8062, isOptional = false, sample = "1470"
  id: Int = -1,
  localisation: Localisation = Localisation.empty,
  // hits = 8062, isOptional = false, sample = 0
  barbarianPower: Int = 0,
  // hits = 8062, isOptional = false, sample = 26
  civilizationValue: BigDecimal = BigDecimal(0),
  // hits = 8062, isOptional = false, sample = "carthaginian"
  culture: String = Entity.UNDEFINED,
  // hits = 8062, isOptional = false, sample = "settlement"
  provinceRank: String = Entity.UNDEFINED,
  // hits = 8062, isOptional = false, sample = "carthaginian_pantheon"
  religion: String = Entity.UNDEFINED,
  // hits = 8062, isOptional = false, sample = "plains"
  terrain: String = Entity.UNDEFINED,
  // hits = 8062, isOptional = false, sample = "stone"
  tradeGoods: String = Entity.UNDEFINED,
  // hits = 8062, isOptional = false
  buildings: ObjectNode = JsonMapper.objectNode,
  // hits = 8062, isOptional = false
  pops: ObjectNode = JsonMapper.objectNode,
  // hits = 166, isOptional = true, sample = "omen_baal_hammon"
  holySite: Option[String] = None,
) {
  @JsonCreator def this() = this(-1)
  def withId(id: Int): Province = copy(id = id)
}

object Province extends FromJson[Province]
