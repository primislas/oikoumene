package com.lomicron.oikoumene.model.modifiers

import com.fasterxml.jackson.databind.annotation.{JsonDeserialize, JsonSerialize}
import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.oikoumene.model.MapClassTemplate
import com.lomicron.oikoumene.model.localisation.Localisation
import com.lomicron.utils.json.JsonMapper.ObjectNodeEx
import com.lomicron.utils.json.{FromJson, JsonMapper}

@JsonDeserialize(using = classOf[ModifierDeserializer])
@JsonSerialize(using = classOf[ModifierSerializer])
case class Modifier
(
  id: Option[String] = None,
  localisation: Option[Localisation] = None,
  sourceFile: Option[String] = None,
  conf: ObjectNode = JsonMapper.objectNode
) {

  def name: Option[String] = localisation.flatMap(_.name)

  def add(other: Modifier): Modifier =
    copy(conf = MapClassTemplate.add(conf, other.conf))
  def +(other: Modifier): Modifier = add(other)

  def remove(other: Modifier): Modifier =
    copy(conf = MapClassTemplate.remove(conf, other.conf))
  def -(other: Modifier): Modifier = remove(other)

  // hits = 339, isOptional = true, sample = 0.5
  def prestige: Option[BigDecimal] = conf.getBigDecimal("prestige")
  // hits = 336, isOptional = true, sample = -1
  def globalUnrest: Option[BigDecimal] = conf.getBigDecimal("global_unrest")
  // hits = 281, isOptional = true, sample = 2
  def localUnrest: Option[BigDecimal] = conf.getBigDecimal("local_unrest")
  // hits = 264, isOptional = true, sample = -0.15
  def globalTaxModifier: Option[BigDecimal] = conf.getBigDecimal("global_tax_modifier")
  // hits = 253, isOptional = true, sample = 0.5
  def legitimacy: Option[BigDecimal] = conf.getBigDecimal("legitimacy")
  // hits = 252, isOptional = true, sample = 0.05
  def tradeEfficiency: Option[BigDecimal] = conf.getBigDecimal("trade_efficiency")
  // hits = 248, isOptional = true, sample = 0.05
  def stabilityCostModifier: Option[BigDecimal] = conf.getBigDecimal("stability_cost_modifier")
  // hits = 243, isOptional = true, sample = 0.1
  def landMorale: Option[BigDecimal] = conf.getBigDecimal("land_morale")
  // hits = 214, isOptional = true, sample = -3
  def diplomaticReputation: Option[BigDecimal] = conf.getBigDecimal("diplomatic_reputation")
  // hits = 212, isOptional = true, sample = 1
  def toleranceOwn: Option[BigDecimal] = conf.getBigDecimal("tolerance_own")
  // hits = 186, isOptional = true, sample = 0.25
  def globalManpowerModifier: Option[BigDecimal] = conf.getBigDecimal("global_manpower_modifier")
  // hits = 185, isOptional = true, sample = -0.1
  def globalMissionaryStrength: Option[BigDecimal] = conf.getBigDecimal("global_missionary_strength")
  // hits = 182, isOptional = true, sample = true
  def religion: Option[Boolean] = conf.getBoolean("religion")
  // hits = 152, isOptional = true, sample = -0.33
  def localTaxModifier: Option[BigDecimal] = conf.getBigDecimal("local_tax_modifier")
  // hits = 147, isOptional = true, sample = 0.05
  def globalAutonomy: Option[BigDecimal] = conf.getBigDecimal("global_autonomy")
  // hits = 146, isOptional = true, sample = 0.25
  def defensiveness: Option[BigDecimal] = conf.getBigDecimal("defensiveness")
  // hits = 144, isOptional = true, sample = -0.15
  def localDevelopmentCost: Option[BigDecimal] = conf.getBigDecimal("local_development_cost")
  // hits = 144, isOptional = true, sample = 0.10
  def productionEfficiency: Option[BigDecimal] = conf.getBigDecimal("production_efficiency")
  // hits = 115, isOptional = true, sample = -0.25
  def globalTradeGoodsSizeModifier: Option[BigDecimal] = conf.getBigDecimal("global_trade_goods_size_modifier")
  // hits = 113, isOptional = true, sample = 0.1
  def technologyCost: Option[BigDecimal] = conf.getBigDecimal("technology_cost")
  // hits = 111, isOptional = true, sample = 0.05
  def discipline: Option[BigDecimal] = conf.getBigDecimal("discipline")
  // hits = 110, isOptional = true, sample = 0.05
  def ideaCost: Option[BigDecimal] = conf.getBigDecimal("idea_cost")
  // hits = 110, isOptional = true, sample = 1
  def toleranceHeretic: Option[BigDecimal] = conf.getBigDecimal("tolerance_heretic")
  // hits = 102, isOptional = true, sample = -0.1
  def developmentCost: Option[BigDecimal] = conf.getBigDecimal("development_cost")
  // hits = 102, isOptional = true, sample = -0.1
  def localProductionEfficiency: Option[BigDecimal] = conf.getBigDecimal("local_production_efficiency")
  // hits = 95, isOptional = true, sample = -0.2
  def globalTradePower: Option[BigDecimal] = conf.getBigDecimal("global_trade_power")
  // hits = 91, isOptional = true, sample = 1
  def diplomaticUpkeep: Option[BigDecimal] = conf.getBigDecimal("diplomatic_upkeep")
  // hits = 91, isOptional = true, sample = 0.05
  def localAutonomy: Option[BigDecimal] = conf.getBigDecimal("local_autonomy")
  // hits = 91, isOptional = true, sample = 0.15
  def manpowerRecoverySpeed: Option[BigDecimal] = conf.getBigDecimal("manpower_recovery_speed")
  // hits = 91, isOptional = true, sample = 15
  def provinceTradePowerValue: Option[BigDecimal] = conf.getBigDecimal("province_trade_power_value")
  // hits = 85, isOptional = true, sample = -0.05
  def globalInstitutionSpread: Option[BigDecimal] = conf.getBigDecimal("global_institution_spread")
  // hits = 85, isOptional = true, sample = 1.0
  def tradeGoodsSize: Option[BigDecimal] = conf.getBigDecimal("trade_goods_size")
  // hits = 84, isOptional = true, sample = -0.4
  def localManpowerModifier: Option[BigDecimal] = conf.getBigDecimal("local_manpower_modifier")
  // hits = 83, isOptional = true, sample = 0.03
  def localMissionaryStrength: Option[BigDecimal] = conf.getBigDecimal("local_missionary_strength")
  // hits = 81, isOptional = true, sample = 0.1
  def infantryPower: Option[BigDecimal] = conf.getBigDecimal("infantry_power")
  // hits = 79, isOptional = true, sample = -2
  def toleranceHeathen: Option[BigDecimal] = conf.getBigDecimal("tolerance_heathen")
  // hits = 74, isOptional = true, sample = 0.5
  def advisorCost: Option[BigDecimal] = conf.getBigDecimal("advisor_cost")
  // hits = 73, isOptional = true, sample = 0.1
  def buildCost: Option[BigDecimal] = conf.getBigDecimal("build_cost")
  // hits = 69, isOptional = true, sample = "province_trade_power_value"
  def picture: Option[String] = conf.getString("picture")
  // hits = 68, isOptional = true, sample = 0.10
  def cavalryPower: Option[BigDecimal] = conf.getBigDecimal("cavalry_power")
  // hits = 67, isOptional = true, sample = 150
  def governingCapacity: Option[BigDecimal] = conf.getBigDecimal("governing_capacity")
  // hits = 67, isOptional = true, sample = 20
  def maxAbsolutism: Option[BigDecimal] = conf.getBigDecimal("max_absolutism")
  // hits = 65, isOptional = true, sample = 0.05
  def aeImpact: Option[BigDecimal] = conf.getBigDecimal("ae_impact")
  // hits = 64, isOptional = true, sample = -0.10
  def improveRelationModifier: Option[BigDecimal] = conf.getBigDecimal("improve_relation_modifier")
  // hits = 62, isOptional = true, sample = -0.05
  def coreCreation: Option[BigDecimal] = conf.getBigDecimal("core_creation")
  // hits = 61, isOptional = true, sample = 0.2
  def republicanTradition: Option[BigDecimal] = conf.getBigDecimal("republican_tradition")
  // hits = 60, isOptional = true, sample = 0.25
  def cavalryCost: Option[BigDecimal] = conf.getBigDecimal("cavalry_cost")
  // hits = 60, isOptional = true, sample = 0.1
  def localDefensiveness: Option[BigDecimal] = conf.getBigDecimal("local_defensiveness")
  // hits = 58, isOptional = true, sample = 1
  def armyTradition: Option[BigDecimal] = conf.getBigDecimal("army_tradition")
  // hits = 58, isOptional = true, sample = 1
  def merchants: Option[Int] = conf.getInt("merchants")
  // hits = 57, isOptional = true, sample = -0.25
  def tradeGoodsSizeModifier: Option[BigDecimal] = conf.getBigDecimal("trade_goods_size_modifier")
  // hits = 56, isOptional = true, sample = -0.10
  def dipTechCostModifier: Option[BigDecimal] = conf.getBigDecimal("dip_tech_cost_modifier")
  // hits = 54, isOptional = true, sample = -1
  def papalInfluence: Option[BigDecimal] = conf.getBigDecimal("papal_influence")
  // hits = 52, isOptional = true, sample = 1
  def numAcceptedCultures: Option[Int] = conf.getInt("num_accepted_cultures")
  // hits = 51, isOptional = true, sample = -5
  def globalColonialGrowth: Option[BigDecimal] = conf.getBigDecimal("global_colonial_growth")
  // hits = 51, isOptional = true, sample = 0.1
  def tradeSteering: Option[BigDecimal] = conf.getBigDecimal("trade_steering")
  // hits = 50, isOptional = true, sample = 0.1
  def admTechCostModifier: Option[BigDecimal] = conf.getBigDecimal("adm_tech_cost_modifier")
  // hits = 49, isOptional = true, sample = -1
  def diplomats: Option[BigDecimal] = conf.getBigDecimal("diplomats")
  // hits = 49, isOptional = true, sample = 0.05
  def globalOwnTradePower: Option[BigDecimal] = conf.getBigDecimal("global_own_trade_power")
  // hits = 49, isOptional = true, sample = 0.25
  def provinceTradePowerModifier: Option[BigDecimal] = conf.getBigDecimal("province_trade_power_modifier")
  // hits = 47, isOptional = true, sample = 0.5
  def hostileAttrition: Option[BigDecimal] = conf.getBigDecimal("hostile_attrition")
  // hits = 45, isOptional = true, sample = -0.1
  def devotion: Option[BigDecimal] = conf.getBigDecimal("devotion")
  // hits = 44, isOptional = true, sample = -0.1
  def mercMaintenanceModifier: Option[BigDecimal] = conf.getBigDecimal("merc_maintenance_modifier")
  // hits = 44, isOptional = true, sample = -0.10
  def navalMorale: Option[BigDecimal] = conf.getBigDecimal("naval_morale")
  // hits = 44, isOptional = true, sample = 0.25
  def religiousUnity: Option[BigDecimal] = conf.getBigDecimal("religious_unity")
  // hits = 42, isOptional = true, sample = 0.02
  def globalHereticMissionaryStrength: Option[BigDecimal] = conf.getBigDecimal("global_heretic_missionary_strength")
  // hits = 40, isOptional = true, sample = 0.1
  def landForcelimitModifier: Option[BigDecimal] = conf.getBigDecimal("land_forcelimit_modifier")
  // hits = 40, isOptional = true, sample = 0.15
  def spyOffence: Option[BigDecimal] = conf.getBigDecimal("spy_offence")
  // hits = 39, isOptional = true, sample = 0.1
  def globalProvTradePowerModifier: Option[BigDecimal] = conf.getBigDecimal("global_prov_trade_power_modifier")
  // hits = 39, isOptional = true, sample = -0.5
  def interest: Option[BigDecimal] = conf.getBigDecimal("interest")
  // hits = 39, isOptional = true, sample = 0.10
  def navalForcelimitModifier: Option[BigDecimal] = conf.getBigDecimal("naval_forcelimit_modifier")
  // hits = 38, isOptional = true, sample = 0.25
  def privateerEfficiency: Option[BigDecimal] = conf.getBigDecimal("privateer_efficiency")
  // hits = 37, isOptional = true, sample = 0.1
  def infantryCost: Option[BigDecimal] = conf.getBigDecimal("infantry_cost")
  // hits = 36, isOptional = true, sample = -0.05
  def globalShipCost: Option[BigDecimal] = conf.getBigDecimal("global_ship_cost")
  // hits = 36, isOptional = true, sample = 0.15
  def globalSpyDefence: Option[BigDecimal] = conf.getBigDecimal("global_spy_defence")
  // hits = 35, isOptional = true, sample = 1
  def advisorPool: Option[Int] = conf.getInt("advisor_pool")
  // hits = 35, isOptional = true, sample = 0.10
  def fortMaintenanceModifier: Option[BigDecimal] = conf.getBigDecimal("fort_maintenance_modifier")
  // hits = 34, isOptional = true, sample = -0.10
  def landMaintenanceModifier: Option[BigDecimal] = conf.getBigDecimal("land_maintenance_modifier")
  // hits = 32, isOptional = true, sample = 0.50
  def heirChance: Option[BigDecimal] = conf.getBigDecimal("heir_chance")
  // hits = 31, isOptional = true, sample = 0.05
  def inflationReduction: Option[BigDecimal] = conf.getBigDecimal("inflation_reduction")
  // hits = 31, isOptional = true, sample = -0.25
  def milTechCostModifier: Option[BigDecimal] = conf.getBigDecimal("mil_tech_cost_modifier")
  // hits = 30, isOptional = true, sample = -0.1
  def yearlyCorruption: Option[BigDecimal] = conf.getBigDecimal("yearly_corruption")
  // hits = 29, isOptional = true, sample = 0.1
  def fireDamage: Option[BigDecimal] = conf.getBigDecimal("fire_damage")
  // hits = 29, isOptional = true, sample = -0.25
  def globalTariffs: Option[BigDecimal] = conf.getBigDecimal("global_tariffs")
  // hits = 29, isOptional = true, sample = -0.10
  def landAttrition: Option[BigDecimal] = conf.getBigDecimal("land_attrition")
  // hits = 29, isOptional = true, sample = 0.25
  def navyTradition: Option[BigDecimal] = conf.getBigDecimal("navy_tradition")
  // hits = 29, isOptional = true, sample = -0.01
  def prestigeDecay: Option[BigDecimal] = conf.getBigDecimal("prestige_decay")
  // hits = 27, isOptional = true, sample = -0.75
  def churchPowerModifier: Option[BigDecimal] = conf.getBigDecimal("church_power_modifier")
  // hits = 27, isOptional = true, sample = 1
  def leaderLandShock: Option[Int] = conf.getInt("leader_land_shock")
  // hits = 27, isOptional = true, sample = -0.1
  def stateMaintenanceModifier: Option[BigDecimal] = conf.getBigDecimal("state_maintenance_modifier")
  // hits = 27, isOptional = true, sample = -5
  def yearsOfNationalism: Option[BigDecimal] = conf.getBigDecimal("years_of_nationalism")
  // hits = 26, isOptional = true, sample = -0.15
  def localBuildCost: Option[BigDecimal] = conf.getBigDecimal("local_build_cost")
  // hits = 26, isOptional = true, sample = 2
  def missionaries: Option[Int] = conf.getInt("missionaries")
  // hits = 25, isOptional = true, sample = 0.20
  def globalSailorsModifier: Option[BigDecimal] = conf.getBigDecimal("global_sailors_modifier")
  // hits = 25, isOptional = true, sample = 1
  def hordeUnity: Option[BigDecimal] = conf.getBigDecimal("horde_unity")
  // hits = 25, isOptional = true, sample = -0.05
  def navalMaintenanceModifier: Option[BigDecimal] = conf.getBigDecimal("naval_maintenance_modifier")
  // hits = 25, isOptional = true, sample = 0.25
  def tradeRangeModifier: Option[BigDecimal] = conf.getBigDecimal("trade_range_modifier")
  // hits = 24, isOptional = true, sample = 0.10
  def diplomaticAnnexationCost: Option[BigDecimal] = conf.getBigDecimal("diplomatic_annexation_cost")
  // hits = 23, isOptional = true, sample = -0.05
  def globalShipRecruitSpeed: Option[BigDecimal] = conf.getBigDecimal("global_ship_recruit_speed")
  // hits = 23, isOptional = true, sample = 0.5
  def mercenaryManpower: Option[BigDecimal] = conf.getBigDecimal("mercenary_manpower")
  // hits = 23, isOptional = true, sample = 0.1
  def warExhaustion: Option[BigDecimal] = conf.getBigDecimal("war_exhaustion")
  // hits = 22, isOptional = true, sample = 0.15
  def movementSpeed: Option[BigDecimal] = conf.getBigDecimal("movement_speed")
  // hits = 22, isOptional = true, sample = 0.10
  def shipDurability: Option[BigDecimal] = conf.getBigDecimal("ship_durability")
  // hits = 20, isOptional = true, sample = 0.33
  def caravanPower: Option[BigDecimal] = conf.getBigDecimal("caravan_power")
  // hits = 20, isOptional = true, sample = 0.1
  def galleyPower: Option[BigDecimal] = conf.getBigDecimal("galley_power")
  // hits = 19, isOptional = true, sample = -0.05
  def cultureConversionCost: Option[BigDecimal] = conf.getBigDecimal("culture_conversion_cost")
  // hits = 19, isOptional = true, sample = 0.10
  def lightShipCost: Option[BigDecimal] = conf.getBigDecimal("light_ship_cost")
  // hits = 18, isOptional = true, sample = 0.5
  def fabricateClaimsCost: Option[BigDecimal] = conf.getBigDecimal("fabricate_claims_cost")
  // hits = 18, isOptional = true, sample = 0.2
  def globalShipTradePower: Option[BigDecimal] = conf.getBigDecimal("global_ship_trade_power")
  // hits = 18, isOptional = true, sample = 1
  def leaderLandManuever: Option[Int] = conf.getInt("leader_land_manuever")
  // hits = 18, isOptional = true, sample = 0.25
  def localInstitutionSpread: Option[BigDecimal] = conf.getBigDecimal("local_institution_spread")
  // hits = 17, isOptional = true, sample = 0.05
  def administrativeEfficiency: Option[BigDecimal] = conf.getBigDecimal("administrative_efficiency")
  // hits = 17, isOptional = true, sample = 0.25
  def garrisonSize: Option[BigDecimal] = conf.getBigDecimal("garrison_size")
  // hits = 17, isOptional = true, sample = -0.1
  def globalRegimentRecruitSpeed: Option[BigDecimal] = conf.getBigDecimal("global_regiment_recruit_speed")
  // hits = 17, isOptional = true, sample = -0.8
  def localStateMaintenanceModifier: Option[BigDecimal] = conf.getBigDecimal("local_state_maintenance_modifier")
  // hits = 17, isOptional = true, sample = -1
  def monthlyFervorIncrease: Option[BigDecimal] = conf.getBigDecimal("monthly_fervor_increase")
  // hits = 17, isOptional = true, sample = 0.25
  def range: Option[BigDecimal] = conf.getBigDecimal("range")
  // hits = 17, isOptional = true, sample = 0.10
  def vassalIncome: Option[BigDecimal] = conf.getBigDecimal("vassal_income")
  // hits = 16, isOptional = true, sample = -0.05
  def colonistPlacementChance: Option[BigDecimal] = conf.getBigDecimal("colonist_placement_chance")
  // hits = 16, isOptional = true, sample = 1
  def colonists: Option[Int] = conf.getInt("colonists")
  // hits = 16, isOptional = true, sample = -0.20
  def envoyTravelTime: Option[BigDecimal] = conf.getBigDecimal("envoy_travel_time")
  // hits = 16, isOptional = true, sample = 0.15
  def shockDamage: Option[BigDecimal] = conf.getBigDecimal("shock_damage")
  // hits = 16, isOptional = true, sample = 0.10
  def siegeAbility: Option[BigDecimal] = conf.getBigDecimal("siege_ability")
  // hits = 15, isOptional = true, sample = -0.01
  def armyTraditionDecay: Option[BigDecimal] = conf.getBigDecimal("army_tradition_decay")
  // hits = 15, isOptional = true, sample = 1
  def freeLeaderPool: Option[BigDecimal] = conf.getBigDecimal("free_leader_pool")
  // hits = 15, isOptional = true, sample = -0.05
  def globalForeignTradePower: Option[BigDecimal] = conf.getBigDecimal("global_foreign_trade_power")
  // hits = 15, isOptional = true, sample = 0.05
  def mercenaryDiscipline: Option[BigDecimal] = conf.getBigDecimal("mercenary_discipline")
  // hits = 15, isOptional = true, sample = -0.20
  def unjustifiedDemands: Option[BigDecimal] = conf.getBigDecimal("unjustified_demands")
  // hits = 14, isOptional = true, sample = 0.25
  def captureShipChance: Option[BigDecimal] = conf.getBigDecimal("capture_ship_chance")
  // hits = 14, isOptional = true, sample = 8
  def globalTaxIncome: Option[BigDecimal] = conf.getBigDecimal("global_tax_income")
  // hits = 14, isOptional = true, sample = -0.1
  def noblesInfluenceModifier: Option[BigDecimal] = conf.getBigDecimal("nobles_influence_modifier")
  // hits = 14, isOptional = true, sample = 0.03
  def prestigeFromLand: Option[BigDecimal] = conf.getBigDecimal("prestige_from_land")
  // hits = 14, isOptional = true, sample = -0.1
  def provinceWarscoreCost: Option[BigDecimal] = conf.getBigDecimal("province_warscore_cost")
  // hits = 14, isOptional = true, sample = 0.5
  def yearlyAbsolutism: Option[BigDecimal] = conf.getBigDecimal("yearly_absolutism")
  // hits = 13, isOptional = true, sample = 0.05
  def heavyShipPower: Option[BigDecimal] = conf.getBigDecimal("heavy_ship_power")
  // hits = 13, isOptional = true, sample = 0.15
  def lootAmount: Option[BigDecimal] = conf.getBigDecimal("loot_amount")
  // hits = 13, isOptional = true, sample = -0.1
  def noblesLoyaltyModifier: Option[BigDecimal] = conf.getBigDecimal("nobles_loyalty_modifier")
  // hits = 13, isOptional = true, sample = -0.1
  def shockDamageReceived: Option[BigDecimal] = conf.getBigDecimal("shock_damage_received")
  // hits = 12, isOptional = true, sample = 0.05
  def burghersLoyaltyModifier: Option[BigDecimal] = conf.getBigDecimal("burghers_loyalty_modifier")
  // hits = 12, isOptional = true, sample = 0.05
  def churchLoyaltyModifier: Option[BigDecimal] = conf.getBigDecimal("church_loyalty_modifier")
  // hits = 12, isOptional = true, sample = 0.1
  def embracementCost: Option[BigDecimal] = conf.getBigDecimal("embracement_cost")
  // hits = 12, isOptional = true, sample = 0.1
  def governingCapacityModifier: Option[BigDecimal] = conf.getBigDecimal("governing_capacity_modifier")
  // hits = 12, isOptional = true, sample = 25
  def localColonialGrowth: Option[BigDecimal] = conf.getBigDecimal("local_colonial_growth")
  // hits = 12, isOptional = true, sample = -0.25
  def sameCultureAdvisorCost: Option[BigDecimal] = conf.getBigDecimal("same_culture_advisor_cost")
  // hits = 11, isOptional = true, sample = 0.1
  def embargoEfficiency: Option[BigDecimal] = conf.getBigDecimal("embargo_efficiency")
  // hits = 11, isOptional = true, sample = 0.25
  def reinforceCostModifier: Option[BigDecimal] = conf.getBigDecimal("reinforce_cost_modifier")
  // hits = 11, isOptional = true, sample = -0.1
  def sailorMaintenanceModifer: Option[BigDecimal] = conf.getBigDecimal("sailor_maintenance_modifer")
  // hits = 11, isOptional = true, sample = 0.20
  def tradeValueModifier: Option[BigDecimal] = conf.getBigDecimal("trade_value_modifier")
  // hits = 10, isOptional = true, sample = 0.5
  def localCultureConversionCost: Option[BigDecimal] = conf.getBigDecimal("local_culture_conversion_cost")
  // hits = 10, isOptional = true, sample = -0.5
  def localHostileMovementSpeed: Option[BigDecimal] = conf.getBigDecimal("local_hostile_movement_speed")
  // hits = 10, isOptional = true, sample = -0.33
  def missionaryMaintenanceCost: Option[BigDecimal] = conf.getBigDecimal("missionary_maintenance_cost")
  // hits = 10, isOptional = true, sample = -0.001
  def monthlyPiety: Option[BigDecimal] = conf.getBigDecimal("monthly_piety")
  // hits = 10, isOptional = true, sample = 5
  def reducedLibertyDesire: Option[BigDecimal] = conf.getBigDecimal("reduced_liberty_desire")
  // hits = 10, isOptional = true, sample = -0.20
  def warExhaustionCost: Option[BigDecimal] = conf.getBigDecimal("war_exhaustion_cost")
  // hits = 9, isOptional = true, sample = 0.05
  def artilleryPower: Option[BigDecimal] = conf.getBigDecimal("artillery_power")
  // hits = 9, isOptional = true, sample = 0.25
  def cavToInfRatio: Option[BigDecimal] = conf.getBigDecimal("cav_to_inf_ratio")
  // hits = 9, isOptional = true, sample = -0.33
  def harshTreatmentCost: Option[BigDecimal] = conf.getBigDecimal("harsh_treatment_cost")
  // hits = 9, isOptional = true, sample = 1
  def leaderNavalFire: Option[Int] = conf.getInt("leader_naval_fire")
  // hits = 9, isOptional = true, sample = -1
  def libertyDesireFromSubjectDevelopment: Option[BigDecimal] = conf.getBigDecimal("liberty_desire_from_subject_development")
  // hits = 9, isOptional = true, sample = 50
  def maxRevolutionaryZeal: Option[Int] = conf.getInt("max_revolutionary_zeal")
  // hits = 9, isOptional = true, sample = 0.1
  def reformProgressGrowth: Option[BigDecimal] = conf.getBigDecimal("reform_progress_growth")
  // hits = 8, isOptional = true, sample = -0.25
  def imperialAuthorityValue: Option[BigDecimal] = conf.getBigDecimal("imperial_authority_value")
  // hits = 8, isOptional = true, sample = 0.05
  def lightShipPower: Option[BigDecimal] = conf.getBigDecimal("light_ship_power")
  // hits = 8, isOptional = true, sample = 0.03
  def localMonthlyDevastation: Option[BigDecimal] = conf.getBigDecimal("local_monthly_devastation")
  // hits = 8, isOptional = true, sample = 0.1
  def mercenaryCost: Option[BigDecimal] = conf.getBigDecimal("mercenary_cost")
  // hits = 8, isOptional = true, sample = -0.2
  def nativeUprisingChance: Option[BigDecimal] = conf.getBigDecimal("native_uprising_chance")
  // hits = 8, isOptional = true, sample = 0.2
  def recoverArmyMoraleSpeed: Option[BigDecimal] = conf.getBigDecimal("recover_army_morale_speed")
  // hits = 8, isOptional = true, sample = 0.15
  def reinforceSpeed: Option[BigDecimal] = conf.getBigDecimal("reinforce_speed")
  // hits = 8, isOptional = true, sample = 1
  def vassalForcelimitBonus: Option[Int] = conf.getInt("vassal_forcelimit_bonus")
  // hits = 7, isOptional = true, sample = 0.5
  def armyTraditionFromBattle: Option[BigDecimal] = conf.getBigDecimal("army_tradition_from_battle")
  // hits = 7, isOptional = true, sample = 0.20
  def artilleryCost: Option[BigDecimal] = conf.getBigDecimal("artillery_cost")
  // hits = 7, isOptional = true, sample = 0.05
  def burghersInfluenceModifier: Option[BigDecimal] = conf.getBigDecimal("burghers_influence_modifier")
  // hits = 7, isOptional = true, sample = 0.25
  def cavalryFlanking: Option[BigDecimal] = conf.getBigDecimal("cavalry_flanking")
  // hits = 7, isOptional = true, sample = 0.2
  def femaleAdvisorChance: Option[BigDecimal] = conf.getBigDecimal("female_advisor_chance")
  // hits = 7, isOptional = true, sample = -0.1
  def galleyCost: Option[BigDecimal] = conf.getBigDecimal("galley_cost")
  // hits = 7, isOptional = true, sample = true
  def isJanissaryModifier: Option[Boolean] = conf.getBoolean("is_janissary_modifier")
  // hits = 7, isOptional = true, sample = 1
  def leaderLandFire: Option[Int] = conf.getInt("leader_land_fire")
  // hits = 7, isOptional = true, sample = 1
  def leaderNavalManuever: Option[Int] = conf.getInt("leader_naval_manuever")
  // hits = 7, isOptional = true, sample = 0.05
  def marathaExclusiveInfluenceModifier: Option[BigDecimal] = conf.getBigDecimal("maratha_exclusive_influence_modifier")
  // hits = 7, isOptional = true, sample = 0.4
  def nativeAssimilation: Option[BigDecimal] = conf.getBigDecimal("native_assimilation")
  // hits = 7, isOptional = true, sample = 0.05
  def rajputExclusiveInfluenceModifier: Option[BigDecimal] = conf.getBigDecimal("rajput_exclusive_influence_modifier")
  // hits = 7, isOptional = true, sample = 0.5
  def yearlyRevolutionaryZeal: Option[BigDecimal] = conf.getBigDecimal("yearly_revolutionary_zeal")
  // hits = 6, isOptional = true, sample = -0.25
  def buildTime: Option[BigDecimal] = conf.getBigDecimal("build_time")
  // hits = 6, isOptional = true, sample = -0.25
  def drillDecayModifier: Option[BigDecimal] = conf.getBigDecimal("drill_decay_modifier")
  // hits = 6, isOptional = true, sample = 0.5
  def drillGainModifier: Option[BigDecimal] = conf.getBigDecimal("drill_gain_modifier")
  // hits = 6, isOptional = true, sample = -0.1
  def globalRegimentCost: Option[BigDecimal] = conf.getBigDecimal("global_regiment_cost")
  // hits = 6, isOptional = true, sample = 0.10
  def heavyShipCost: Option[BigDecimal] = conf.getBigDecimal("heavy_ship_cost")
  // hits = 6, isOptional = true, sample = -0.05
  def imperialAuthority: Option[BigDecimal] = conf.getBigDecimal("imperial_authority")
  // hits = 6, isOptional = true, sample = 0.05
  def imperialMandate: Option[BigDecimal] = conf.getBigDecimal("imperial_mandate")
  // hits = 6, isOptional = true, sample = 0.25
  def localSailorsModifier: Option[BigDecimal] = conf.getBigDecimal("local_sailors_modifier")
  // hits = 6, isOptional = true, sample = -1
  def meritocracy: Option[BigDecimal] = conf.getBigDecimal("meritocracy")
  // hits = 6, isOptional = true, sample = -0.90
  def navalAttrition: Option[BigDecimal] = conf.getBigDecimal("naval_attrition")
  // hits = 6, isOptional = true, sample = -0.01
  def navyTraditionDecay: Option[BigDecimal] = conf.getBigDecimal("navy_tradition_decay")
  // hits = 6, isOptional = true, sample = -0.25
  def promoteCultureCost: Option[BigDecimal] = conf.getBigDecimal("promote_culture_cost")
  // hits = 6, isOptional = true, sample = 0.5
  def rebelSupportEfficiency: Option[BigDecimal] = conf.getBigDecimal("rebel_support_efficiency")
  // hits = 6, isOptional = true, sample = 1
  def sailorsRecoverySpeed: Option[BigDecimal] = conf.getBigDecimal("sailors_recovery_speed")
  // hits = 6, isOptional = true, sample = -0.1
  def shipRecruitSpeed: Option[BigDecimal] = conf.getBigDecimal("ship_recruit_speed")
  // hits = 5, isOptional = true, sample = 0.05
  def allowedMarineFraction: Option[BigDecimal] = conf.getBigDecimal("allowed_marine_fraction")
  // hits = 5, isOptional = true, sample = 0.20
  def blockadeEfficiency: Option[BigDecimal] = conf.getBigDecimal("blockade_efficiency")
  // hits = 5, isOptional = true, sample = -1
  def curiaTreasuryContribution: Option[BigDecimal] = conf.getBigDecimal("curia_treasury_contribution")
  // hits = 5, isOptional = true, sample = -0.1
  def fireDamageReceived: Option[BigDecimal] = conf.getBigDecimal("fire_damage_received")
  // hits = 5, isOptional = true, sample = 2
  def fortLevel: Option[Int] = conf.getInt("fort_level")
  // hits = 5, isOptional = true, sample = true
  def isRajputModifier: Option[Boolean] = conf.getBoolean("is_rajput_modifier")
  // hits = 5, isOptional = true, sample = 0.15
  def localShipRepair: Option[BigDecimal] = conf.getBigDecimal("local_ship_repair")
  // hits = 5, isOptional = true, sample = true
  def mayPerformSlaveRaidOnSameReligion: Option[Boolean] = conf.getBoolean("may_perform_slave_raid_on_same_religion")
  // hits = 5, isOptional = true, sample = 1
  def monarchMilitaryPower: Option[Int] = conf.getInt("monarch_military_power")
  // hits = 5, isOptional = true, sample = -0.5
  def supplyLimitModifier: Option[BigDecimal] = conf.getBigDecimal("supply_limit_modifier")
  // hits = 4, isOptional = true, sample = 1
  def allowedNumOfBuildings: Option[Int] = conf.getInt("allowed_num_of_buildings")
  // hits = 4, isOptional = true, sample = -0.1
  def churchInfluenceModifier: Option[BigDecimal] = conf.getBigDecimal("church_influence_modifier")
  // hits = 4, isOptional = true, sample = -1
  def electionCycle: Option[BigDecimal] = conf.getBigDecimal("election_cycle")
  // hits = 4, isOptional = true, sample = 1
  def freeAdmPolicy: Option[Int] = conf.getInt("free_adm_policy")
  // hits = 4, isOptional = true, sample = 0.15
  def globalGarrisonGrowth: Option[BigDecimal] = conf.getBigDecimal("global_garrison_growth")
  // hits = 4, isOptional = true, sample = true
  def isMercenaryModifier: Option[Boolean] = conf.getBoolean("is_mercenary_modifier")
  // hits = 4, isOptional = true, sample = true
  def isRevolutionaryGuardModifier: Option[Boolean] = conf.getBoolean("is_revolutionary_guard_modifier")
  // hits = 4, isOptional = true, sample = -0.15
  def localShipCost: Option[BigDecimal] = conf.getBigDecimal("local_ship_cost")
  // hits = 4, isOptional = true, sample = true
  def mayRecruitFemaleGenerals: Option[Boolean] = conf.getBoolean("may_recruit_female_generals")
  // hits = 4, isOptional = true, sample = 2
  def monarchDiplomaticPower: Option[Int] = conf.getInt("monarch_diplomatic_power")
  // hits = 4, isOptional = true, sample = 2
  def navalForcelimit: Option[Int] = conf.getInt("naval_forcelimit")
  // hits = 4, isOptional = true, sample = true
  def noReligionPenalty: Option[Boolean] = conf.getBoolean("no_religion_penalty")
  // hits = 4, isOptional = true, sample = 1
  def powerProjectionFromInsults: Option[Int] = conf.getInt("power_projection_from_insults")
  // hits = 4, isOptional = true, sample = 0.1
  def prCaptainsInfluence: Option[BigDecimal] = conf.getBigDecimal("pr_captains_influence")
  // hits = 4, isOptional = true, sample = 0.2
  def shipPowerPropagation: Option[BigDecimal] = conf.getBigDecimal("ship_power_propagation")
  // hits = 4, isOptional = true, sample = -0.33
  def sunkShipMoraleHitRecieved: Option[BigDecimal] = conf.getBigDecimal("sunk_ship_morale_hit_recieved")
  // hits = 4, isOptional = true, sample = 5
  def taxIncome: Option[Int] = conf.getInt("tax_income")
  // hits = 4, isOptional = true, sample = -0.2
  def tradeCompanyInvestmentCost: Option[BigDecimal] = conf.getBigDecimal("trade_company_investment_cost")
  // hits = 4, isOptional = true, sample = -0.25
  def warscoreCostVsOtherReligion: Option[BigDecimal] = conf.getBigDecimal("warscore_cost_vs_other_religion")
  // hits = 3, isOptional = true, sample = 0.15
  def amountOfBanners: Option[BigDecimal] = conf.getBigDecimal("amount_of_banners")
  // hits = 3, isOptional = true, sample = -0.5
  def appointCardinalCost: Option[BigDecimal] = conf.getBigDecimal("appoint_cardinal_cost")
  // hits = 3, isOptional = true, sample = -0.25
  def autonomyChangeTime: Option[BigDecimal] = conf.getBigDecimal("autonomy_change_time")
  // hits = 3, isOptional = true, sample = true
  def cbOnReligiousEnemies: Option[Boolean] = conf.getBoolean("cb_on_religious_enemies")
  // hits = 3, isOptional = true, sample = 0.05
  def disengagementChance: Option[BigDecimal] = conf.getBigDecimal("disengagement_chance")
  // hits = 3, isOptional = true, sample = 0.5
  def enemyCoreCreation: Option[BigDecimal] = conf.getBigDecimal("enemy_core_creation")
  // hits = 3, isOptional = true, sample = -0.25
  def expelMinoritiesCost: Option[BigDecimal] = conf.getBigDecimal("expel_minorities_cost")
  // hits = 3, isOptional = true, sample = 1
  def freePolicy: Option[Int] = conf.getInt("free_policy")
  // hits = 3, isOptional = true, sample = -0.1
  def justifyTradeConflictCost: Option[BigDecimal] = conf.getBigDecimal("justify_trade_conflict_cost")
  // hits = 3, isOptional = true, sample = 1
  def landForcelimit: Option[Int] = conf.getInt("land_forcelimit")
  // hits = 3, isOptional = true, sample = -0.25
  def leaderCost: Option[BigDecimal] = conf.getBigDecimal("leader_cost")
  // hits = 3, isOptional = true, sample = 50
  def libertyDesire: Option[BigDecimal] = conf.getBigDecimal("liberty_desire")
  // hits = 3, isOptional = true, sample = 0.1
  def localFriendlyMovementSpeed: Option[BigDecimal] = conf.getBigDecimal("local_friendly_movement_speed")
  // hits = 3, isOptional = true, sample = -0.5
  def localMissionaryMaintenanceCost: Option[BigDecimal] = conf.getBigDecimal("local_missionary_maintenance_cost")
  // hits = 3, isOptional = true, sample = 0.1
  def marathaExclusiveLoyaltyModifier: Option[BigDecimal] = conf.getBigDecimal("maratha_exclusive_loyalty_modifier")
  // hits = 3, isOptional = true, sample = -0.10
  def minAutonomyInTerritories: Option[BigDecimal] = conf.getBigDecimal("min_autonomy_in_territories")
  // hits = 3, isOptional = true, sample = 50
  def minLocalAutonomy: Option[BigDecimal] = conf.getBigDecimal("min_local_autonomy")
  // hits = 3, isOptional = true, sample = 2
  def monarchAdminPower: Option[Int] = conf.getInt("monarch_admin_power")
  // hits = 3, isOptional = true, sample = 3.0
  def monthlySplendor: Option[BigDecimal] = conf.getBigDecimal("monthly_splendor")
  // hits = 3, isOptional = true, sample = 0.02
  def mrTradersInfluence: Option[BigDecimal] = conf.getBigDecimal("mr_traders_influence")
  // hits = 3, isOptional = true, sample = 10
  def placedMerchantPower: Option[Int] = conf.getInt("placed_merchant_power")
  // hits = 3, isOptional = true, sample = 1
  def possibleDipPolicy: Option[Int] = conf.getInt("possible_dip_policy")
  // hits = 3, isOptional = true, sample = 0.1
  def prBuccaneersInfluence: Option[BigDecimal] = conf.getBigDecimal("pr_buccaneers_influence")
  // hits = 3, isOptional = true, sample = 0.1
  def prSmugglersInfluence: Option[BigDecimal] = conf.getBigDecimal("pr_smugglers_influence")
  // hits = 3, isOptional = true, sample = 0.1
  def rajputExclusiveLoyaltyModifier: Option[BigDecimal] = conf.getBigDecimal("rajput_exclusive_loyalty_modifier")
  // hits = 3, isOptional = true, sample = 0.2
  def regimentRecruitSpeed: Option[BigDecimal] = conf.getBigDecimal("regiment_recruit_speed")
  // hits = 3, isOptional = true, sample = -0.5
  def rivalBorderFortMaintenance: Option[BigDecimal] = conf.getBigDecimal("rival_border_fort_maintenance")
  // hits = 3, isOptional = true, sample = 0.1
  def rrGirondistsInfluence: Option[BigDecimal] = conf.getBigDecimal("rr_girondists_influence")
  // hits = 3, isOptional = true, sample = 0.1
  def rrJacobinsInfluence: Option[BigDecimal] = conf.getBigDecimal("rr_jacobins_influence")
  // hits = 3, isOptional = true, sample = 0.1
  def rrRoyalistsInfluence: Option[BigDecimal] = conf.getBigDecimal("rr_royalists_influence")
  // hits = 3, isOptional = true, sample = 0.5
  def tradeValue: Option[BigDecimal] = conf.getBigDecimal("trade_value")
  // hits = 2, isOptional = true, sample = 25
  def acceptVassalizationReasons: Option[Int] = conf.getInt("accept_vassalization_reasons")
  // hits = 2, isOptional = true, sample = -0.01
  def allPowerCost: Option[BigDecimal] = conf.getBigDecimal("all_power_cost")
  // hits = 2, isOptional = true, sample = true
  def autoExploreAdjacentToColony: Option[Boolean] = conf.getBoolean("auto_explore_adjacent_to_colony")
  // hits = 2, isOptional = true, sample = 0.5
  def blockadeForceRequired: Option[BigDecimal] = conf.getBigDecimal("blockade_force_required")
  // hits = 2, isOptional = true, sample = 0.1
  def brahminsHinduInfluenceModifier: Option[BigDecimal] = conf.getBigDecimal("brahmins_hindu_influence_modifier")
  // hits = 2, isOptional = true, sample = 0.05
  def brahminsHinduLoyaltyModifier: Option[BigDecimal] = conf.getBigDecimal("brahmins_hindu_loyalty_modifier")
  // hits = 2, isOptional = true, sample = -0.4
  def enforceReligionCost: Option[BigDecimal] = conf.getBigDecimal("enforce_religion_cost")
  // hits = 2, isOptional = true, sample = 1
  def freeDipPolicy: Option[Int] = conf.getInt("free_dip_policy")
  // hits = 2, isOptional = true, sample = 1
  def freeMilPolicy: Option[Int] = conf.getInt("free_mil_policy")
  // hits = 2, isOptional = true, sample = 0.1
  def garrisonGrowth: Option[BigDecimal] = conf.getBigDecimal("garrison_growth")
  // hits = 2, isOptional = true, sample = 0.1
  def globalNavalEngagementModifier: Option[BigDecimal] = conf.getBigDecimal("global_naval_engagement_modifier")
  // hits = 2, isOptional = true, sample = 1.0
  def hostileDisembarkSpeed: Option[BigDecimal] = conf.getBigDecimal("hostile_disembark_speed")
  // hits = 2, isOptional = true, sample = true
  def ideaClaimColonies: Option[Boolean] = conf.getBoolean("idea_claim_colonies")
  // hits = 2, isOptional = true, sample = 0.5
  def inflationActionCost: Option[BigDecimal] = conf.getBigDecimal("inflation_action_cost")
  // hits = 2, isOptional = true, sample = 0.25
  def innovativenessGain: Option[BigDecimal] = conf.getBigDecimal("innovativeness_gain")
  // hits = 2, isOptional = true, sample = 1
  def leaderNavalShock: Option[Int] = conf.getInt("leader_naval_shock")
  // hits = 2, isOptional = true, sample = 1
  def leaderSiege: Option[Int] = conf.getInt("leader_siege")
  // hits = 2, isOptional = true, sample = -0.25
  def localCoreCreation: Option[BigDecimal] = conf.getBigDecimal("local_core_creation")
  // hits = 2, isOptional = true, sample = -0.25
  def localGoverningCost: Option[BigDecimal] = conf.getBigDecimal("local_governing_cost")
  // hits = 2, isOptional = true, sample = 3
  def localHostileAttrition: Option[Int] = conf.getInt("local_hostile_attrition")
  // hits = 2, isOptional = true, sample = -1
  def localReligiousUnityContribution: Option[BigDecimal] = conf.getBigDecimal("local_religious_unity_contribution")
  // hits = 2, isOptional = true, sample = 0.1
  def marathaMuslimInfluenceModifier: Option[BigDecimal] = conf.getBigDecimal("maratha_muslim_influence_modifier")
  // hits = 2, isOptional = true, sample = true
  def mayExplore: Option[Boolean] = conf.getBoolean("may_explore")
  // hits = 2, isOptional = true, sample = 0.02
  def monthlyMilitarizedSociety: Option[BigDecimal] = conf.getBigDecimal("monthly_militarized_society")
  // hits = 2, isOptional = true, sample = -0.05
  def mrAristocratsInfluence: Option[BigDecimal] = conf.getBigDecimal("mr_aristocrats_influence")
  // hits = 2, isOptional = true, sample = 1
  def ownCoastNavalCombatBonus: Option[Int] = conf.getInt("own_coast_naval_combat_bonus")
  // hits = 2, isOptional = true, sample = 1
  def possibleAdmPolicy: Option[Int] = conf.getInt("possible_adm_policy")
  // hits = 2, isOptional = true, sample = 1
  def possibleMilPolicy: Option[Int] = conf.getInt("possible_mil_policy")
  // hits = 2, isOptional = true, sample = 1
  def possiblePolicy: Option[Int] = conf.getInt("possible_policy")
  // hits = 2, isOptional = true, sample = 0.05
  def prestigeFromNaval: Option[BigDecimal] = conf.getBigDecimal("prestige_from_naval")
  // hits = 2, isOptional = true, sample = 0.33
  def prestigePerDevelopmentFromConversion: Option[BigDecimal] = conf.getBigDecimal("prestige_per_development_from_conversion")
  // hits = 2, isOptional = true, sample = 0.1
  def rajputMuslimInfluenceModifier: Option[BigDecimal] = conf.getBigDecimal("rajput_muslim_influence_modifier")
  // hits = 2, isOptional = true, sample = 0.20
  def razePowerGain: Option[BigDecimal] = conf.getBigDecimal("raze_power_gain")
  // hits = 2, isOptional = true, sample = 0.1
  def recoverNavyMoraleSpeed: Option[BigDecimal] = conf.getBigDecimal("recover_navy_morale_speed")
  // hits = 2, isOptional = true, sample = -0.1
  def reelectionCost: Option[BigDecimal] = conf.getBigDecimal("reelection_cost")
  // hits = 2, isOptional = true, sample = -0.1
  def specialUnitForcelimit: Option[BigDecimal] = conf.getBigDecimal("special_unit_forcelimit")
  // hits = 2, isOptional = true, sample = 0.05
  def vaisyasInfluenceModifier: Option[BigDecimal] = conf.getBigDecimal("vaisyas_influence_modifier")
  // hits = 2, isOptional = true, sample = 0.1
  def vaisyasNonMuslimInfluenceModifier: Option[BigDecimal] = conf.getBigDecimal("vaisyas_non_muslim_influence_modifier")
  // hits = 2, isOptional = true, sample = -1
  def yearlyHarmony: Option[BigDecimal] = conf.getBigDecimal("yearly_harmony")
  // hits = 1, isOptional = true, sample = -0.25
  def admiralCost: Option[BigDecimal] = conf.getBigDecimal("admiral_cost")
  // hits = 1, isOptional = true, sample = 0.15
  def availableProvinceLoot: Option[BigDecimal] = conf.getBigDecimal("available_province_loot")
  // hits = 1, isOptional = true, sample = true
  def blockIntroduceHeir: Option[Boolean] = conf.getBoolean("block_introduce_heir")
  // hits = 1, isOptional = true, sample = true
  def canFabricateForVassals: Option[Boolean] = conf.getBoolean("can_fabricate_for_vassals")
  // hits = 1, isOptional = true, sample = 1
  def candidateRandomBonus: Option[Int] = conf.getInt("candidate_random_bonus")
  // hits = 1, isOptional = true, sample = -0.2
  def centerOfTradeUpgradeCost: Option[BigDecimal] = conf.getBigDecimal("center_of_trade_upgrade_cost")
  // hits = 1, isOptional = true, sample = -0.50
  def discoveredRelationsImpact: Option[BigDecimal] = conf.getBigDecimal("discovered_relations_impact")
  // hits = 1, isOptional = true, sample = -0.2
  def establishOrderCost: Option[BigDecimal] = conf.getBigDecimal("establish_order_cost")
  // hits = 1, isOptional = true, sample = -0.5
  def flagshipCost: Option[BigDecimal] = conf.getBigDecimal("flagship_cost")
  // hits = 1, isOptional = true, sample = 5
  def globalManpower: Option[Int] = conf.getInt("global_manpower")
  // hits = 1, isOptional = true, sample = 0.5
  def globalReligiousConversionResistance: Option[BigDecimal] = conf.getBigDecimal("global_religious_conversion_resistance")
  // hits = 1, isOptional = true, sample = 0.1
  def globalShipRepair: Option[BigDecimal] = conf.getBigDecimal("global_ship_repair")
  // hits = 1, isOptional = true, sample = 0.33
  def globalSupplyLimitModifier: Option[BigDecimal] = conf.getBigDecimal("global_supply_limit_modifier")
  // hits = 1, isOptional = true, sample = 0.75
  def governingCost: Option[BigDecimal] = conf.getBigDecimal("governing_cost")
  // hits = 1, isOptional = true, sample = 5
  def hostileFleetAttrition: Option[Int] = conf.getInt("hostile_fleet_attrition")
  // hits = 1, isOptional = true, sample = 0.5
  def institutionSpreadFromTrueFaith: Option[BigDecimal] = conf.getBigDecimal("institution_spread_from_true_faith")
  // hits = 1, isOptional = true, sample = 0.05
  def jainsLoyaltyModifier: Option[BigDecimal] = conf.getBigDecimal("jains_loyalty_modifier")
  // hits = 1, isOptional = true, sample = 50
  def legitimateSubjectElector: Option[Int] = conf.getInt("legitimate_subject_elector")
  // hits = 1, isOptional = true, sample = -0.2
  def localBuildTime: Option[BigDecimal] = conf.getBigDecimal("local_build_time")
  // hits = 1, isOptional = true, sample = 1
  def localHeirAdm: Option[Int] = conf.getInt("local_heir_adm")
  // hits = 1, isOptional = true, sample = 1
  def localHeirDip: Option[Int] = conf.getInt("local_heir_dip")
  // hits = 1, isOptional = true, sample = 1
  def localHeirMil: Option[Int] = conf.getInt("local_heir_mil")
  // hits = 1, isOptional = true, sample = 0.75
  def localManpower: Option[BigDecimal] = conf.getBigDecimal("local_manpower")
  // hits = 1, isOptional = true, sample = -0.2
  def localRegimentCost: Option[BigDecimal] = conf.getBigDecimal("local_regiment_cost")
  // hits = 1, isOptional = true, sample = 0.75
  def localReligiousConversionResistance: Option[BigDecimal] = conf.getBigDecimal("local_religious_conversion_resistance")
  // hits = 1, isOptional = true, sample = 250
  def localSailors: Option[Int] = conf.getInt("local_sailors")
  // hits = 1, isOptional = true, sample = 0.10
  def manpowerInTrueFaithProvinces: Option[BigDecimal] = conf.getBigDecimal("manpower_in_true_faith_provinces")
  // hits = 1, isOptional = true, sample = 0.1
  def marathaInfluenceModifier: Option[BigDecimal] = conf.getBigDecimal("maratha_influence_modifier")
  // hits = 1, isOptional = true, sample = 0.05
  def marathaLoyaltyModifier: Option[BigDecimal] = conf.getBigDecimal("maratha_loyalty_modifier")
  // hits = 1, isOptional = true, sample = true
  def mayEstablishFrontier: Option[Boolean] = conf.getBoolean("may_establish_frontier")
  // hits = 1, isOptional = true, sample = -0.33
  def mercantilismCost: Option[BigDecimal] = conf.getBigDecimal("mercantilism_cost")
  // hits = 1, isOptional = true, sample = 0.15
  def monarchLifespan: Option[BigDecimal] = conf.getBigDecimal("monarch_lifespan")
  // hits = 1, isOptional = true, sample = 0.02
  def mrGuildsInfluence: Option[BigDecimal] = conf.getBigDecimal("mr_guilds_influence")
  // hits = 1, isOptional = true, sample = 1.0
  def navalTraditionFromTrade: Option[BigDecimal] = conf.getBigDecimal("naval_tradition_from_trade")
  // hits = 1, isOptional = true, sample = 0.05
  def nomadicTribesLoyaltyModifier: Option[BigDecimal] = conf.getBigDecimal("nomadic_tribes_loyalty_modifier")
  // hits = 1, isOptional = true, sample = 1.0
  def papalInfluenceFromCardinals: Option[BigDecimal] = conf.getBigDecimal("papal_influence_from_cardinals")
  // hits = 1, isOptional = true, sample = 0.1
  def rajputInfluenceModifier: Option[BigDecimal] = conf.getBigDecimal("rajput_influence_modifier")
  // hits = 1, isOptional = true, sample = 0.05
  def rajputLoyaltyModifier: Option[BigDecimal] = conf.getBigDecimal("rajput_loyalty_modifier")
  // hits = 1, isOptional = true, sample = true
  def secondaryReligion: Option[Boolean] = conf.getBoolean("secondary_religion")
  // hits = 1, isOptional = true, sample = 1
  def siegeBlockadeProgress: Option[Int] = conf.getInt("siege_blockade_progress")
  // hits = 1, isOptional = true, sample = -0.2
  def statewideGoverningCost: Option[BigDecimal] = conf.getBigDecimal("statewide_governing_cost")
  // hits = 1, isOptional = true, sample = -0.1
  def transportCost: Option[BigDecimal] = conf.getBigDecimal("transport_cost")
  // hits = 1, isOptional = true, sample = 0.2
  def treasureFleetIncome: Option[BigDecimal] = conf.getBigDecimal("treasure_fleet_income")
  // hits = 1, isOptional = true, sample = 0.05
  def vaisyasLoyaltyModifier: Option[BigDecimal] = conf.getBigDecimal("vaisyas_loyalty_modifier")
  // hits = 1, isOptional = true, sample = 0.01
  def yearlyArmyProfessionalism: Option[BigDecimal] = conf.getBigDecimal("yearly_army_professionalism")
  // hits = 1, isOptional = true, sample = 1
  def yearlyTribalAllegiance: Option[Int] = conf.getInt("yearly_tribal_allegiance")

}

object Modifier extends FromJson[Modifier] {
  val empty: Modifier = Modifier()
}


