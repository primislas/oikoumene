package com.lomicron.imperator.model.provinces

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.oikoumene.model.localisation.Localisation
import com.lomicron.oikoumene.model.{Color, Entity}
import com.lomicron.utils.json.{FromJson, JsonMapper}

case class PopType
(
  // hits = 5, isOptional = false, sample = "citizen"
  id: String = Entity.UNDEFINED,
  localisation: Localisation = Localisation.empty,
  // hits = 5, isOptional = false, sample = {"arrayElements":[0.63,0.87,0.95]}
  arrayelements: ObjectNode = JsonMapper.objectNode,
  // hits = 5, isOptional = false, sample = 0.6
  assimilate: BigDecimal = BigDecimal(0),
  // hits = 5, isOptional = false, sample = -0.1
  baseHappyness: BigDecimal = BigDecimal(0),
  // hits = 5, isOptional = false, sample = "hsv"
  color: Color = Color.black,
  // hits = 5, isOptional = false, sample = 25
  conquestDemoteChance: Int = 0,
  // hits = 5, isOptional = false, sample = 0.6
  convert: BigDecimal = BigDecimal(0),
  // hits = 5, isOptional = false, sample = {"pop_food_consumption":-0.3,"local_base_trade_routes":0.03}
  countModifier: ObjectNode = JsonMapper.objectNode,
  // hits = 5, isOptional = false, sample = 4
  demote: Int = 0,
  // hits = 5, isOptional = false, sample = "freemen"
  demotesTo: String = Entity.UNDEFINED,
  // hits = 5, isOptional = false, sample = 0.0
  growingPop: BigDecimal = BigDecimal(0),
  // hits = 5, isOptional = false, sample = 0.6
  migrant: BigDecimal = BigDecimal(0),
  // hits = 5, isOptional = false, sample = {"0":"value_research","1":"value_income"}
  modificationDisplay: ObjectNode = JsonMapper.objectNode,
  // hits = 5, isOptional = false, sample = {"local_manpower":0.004,"research_points":0.2}
  outputModifier: ObjectNode = JsonMapper.objectNode,
  // hits = 5, isOptional = false, sample = 1.5
  politicalWeight: BigDecimal = BigDecimal(0),
  // hits = 5, isOptional = false, sample = 6
  promote: Int = 0,
  // hits = 5, isOptional = false, sample = 2
  uiTier: Int = 0,
  // hits = 4, isOptional = true, sample = "nobles"
  canPromoteTo: Option[String] = None,
  // hits = 4, isOptional = true, sample = "advanced"
  levyTier: Option[String] = None,
  // hits = 2, isOptional = true, sample = true
  integratedPopTypeRight: Boolean = false,
  // hits = 2, isOptional = true, sample = true
  score: Boolean = false,
  // hits = 1, isOptional = true, sample = true
  blockColonization: Boolean = false,
  // hits = 1, isOptional = true, sample = true
  defaultPopRight: Boolean = false,
  // hits = 1, isOptional = true, sample = true
  isCitizen: Boolean = false,
  // hits = 1, isOptional = true, sample = true
  isLinkedWithHoldings: Boolean = false,
  // hits = 1, isOptional = true, sample = true
  isSlaves: Boolean = false,
  // hits = 1, isOptional = true, sample = true
  tribal: Boolean = false,
) extends Entity {
  @JsonCreator def this() = this(Entity.UNDEFINED)
  def withId(id: String): PopType = copy(id = id)
}

object PopType extends FromJson[PopType]
