package com.lomicron.imperator.model.provinces

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.oikoumene.model.Entity
import com.lomicron.oikoumene.model.localisation.Localisation
import com.lomicron.utils.json.{FromJson, JsonMapper}

case class Building
(
  // hits = 22, isOptional = false, sample = "fortress_building"
  id: String = Entity.UNDEFINED,
  localisation: Localisation = Localisation.empty,
  // hits = 22, isOptional = false, sample = 150
  cost: Int = 0,
  // hits = 22, isOptional = false, sample = {"0":"fort_level","1":"value_manpower","2":"local_defensive"}
  modificationDisplay: ObjectNode = JsonMapper.objectNode,
  // hits = 22, isOptional = false, sample = 720
  time: Int = 0,
  // hits = 20, isOptional = true, sample = {"has_city_status":true}
  potential: Option[ObjectNode] = None,
  // hits = 17, isOptional = true, sample = {"modifier":["num_of_port_building",">",0,"num_of_port_building","<",5,{">":3,"add":5},{"owner":["num_of_ports","<",1],"add":5},{"owner":{"is_tribal":true},"add":-1.5},{"has_city_status":false,"add":-3}]}
  chance: Option[ObjectNode] = None,
  // hits = 17, isOptional = true, sample = 1
  localCountryCivilizationValue: Option[Int] = None,
  // hits = 14, isOptional = true, sample = 1
  maxAmount: Option[Int] = None,
  // hits = 8, isOptional = true, sample = {"AND":{"exists":"owner","owner":{"invention":"global_defensive_inv_3"}},">":1}
  allow: Option[ObjectNode] = None,
  // hits = 5, isOptional = true, sample = -0.25
  localMigrationSpeedModifier: Option[BigDecimal] = None,
  // hits = 3, isOptional = true, sample = 0.05
  cityMonthlyStateLoyalty: Option[BigDecimal] = None,
  // hits = 3, isOptional = true, sample = -4
  localGoodsFromSlaves: Option[BigDecimal] = None,
  // hits = 3, isOptional = true, sample = -0.05
  localManpowerModifier: Option[BigDecimal] = None,
  // hits = 3, isOptional = true, sample = 0.025
  localMonthlyFoodModifier: Option[BigDecimal] = None,
  // hits = 2, isOptional = true, sample = 0.1
  localCitizenDesiredPopRatio: Option[BigDecimal] = None,
  // hits = 2, isOptional = true, sample = 0.05
  localDefensive: Option[BigDecimal] = None,
  // hits = 2, isOptional = true, sample = 0.15
  localFreemenDesiredPopRatio: Option[BigDecimal] = None,
  // hits = 2, isOptional = true, sample = "happiness_large_svalue"
  localFreemenHappyness: Option[String] = None,
  // hits = 2, isOptional = true, sample = 0.025
  localPopAssimilationSpeedModifier: Option[BigDecimal] = None,
  // hits = 2, isOptional = true, sample = 1
  localPopulationCapacity: Option[Int] = None,
  // hits = 2, isOptional = true, sample = -0.1
  localPopulationCapacityModifier: Option[BigDecimal] = None,
  // hits = 2, isOptional = true, sample = 0.25
  localResearchPointsModifier: Option[BigDecimal] = None,
  // hits = 2, isOptional = true, sample = 0.3
  localSlavesOutput: Option[BigDecimal] = None,
  // hits = 2, isOptional = true, sample = 0.25
  localTaxModifier: Option[BigDecimal] = None,
  // hits = 1, isOptional = true, sample = {"modifier":{"add":{"value":"fort_level","multiply":10}}}
  aiWillDo: Option[ObjectNode] = None,
  // hits = 1, isOptional = true, sample = 1
  fortLevel: Option[Int] = None,
  // hits = 1, isOptional = true, sample = 0.025
  localBaseTradeRoutesModifier: Option[BigDecimal] = None,
  // hits = 1, isOptional = true, sample = 0.06
  localCitizenHappyness: Option[BigDecimal] = None,
  // hits = 1, isOptional = true, sample = -3
  localCombatWidthModifier: Option[BigDecimal] = None,
  // hits = 1, isOptional = true, sample = 200
  localFoodCapacity: Option[Int] = None,
  // hits = 1, isOptional = true, sample = 0.1
  localHappinessForSameCultureModifier: Option[BigDecimal] = None,
  // hits = 1, isOptional = true, sample = 0.1
  localHappinessForSameReligionModifier: Option[BigDecimal] = None,
  // hits = 1, isOptional = true, sample = 1
  localHostileAttrition: Option[Int] = None,
  // hits = 1, isOptional = true, sample = 0.1
  localMigrationAttraction: Option[BigDecimal] = None,
  // hits = 1, isOptional = true, sample = 0.1
  localMigrationSpeed: Option[BigDecimal] = None,
  // hits = 1, isOptional = true, sample = 0.01
  localMonthlyCivilization: Option[BigDecimal] = None,
  // hits = 1, isOptional = true, sample = 0.05
  localNoblesDesiredPopRatio: Option[BigDecimal] = None,
  // hits = 1, isOptional = true, sample = 0.04
  localNoblesHappyness: Option[BigDecimal] = None,
  // hits = 1, isOptional = true, sample = 2
  localPopAssimilationSpeed: Option[Int] = None,
  // hits = 1, isOptional = true, sample = 2
  localPopConversionSpeed: Option[Int] = None,
  // hits = 1, isOptional = true, sample = 0.025
  localPopConversionSpeedModifier: Option[BigDecimal] = None,
  // hits = 1, isOptional = true, sample = 0.15
  localShipRecruitSpeed: Option[BigDecimal] = None,
  // hits = 1, isOptional = true, sample = 0.1
  localSlavesDesiredPopRatio: Option[BigDecimal] = None,
  // hits = 1, isOptional = true, sample = 0.06
  localSlavesHappyness: Option[BigDecimal] = None,
  // hits = 1, isOptional = true, sample = "happiness_large_svalue"
  localTribesmenHappyness: Option[String] = None,
  // hits = 1, isOptional = true, sample = 0.3
  localTribesmenOutput: Option[BigDecimal] = None,
) extends Entity {
  @JsonCreator def this() = this(Entity.UNDEFINED)
  def withId(id: String): Building = copy(id = id)
}

object Building extends FromJson[Building]
