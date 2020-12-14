package com.lomicron.oikoumene.model.government

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.oikoumene.model.Entity
import com.lomicron.oikoumene.model.events.TagCondition
import com.lomicron.oikoumene.model.localisation.Localisation
import com.lomicron.oikoumene.model.modifiers.Modifier
import com.lomicron.oikoumene.model.politics.TagUpdate
import com.lomicron.utils.json.FromJson

import scala.collection.immutable.ListSet

case class GovernmentReform
(
  // hits = 274, isOptional = false, sample = "tribe_mechanic"
  id: String = Entity.UNDEFINED,
  // hits = 274, isOptional = false, sample = "04_government_reforms_tribes.txt"
  sourceFile: String = Entity.UNDEFINED,
  localisation: Localisation = Localisation.empty,
  // hits = 268, isOptional = true, sample = "horde_riding"
  icon: Option[String] = None,
  // hits = 265, isOptional = true, sample = false
  allowNormalConversion: Option[Boolean] = None,
  // hits = 262, isOptional = true, sample = {"global_manpower_modifier":0.2,"land_forcelimit_modifier":0.2,"loot_amount":0.50,"global_institution_spread":-0.15,"reinforce_cost_modifier":-0.5,"cav_to_inf_ratio":0.25,"movement_speed":0.2,"years_of_nationalism":-5}
  modifiers: Option[Modifier] = None,
  // hits = 182, isOptional = true, sample = {"factor":1000}
  ai: Option[JsonNode] = None,
  // hits = 162, isOptional = true, sample = {"OR":{"has_reform":"steppe_horde","culture_group":["altaic","tartar"]},"has_reform":"steppe_horde","NOT":{"has_reform":"great_mongol_state_reform"}}
  potential: Option[JsonNode] = None,
  // hits = 126, isOptional = true, sample = false
  validForNationDesigner: Option[Boolean] = None,
  // hits = 93, isOptional = true, sample = 0
  nationDesignerCost: Option[BigDecimal] = None,
  // hits = 68, isOptional = true, sample = false
  validForNewCountry: Option[Boolean] = None,
  // hits = 64, isOptional = true, sample = "steppe_horde_legacy"
  legacyEquivalent: Option[String] = None,
  // hits = 62, isOptional = true, sample = {"allow":{"has_dlc":"Mandate of Heaven"},"allow_banners":true}
  conditional: Seq[JsonNode] = Seq.empty,
  // hits = 62, isOptional = true, sample = true
  legacyGovernment: Boolean = false,
  // hits = 60, isOptional = true, sample = [{"blocked_call_diet":true},{"blocked_call_diet":true}]
  customAttributes: Seq[ObjectNode] = Seq.empty,
  // hits = 59, isOptional = true, sample = {"technology_group":"nomad_group"}
  nationDesignerTrigger: Option[TagCondition] = None,
  // hits = 48, isOptional = true, sample = {"NOT":{"is_lesser_in_union":true}}
  trigger: Option[TagCondition] = None,
  // hits = 39, isOptional = true, sample = 0
  duration: Option[Int] = None,
  // hits = 38, isOptional = true, sample = false
  royalMarriage: Option[Boolean] = None,
  // hits = 37, isOptional = true, sample = 3
  fixedRank: Option[Int] = None,
  // hits = 31, isOptional = true, sample = true
  monarchy: Option[Boolean] = None,
  // hits = 31, isOptional = true, sample = true
  republicanName: Option[Boolean] = None,
  // hits = 30, isOptional = true, sample = true
  allowConvert: Option[Boolean] = None,
  // hits = 29, isOptional = true, sample = false
  republic: Option[Boolean] = None,
  // hits = 23, isOptional = true, sample = true
  lockLevelWhenSelected: Boolean = false,
  // hits = 21, isOptional = true, sample = false
  hasTermElection: Seq[Boolean] = Seq.empty,
  // hits = 19, isOptional = true, sample = {"set_country_flag":"populists_in_government","lose_reforms":4,"if":[{"limit":{"technology_group":"nomad_group","OR":{"religion_group":"muslim","secondary_religion":["sunni","shiite","ibadi"]}},"change_technology_group":"muslim","change_unit_type":"muslim"},{"limit":{"technology_group":"nomad_group","religion_group":"christian"},"change_technology_group":"eastern","change_unit_type":"eastern"},{"limit":{"technology_group":"nomad_group","NOT":[{"religion_group":"muslim"},{"religion_group":"christ
  effect: Option[ObjectNode] = None,
  // hits = 16, isOptional = true, sample = ["temples","enuchs","bureaucrats"]
  factions: Seq[String] = Seq.empty,
  // hits = 11, isOptional = true, sample = true
  maintainDynasty: Option[Boolean] = None,
  // hits = 11, isOptional = true, sample = {"if":{"limit":{"has_country_modifier":"look_up_purbias_upgraded"},"remove_country_modifier":"look_up_purbias_upgraded","add_country_modifier":{"name":"look_up_purbias","duration":-1}}}
  removedEffect: Option[TagUpdate] = None,
  // hits = 10, isOptional = true, sample = false
  boostIncome: Option[Boolean] = None,
  // hits = 8, isOptional = true, sample = true
  queen: Option[Boolean] = None,
  // hits = 8, isOptional = true, sample = true
  rulersCanBeGenerals: Seq[Boolean] = Seq.empty,
  // hits = 8, isOptional = true, sample = 0.5
  startTerritoryToEstates: Option[BigDecimal] = None,
  // hits = 8, isOptional = true, sample = true
  tribal: Option[Boolean] = None,
  // hits = 7, isOptional = true, sample = true
  heir: Option[Boolean] = None,
  // hits = 6, isOptional = true, sample = 2
  factionsFrame: Option[Int] = None,
  // hits = 6, isOptional = true, sample = false
  religion: Option[Boolean] = None,
  // hits = 6, isOptional = true, sample = true
  revolutionary: Option[Boolean] = None,
  // hits = 5, isOptional = true, sample = true
  allowMigration: Option[Boolean] = None,
  // hits = 5, isOptional = true, sample = true
  basicReform: Boolean = false,
  // hits = 5, isOptional = true, sample = 0
  differentReligionAcceptance: Option[BigDecimal] = None,
  // hits = 5, isOptional = true, sample = 0
  differentReligionGroupAcceptance: Option[BigDecimal] = None,
  // hits = 5, isOptional = true, sample = false
  monastic: Option[Boolean] = None,
  // hits = 5, isOptional = true, sample = false
  nativeMechanic: Option[Boolean] = None,
  // hits = 5, isOptional = true, sample = true
  nomad: Option[Boolean] = None,
  // hits = 4, isOptional = true, sample = true
  heirsCanBeGenerals: Option[Boolean] = None,
  // hits = 4, isOptional = true, sample = true
  usesRevolutionaryZeal: Option[Boolean] = None,
  // hits = 3, isOptional = true, sample = false
  allowForceTributary: Option[Boolean] = None,
  // hits = 3, isOptional = true, sample = true
  allowVassalAlliance: Option[Boolean] = None,
  // hits = 3, isOptional = true, sample = true
  allowVassalWar: Option[Boolean] = None,
  // hits = 3, isOptional = true, sample = false
  canFormTradeLeague: Boolean = true,
  // hits = 3, isOptional = true, sample = false
  dictatorship: Option[Boolean] = None,
  // hits = 3, isOptional = true, sample = {}
  disallowedTradeGoods: Seq[String] = Seq.empty,
  // hits = 3, isOptional = true, sample = false
  freeCity: Option[Boolean] = None,
  // hits = 3, isOptional = true, sample = false
  isTradingCity: Option[Boolean] = None,
  // hits = 3, isOptional = true, sample = false
  papacy: Option[Boolean] = None,
  // hits = 2, isOptional = true, sample = {"altaic":{"artillery_cost":-0.1},"baltic":{"naval_forcelimit_modifier":0.05},"british":{"ship_durability":0.05},"burman":{"free_leader_pool":1},"byzantine":{"legitimacy":0.25},"carpathian":{"mercenary_cost":-0.1},"caucasian":{"mercenary_discipline":0.05},"gaelic":{"shock_damage":0.05},"central_indic":{"land_attrition":-0.1},"east_asian":{"state_maintenance_modifier":-0.2},"kongo_group":{"heir_chance":0.5},"cushitic":{"missionaries":1},"dravidian":{"global_trade_goods_size_modifier":0.05},"afric
  assimilationCultures: Option[Map[String, Modifier]] = None,
  // hits = 2, isOptional = true, sample = {}
  governmentAbilities: ListSet[String] = ListSet.empty,
  // hits = 2, isOptional = true, sample = false
  hasDevotion: Option[Boolean] = None,
  // hits = 2, isOptional = true, sample = false
  revolutionaryClientState: Option[Boolean] = None,
  // hits = 1, isOptional = true, sample = false
  admiralsBecomeRulers: Boolean = true,
  // hits = 1, isOptional = true, sample = false
  allowBanners: Boolean = true,
  // hits = 1, isOptional = true, sample = false
  canUseTradePost: Boolean = true,
  // hits = 1, isOptional = true, sample = false
  claimStates: Boolean = true,
  // hits = 1, isOptional = true, sample = false
  electionOnDeath: Boolean = true,
  // hits = 1, isOptional = true, sample = false
  forceAdmiralLeader: Boolean = true,
  // hits = 1, isOptional = true, sample = false
  foreignSlaveRulers: Boolean = true,
  // hits = 1, isOptional = true, sample = false
  generalsBecomeRulers: Boolean = true,
  // hits = 1, isOptional = true, sample = false
  hasHarem: Boolean = true,
  // hits = 1, isOptional = true, sample = false
  hasMeritocracy: Boolean = true,
  // hits = 1, isOptional = true, sample = false
  hasParliament: Boolean = true,
  // hits = 1, isOptional = true, sample = false
  hasPashas: Boolean = true,
  // hits = 1, isOptional = true, sample = false
  isElective: Boolean = true,
  // hits = 1, isOptional = true, sample = false
  militarisedSociety: Boolean = true,
  // hits = 1, isOptional = true, sample = 0
  minAutonomy: Option[Int] = None,
  // hits = 1, isOptional = true, sample = {"give_estate_land_share_init":{"estate":"estate_nobles"}}
  postRemovedEffect: Option[ObjectNode] = None,
  // hits = 1, isOptional = true, sample = {}
  statesGeneralMechanic: Option[ObjectNode] = None,
  // hits = 1, isOptional = true, sample = ""
  tradeCityReform: Option[String] = None,
) extends Entity {
  @JsonCreator def this() = this(Entity.UNDEFINED)
}

object GovernmentReform extends FromJson[GovernmentReform]

