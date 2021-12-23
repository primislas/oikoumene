package com.lomicron.imperator.model.provinces

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.oikoumene.model.Entity
import com.lomicron.oikoumene.model.localisation.Localisation
import com.lomicron.utils.json.FromJson

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
  // hits = 5424, isOptional = true, sample = {"amount":2}
  tribesmen: Seq[ObjectNode] = Seq.empty,
  // hits = 4876, isOptional = true, sample = {"amount":1}
  freemen: Seq[ObjectNode] = Seq.empty,
  // hits = 3800, isOptional = true, sample = {"amount":1}
  slaves: Seq[ObjectNode] = Seq.empty,
  // hits = 2889, isOptional = true, sample = {"amount":1}
  citizen: Seq[ObjectNode] = Seq.empty,
  // hits = 393, isOptional = true, sample = 1
  portBuilding: Option[Int] = None,
  // hits = 179, isOptional = true, sample = {"amount":1}
  nobles: Seq[ObjectNode] = Seq.empty,
  // hits = 166, isOptional = true, sample = "omen_baal_hammon"
  holySite: Option[String] = None,
  // hits = 2, isOptional = true, sample = 2
  commerceBuilding: Option[Int] = None,
  // hits = 2, isOptional = true, sample = 2
  libraryBuilding: Option[Int] = None,
  // hits = 1, isOptional = true, sample = 2
  aqueductBuilding: Option[Int] = None,
  // hits = 1, isOptional = true, sample = 1
  courtBuilding: Option[Int] = None,
  // hits = 1, isOptional = true, sample = 1
  townHallBuilding: Option[Int] = None,
) {
  @JsonCreator def this() = this(-1)
  def withId(id: Int): Province = copy(id = id)
}

object Province extends FromJson[Province]
