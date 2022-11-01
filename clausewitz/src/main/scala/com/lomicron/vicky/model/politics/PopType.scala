package com.lomicron.vicky.model.politics

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.oikoumene.model.localisation.Localisation
import com.lomicron.oikoumene.model.{Color, Entity}
import com.lomicron.utils.json.{FromJson, JsonMapper}

import scala.collection.immutable.ListMap

case class PopType
(
  id: String,
  localisation: Localisation = Localisation.empty,
  // hits = 12, isOptional = false, sample = "aristocrats.txt"
  sourceFile: String = Entity.UNDEFINED,
  // hits = 12, isOptional = false, sample = [11,40,93]
  color: Color = Color.black,
  // hits = 12, isOptional = false, sample = {"coal":1,"paper":10,"luxury_clothes":3,"luxury_furniture":3,"wine":10,"tobacco":10,"coffee":5}
  everydayNeeds: ListMap[String, BigDecimal] = ListMap.empty,
  // hits = 11, isOptional = true, sample = {"cattle":0.75,"wool":1,"fish":1,"fruit":1,"grain":2.5}
  lifeNeeds: ListMap[String, BigDecimal] = ListMap.empty,
  // hits = 12, isOptional = false, sample = {"opium":10,"telephones":10,"automobiles":10,"aeroplanes":5,"radio":10,"fuel":10,"ammunition":1,"small_arms":1,"clipper_convoy":2,"steamer_convoy":2}
  luxuryNeeds: ListMap[String, BigDecimal] = ListMap.empty,
  // hits = 12, isOptional = false, sample = {"fascist":{"factor":1,"modifier":[{"factor":0.5,"not":{"militancy":6}},{"factor":1.1,"revanchism":0.01},{"factor":1.1,"revanchism":0.02},{"factor":1.1,"revanchism":0.03},{"factor":1.1,"revanchism":0.04},{"factor":1.1,"revanchism":0.05},{"factor":2,"revanchism":0.10},{"factor":4,"revanchism":0.15},{"factor":8,"revanchism":0.20},{"factor":1.1,"NOT":{"political_reform_want":0.15}},{"factor":1.1,"NOT":{"political_reform_want":0.05}},{"factor":1.1,"NOT":{"political_reform_want":0.10}},{"factor":2,"r
  ideologies: ObjectNode = JsonMapper.objectNode,
  // hits = 12, isOptional = false, sample = {"protectionism":{"factor":1.1,"modifier":[{"factor":1.1,"NOT":{"life_needs":1.0},"country":{"trade_policy":"free_trade"}},{"factor":1.1,"everyday_needs":0.25,"country":{"trade_policy":"protectionism"}},{"factor":1.1,"everyday_needs":0.5,"country":{"trade_policy":"protectionism"}},{"factor":1.1,"everyday_needs":0.75,"country":{"trade_policy":"protectionism"}},{"factor":1.1,"everyday_needs":1,"country":{"trade_policy":"protectionism"}}]},"free_trade":{"factor":1,"modifier":[{"factor":0,"NOT":{"li
  issues: ObjectNode = JsonMapper.objectNode,
  // hits = 12, isOptional = false, sample = 1
  sprite: Int = 0,
  // hits = 12, isOptional = false, sample = "rich"
  strata: String = Entity.UNDEFINED,
  // hits = 11, isOptional = true, sample = {"factor":1,"modifier":[{"factor":0.9,"unemployment":0.05},{"factor":0.9,"unemployment":0.1},{"factor":0.9,"unemployment":0.15},{"factor":0.9,"unemployment":0.2},{"factor":0.9,"unemployment":0.25},{"factor":0.9,"unemployment":0.3},{"factor":0.9,"unemployment":0.35},{"factor":0.1,"unemployment":0.5},{"factor":2.0,"has_pop_culture":"THIS"},{"factor":1.05,"vote_franschise":"wealth_voting"},{"factor":1.25,"vote_franschise":"universal_weighted_voting"},{"factor":1.5,"vote_franschise":"universal_votin
  countryMigrationTarget: Option[ObjectNode] = None,
  // hits = 11, isOptional = true, sample = {"factor":0.01,"modifier":[{"factor":0.1,"unemployment":0.1,"state_scope":{"has_factories":false}},{"factor":1.2,"total_pops":150000},{"factor":1.2,"total_pops":250000},{"factor":1.2,"total_pops":500000},{"factor":1.2,"total_pops":750000},{"factor":0,"AND":{"state_scope":{"has_factories":false},"can_build_factory":false},"OR":{"unemployment_by_type":[{"type":"farmers","value":0.1},{"type":"labourers","value":0.1}]}},{"factor":1.2,"has_pop_culture":"THIS"},{"factor":2,"state_scope":{"has_factorie
  migrationTarget: Option[ObjectNode] = None,
  // hits = 11, isOptional = true, sample = {"capitalists":{"factor":0.01,"modifier":[{"factor":-1,"terrain":"desert"},{"factor":-1,"terrain":"arctic"},{"factor":-1,"state_scope":{"has_factories":false},"NOT":{"total_pops":200000}},{"factor":-1,"is_accepted_culture":false},{"factor":-10,"NOT":{"literacy":0.30}},{"factor":-2,"country":{"has_country_flag":"serfdom_not_abolished"}},{"factor":-40,"AND":{"state_scope":{"has_factories":false},"can_build_factory":false}},{"factor":10,"AND":{"state_scope":{"has_factories":false},"can_build_factor
  promoteTo: Option[ObjectNode] = None,
  // hits = 9, isOptional = true, sample = {"cavalry":0.1,"artillery":0,"cuirassier":0.1,"dragoon":0.1,"guard":0.6,"hussar":0.1,"infantry":0,"irregular":0}
  rebel: ListMap[String, BigDecimal] = ListMap.empty,
  // hits = 5, isOptional = true, sample = true
  unemployment: Boolean = false,
  // hits = 4, isOptional = true, sample = {"type":"administration","weight":1}
  everydayNeedsIncome: Option[ObjectNode] = None,
  // hits = 4, isOptional = true, sample = {"type":"administration","weight":1}
  lifeNeedsIncome: Option[ObjectNode] = None,
  // hits = 4, isOptional = true, sample = {"type":"administration","weight":1}
  luxuryNeedsIncome: Option[ObjectNode] = None,
  // hits = 3, isOptional = true, sample = 0.02
  researchOptimum: Option[BigDecimal] = None,
  // hits = 3, isOptional = true, sample = true
  stateCapitalOnly: Boolean = false,
  // hits = 2, isOptional = true, sample = true
  canWorkFactory: Boolean = false,
  // hits = 2, isOptional = true, sample = true
  demoteMigrant: Boolean = false,
  // hits = 2, isOptional = true, sample = "labourers"
  equivalent: Option[String] = None,
  // hits = 2, isOptional = true, sample = 3
  researchPoints: Option[Int] = None,
  // hits = 1, isOptional = true, sample = true
  administrativeEfficiency: Boolean = false,
  // hits = 1, isOptional = true, sample = false
  allowedToVote: Boolean = true,
  // hits = 1, isOptional = true, sample = true
  canBeRecruited: Boolean = false,
  // hits = 1, isOptional = true, sample = true
  canBuild: Boolean = false,
  // hits = 1, isOptional = true, sample = true
  canReduceConsciousness: Boolean = false,
  // hits = 1, isOptional = true, sample = true
  factory: Boolean = false,
  // hits = 1, isOptional = true, sample = true
  isArtisan: Boolean = false,
  // hits = 1, isOptional = true, sample = true
  isSlave: Boolean = false,
  // hits = 1, isOptional = true, sample = 3
  leadership: Option[Int] = None,
  // hits = 1, isOptional = true, sample = 250000
  maxSize: Option[Int] = None,
  // hits = 1, isOptional = true, sample = 10000
  mergeMaxSize: Option[Int] = None,
  // hits = 1, isOptional = true, sample = 1.0
  starterShare: Option[BigDecimal] = None,
  // hits = 1, isOptional = true, sample = 1.0
  taxEff: Option[BigDecimal] = None,
  // hits = 1, isOptional = true, sample = -0.1
  workplaceInput: Option[BigDecimal] = None,
  // hits = 1, isOptional = true, sample = 0
  workplaceOutput: Option[Int] = None,
) extends Entity {
  @JsonCreator def this() = this(Entity.UNDEFINED)
}

object PopType extends FromJson[PopType]
