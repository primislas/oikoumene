package com.lomicron.oikoumene.model.politics

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.utils.json.FromJson

case class TagModifier
(
  // hits = 126, isOptional = true, sample = 1
  prestige: Option[BigDecimal] = None,
  // hits = 104, isOptional = true, sample = 0.2
  defensiveness: Option[BigDecimal] = None,
  // hits = 103, isOptional = true, sample = 0.33
  globalManpowerModifier: Option[BigDecimal] = None,
  // hits = 103, isOptional = true, sample = 1
  legitimacy: Option[BigDecimal] = None,
  // hits = 103, isOptional = true, sample = -0.25
  stabilityCostModifier: Option[BigDecimal] = None,
  // hits = 94, isOptional = true, sample = -2
  globalUnrest: Option[BigDecimal] = None,
  // hits = 89, isOptional = true, sample = 0.10
  globalTaxModifier: Option[BigDecimal] = None,
  // hits = 89, isOptional = true, sample = 0.1
  productionEfficiency: Option[BigDecimal] = None,
  // hits = 88, isOptional = true, sample = 0.1
  tradeEfficiency: Option[BigDecimal] = None,
  // hits = 79, isOptional = true, sample = 2
  diplomaticReputation: Option[Int] = None,
  // hits = 77, isOptional = true, sample = 0.10
  landMorale: Option[BigDecimal] = None,
  // hits = 74, isOptional = true, sample = 2
  toleranceOwn: Option[Int] = None,
  // hits = 66, isOptional = true, sample = 0.1
  globalTradeGoodsSizeModifier: Option[BigDecimal] = None,
  // hits = 55, isOptional = true, sample = -0.1
  developmentCost: Option[BigDecimal] = None,
  // hits = 50, isOptional = true, sample = 0.05
  discipline: Option[BigDecimal] = None,
  // hits = 50, isOptional = true, sample = 0.1
  infantryPower: Option[BigDecimal] = None,
  // hits = 43, isOptional = true, sample = -0.1
  ideaCost: Option[BigDecimal] = None,
  // hits = 41, isOptional = true, sample = 0.10
  cavalryPower: Option[BigDecimal] = None,
  // hits = 41, isOptional = true, sample = 1
  diplomaticUpkeep: Option[Int] = None,
  // hits = 40, isOptional = true, sample = 1
  hostileAttrition: Option[BigDecimal] = None,
  // hits = 40, isOptional = true, sample = 1
  merchants: Option[Int] = None,
  // hits = 38, isOptional = true, sample = 1
  diplomats: Option[Int] = None,
  // hits = 38, isOptional = true, sample = 0.2
  globalTradePower: Option[BigDecimal] = None,
  // hits = 37, isOptional = true, sample = -0.1
  technologyCost: Option[BigDecimal] = None,
  // hits = 36, isOptional = true, sample = 0.1
  globalOwnTradePower: Option[BigDecimal] = None,
  // hits = 35, isOptional = true, sample = -0.1
  buildCost: Option[BigDecimal] = None,
  // hits = 34, isOptional = true, sample = -0.25
  coreCreation: Option[BigDecimal] = None,
  // hits = 34, isOptional = true, sample = 0.03
  globalMissionaryStrength: Option[BigDecimal] = None,
  // hits = 32, isOptional = true, sample = -0.15
  mercMaintenanceModifier: Option[BigDecimal] = None,
  // hits = 31, isOptional = true, sample = 2
  toleranceHeretic: Option[Int] = None,
  // hits = 30, isOptional = true, sample = 0.2
  manpowerRecoverySpeed: Option[BigDecimal] = None,
  // hits = 30, isOptional = true, sample = 2
  toleranceHeathen: Option[Int] = None,
  // hits = 29, isOptional = true, sample = 1
  armyTradition: Option[BigDecimal] = None,
  // hits = 26, isOptional = true, sample = 0.25
  globalInstitutionSpread: Option[BigDecimal] = None,
  // hits = 26, isOptional = true, sample = 0.25
  navalForcelimitModifier: Option[BigDecimal] = None,
  // hits = 24, isOptional = true, sample = 2
  numAcceptedCultures: Option[Int] = None,
  // hits = 23, isOptional = true, sample = -0.10
  advisorCost: Option[BigDecimal] = None,
  // hits = 23, isOptional = true, sample = -0.2
  aeImpact: Option[BigDecimal] = None,
  // hits = 23, isOptional = true, sample = -0.10
  cavalryCost: Option[BigDecimal] = None,
  // hits = 22, isOptional = true, sample = -0.1
  fortMaintenanceModifier: Option[BigDecimal] = None,
  // hits = 22, isOptional = true, sample = 0.15
  globalProvTradePowerModifier: Option[BigDecimal] = None,
  // hits = 22, isOptional = true, sample = 1
  leaderLandShock: Option[Int] = None,
  // hits = 22, isOptional = true, sample = 0.25
  tradeSteering: Option[BigDecimal] = None,
  // hits = 21, isOptional = true, sample = -0.025
  globalAutonomy: Option[BigDecimal] = None,
  // hits = 21, isOptional = true, sample = 0.33
  privateerEfficiency: Option[BigDecimal] = None,
  // hits = 21, isOptional = true, sample = 0.25
  religiousUnity: Option[BigDecimal] = None,
  // hits = 19, isOptional = true, sample = 0.45
  factor: Option[BigDecimal] = None,
  // hits = 19, isOptional = true, sample = 0.25
  improveRelationModifier: Option[BigDecimal] = None,
  // hits = 18, isOptional = true, sample = 1
  advisorPool: Option[Int] = None,
  // hits = 18, isOptional = true, sample = 0.1
  fireDamage: Option[BigDecimal] = None,
  // hits = 18, isOptional = true, sample = 0.2
  landForcelimitModifier: Option[BigDecimal] = None,
  // hits = 18, isOptional = true, sample = 1
  navyTradition: Option[BigDecimal] = None,
  // hits = 18, isOptional = true, sample = 0.5
  spyOffence: Option[BigDecimal] = None,
  // hits = 17, isOptional = true, sample = 0.25
  caravanPower: Option[BigDecimal] = None,
  // hits = 15, isOptional = true, sample = -0.1
  globalShipCost: Option[BigDecimal] = None,
  // hits = 15, isOptional = true, sample = -0.1
  infantryCost: Option[BigDecimal] = None,
  // hits = 15, isOptional = true, sample = -0.25
  landAttrition: Option[BigDecimal] = None,
  // hits = 15, isOptional = true, sample = 1
  leaderLandManuever: Option[Int] = None,
  // hits = 15, isOptional = true, sample = 2
  papalInfluence: Option[Int] = None,
  // hits = 14, isOptional = true, sample = 10
  globalColonialGrowth: Option[Int] = None,
  // hits = 13, isOptional = true, sample = 1
  colonists: Option[Int] = None,
  // hits = 13, isOptional = true, sample = 1
  missionaries: Option[Int] = None,
  // hits = 13, isOptional = true, sample = {"factor":0.5,"is_subject":true}
  modifier: Seq[Object] = Seq.empty,
  // hits = 12, isOptional = true, sample = 0.03
  globalHereticMissionaryStrength: Option[BigDecimal] = None,
  // hits = 12, isOptional = true, sample = 0.5
  heirChance: Option[BigDecimal] = None,
  // hits = 12, isOptional = true, sample = -0.1
  sailorMaintenanceModifer: Option[BigDecimal] = None,
  // hits = 12, isOptional = true, sample = 0.25
  tradeRangeModifier: Option[BigDecimal] = None,
  // hits = 11, isOptional = true, sample = -0.25
  fabricateClaimsCost: Option[BigDecimal] = None,
  // hits = 11, isOptional = true, sample = 1
  freeLeaderPool: Option[Int] = None,
  // hits = 11, isOptional = true, sample = -0.01
  prestigeDecay: Option[BigDecimal] = None,
  // hits = 10, isOptional = true, sample = -0.1
  admTechCostModifier: Option[BigDecimal] = None,
  // hits = 10, isOptional = true, sample = -0.01
  armyTraditionDecay: Option[BigDecimal] = None,
  // hits = 10, isOptional = true, sample = 0.10
  navalMorale: Option[BigDecimal] = None,
  // hits = 10, isOptional = true, sample = -0.05
  shockDamageReceived: Option[BigDecimal] = None,
  // hits = 9, isOptional = true, sample = 0.1
  churchPowerModifier: Option[BigDecimal] = None,
  // hits = 9, isOptional = true, sample = 0.25
  enemyCoreCreation: Option[BigDecimal] = None,
  // hits = 9, isOptional = true, sample = 0.25
  galleyPower: Option[BigDecimal] = None,
  // hits = 9, isOptional = true, sample = 0.25
  garrisonSize: Option[BigDecimal] = None,
  // hits = 9, isOptional = true, sample = 0.5
  globalSailorsModifier: Option[BigDecimal] = None,
  // hits = 9, isOptional = true, sample = 0.33
  globalSpyDefence: Option[BigDecimal] = None,
  // hits = 9, isOptional = true, sample = -0.5
  interest: Option[BigDecimal] = None,
  // hits = 9, isOptional = true, sample = -0.20
  lightShipCost: Option[BigDecimal] = None,
  // hits = 9, isOptional = true, sample = 0.15
  movementSpeed: Option[BigDecimal] = None,
  // hits = 9, isOptional = true, sample = -0.05
  warExhaustion: Option[BigDecimal] = None,
  // hits = 8, isOptional = true, sample = 0.5
  devotion: Option[BigDecimal] = None,
  // hits = 8, isOptional = true, sample = 0.25
  embargoEfficiency: Option[BigDecimal] = None,
  // hits = 8, isOptional = true, sample = -0.20
  envoyTravelTime: Option[BigDecimal] = None,
  // hits = 8, isOptional = true, sample = 0.1
  inflationReduction: Option[BigDecimal] = None,
  // hits = 8, isOptional = true, sample = -0.05
  landMaintenanceModifier: Option[BigDecimal] = None,
  // hits = 8, isOptional = true, sample = -0.2
  navalMaintenanceModifier: Option[BigDecimal] = None,
  // hits = 8, isOptional = true, sample = 0.05
  shipDurability: Option[BigDecimal] = None,
  // hits = 8, isOptional = true, sample = 0.20
  siegeAbility: Option[BigDecimal] = None,
  // hits = 8, isOptional = true, sample = -10
  yearsOfNationalism: Option[BigDecimal] = None,
  // hits = 7, isOptional = true, sample = 0.25
  cavalryFlanking: Option[BigDecimal] = None,
  // hits = 7, isOptional = true, sample = -0.1
  cultureConversionCost: Option[BigDecimal] = None,
  // hits = 7, isOptional = true, sample = -0.25
  diplomaticAnnexationCost: Option[BigDecimal] = None,
  // hits = 7, isOptional = true, sample = 0.25
  lootAmount: Option[BigDecimal] = None,
  // hits = 7, isOptional = true, sample = 0.001
  monthlyPiety: Option[BigDecimal] = None,
  // hits = 7, isOptional = true, sample = 0.20
  possibleMercenaries: Option[BigDecimal] = None,
  // hits = 7, isOptional = true, sample = -0.2
  sameCultureAdvisorCost: Option[BigDecimal] = None,
  // hits = 7, isOptional = true, sample = -0.10
  warExhaustionCost: Option[BigDecimal] = None,
  // hits = 6, isOptional = true, sample = 0.15
  captureShipChance: Option[BigDecimal] = None,
  // hits = 6, isOptional = true, sample = -0.1
  globalRegimentRecruitSpeed: Option[BigDecimal] = None,
  // hits = 6, isOptional = true, sample = 1
  leaderNavalManuever: Option[Int] = None,
  // hits = 6, isOptional = true, sample = -0.2
  provinceWarscoreCost: Option[BigDecimal] = None,
  // hits = 6, isOptional = true, sample = 0.5
  republicanTradition: Option[BigDecimal] = None,
  // hits = 5, isOptional = true, sample = 0.1
  artilleryPower: Option[BigDecimal] = None,
  // hits = 5, isOptional = true, sample = -0.2
  galleyCost: Option[BigDecimal] = None,
  // hits = 5, isOptional = true, sample = 0.20
  heavyShipPower: Option[BigDecimal] = None,
  // hits = 5, isOptional = true, sample = 0.025
  mercenaryDiscipline: Option[BigDecimal] = None,
  // hits = 5, isOptional = true, sample = -0.1
  milTechCostModifier: Option[BigDecimal] = None,
  // hits = 5, isOptional = true, sample = -0.01
  navyTraditionDecay: Option[BigDecimal] = None,
  // hits = 5, isOptional = true, sample = 1.0
  prestigeFromLand: Option[BigDecimal] = None,
  // hits = 5, isOptional = true, sample = 0.25
  sailorsRecoverySpeed: Option[BigDecimal] = None,
  // hits = 5, isOptional = true, sample = 0.15
  shockDamage: Option[BigDecimal] = None,
  // hits = 4, isOptional = true, sample = 0.5
  armyTraditionFromBattle: Option[BigDecimal] = None,
  // hits = 4, isOptional = true, sample = -0.1
  artilleryCost: Option[BigDecimal] = None,
  // hits = 4, isOptional = true, sample = -0.1
  fireDamageReceived: Option[BigDecimal] = None,
  // hits = 4, isOptional = true, sample = 0.10
  globalForeignTradePower: Option[BigDecimal] = None,
  // hits = 4, isOptional = true, sample = -0.1
  globalRegimentCost: Option[BigDecimal] = None,
  // hits = 4, isOptional = true, sample = -0.10
  globalShipRecruitSpeed: Option[BigDecimal] = None,
  // hits = 4, isOptional = true, sample = 1
  leaderLandFire: Option[Int] = None,
  // hits = 4, isOptional = true, sample = 0.25
  monthlyFervorIncrease: Option[BigDecimal] = None,
  // hits = 4, isOptional = true, sample = 0.5
  range: Option[BigDecimal] = None,
  // hits = 4, isOptional = true, sample = 15
  reducedLibertyDesire: Option[Int] = None,
  // hits = 4, isOptional = true, sample = 0.33
  reinforceSpeed: Option[BigDecimal] = None,
  // hits = 4, isOptional = true, sample = -0.15
  stateMaintenanceModifier: Option[BigDecimal] = None,
  // hits = 4, isOptional = true, sample = -0.25
  unjustifiedDemands: Option[BigDecimal] = None,
  // hits = 4, isOptional = true, sample = 1
  vassalForcelimitBonus: Option[Int] = None,
  // hits = 4, isOptional = true, sample = 0.25
  vassalIncome: Option[BigDecimal] = None,
  // hits = 4, isOptional = true, sample = -0.1
  yearlyCorruption: Option[BigDecimal] = None,
  // hits = 3, isOptional = true, sample = -0.1
  dipTechCostModifier: Option[BigDecimal] = None,
  // hits = 3, isOptional = true, sample = 0.1
  globalGarrisonGrowth: Option[BigDecimal] = None,
  // hits = 3, isOptional = true, sample = 0.10
  globalTariffs: Option[BigDecimal] = None,
  // hits = 3, isOptional = true, sample = 1
  hordeUnity: Option[Int] = None,
  // hits = 3, isOptional = true, sample = -0.1
  justifyTradeConflictCost: Option[BigDecimal] = None,
  // hits = 3, isOptional = true, sample = 1
  leaderNavalFire: Option[Int] = None,
  // hits = 3, isOptional = true, sample = 0.10
  lightShipPower: Option[BigDecimal] = None,
  // hits = 3, isOptional = true, sample = 3
  maxStates: Option[Int] = None,
  // hits = 3, isOptional = true, sample = -0.25
  navalAttrition: Option[BigDecimal] = None,
  // hits = 3, isOptional = true, sample = true
  noReligionPenalty: Boolean = false,
  // hits = 3, isOptional = true, sample = 0.25
  rebelSupportEfficiency: Option[BigDecimal] = None,
  // hits = 3, isOptional = true, sample = -0.33
  sunkShipMoraleHitRecieved: Option[BigDecimal] = None,
  // hits = 3, isOptional = true, sample = 0.1
  yearlyAbsolutism: Option[BigDecimal] = None,
  // hits = 2, isOptional = true, sample = 0.05
  administrativeEfficiency: Option[BigDecimal] = None,
  // hits = 2, isOptional = true, sample = true
  autoExploreAdjacentToColony: Boolean = false,
  // hits = 2, isOptional = true, sample = 0.5
  blockadeEfficiency: Option[BigDecimal] = None,
  // hits = 2, isOptional = true, sample = -0.1
  embracementCost: Option[BigDecimal] = None,
  // hits = 2, isOptional = true, sample = 1
  freeMilPolicy: Option[Int] = None,
  // hits = 2, isOptional = true, sample = 0.1
  globalNavalEngagementModifier: Option[BigDecimal] = None,
  // hits = 2, isOptional = true, sample = 0.1
  globalShipTradePower: Option[BigDecimal] = None,
  // hits = 2, isOptional = true, sample = true
  ideaClaimColonies: Boolean = false,
  // hits = 2, isOptional = true, sample = 1
  leaderNavalShock: Option[Int] = None,
  // hits = 2, isOptional = true, sample = 1
  leaderSiege: Option[Int] = None,
  // hits = 2, isOptional = true, sample = true
  mayExplore: Boolean = false,
  // hits = 2, isOptional = true, sample = -0.25
  mercenaryCost: Option[BigDecimal] = None,
  // hits = 2, isOptional = true, sample = 1
  ownCoastNavalCombatBonus: Option[Int] = None,
  // hits = 2, isOptional = true, sample = 1
  possibleMilPolicy: Option[Int] = None,
  // hits = 2, isOptional = true, sample = 1
  powerProjectionFromInsults: Option[Int] = None,
  // hits = 2, isOptional = true, sample = -0.5
  rivalBorderFortMaintenance: Option[BigDecimal] = None,
  // hits = 1, isOptional = true, sample = -0.25
  admiralCost: Option[BigDecimal] = None,
  // hits = 1, isOptional = true, sample = 0.25
  amountOfBanners: Option[BigDecimal] = None,
  // hits = 1, isOptional = true, sample = -0.25
  autonomyChangeTime: Option[BigDecimal] = None,
  // hits = 1, isOptional = true, sample = -0.1
  buildTime: Option[BigDecimal] = None,
  // hits = 1, isOptional = true, sample = true
  canFabricateForVassals: Boolean = false,
  // hits = 1, isOptional = true, sample = 0.10
  cavToInfRatio: Option[BigDecimal] = None,
  // hits = 1, isOptional = true, sample = true
  cbOnPrimitives: Boolean = false,
  // hits = 1, isOptional = true, sample = true
  cbOnReligiousEnemies: Boolean = false,
  // hits = 1, isOptional = true, sample = -0.2
  centerOfTradeUpgradeCost: Option[BigDecimal] = None,
  // hits = 1, isOptional = true, sample = 0.05
  colonistPlacementChance: Option[BigDecimal] = None,
  // hits = 1, isOptional = true, sample = -0.25
  expelMinoritiesCost: Option[BigDecimal] = None,
  // hits = 1, isOptional = true, sample = 1
  freeAdmPolicy: Option[Int] = None,
  // hits = 1, isOptional = true, sample = 1
  freeDipPolicy: Option[Int] = None,
  // hits = 1, isOptional = true, sample = 0.1
  globalShipRepair: Option[BigDecimal] = None,
  // hits = 1, isOptional = true, sample = 0.33
  globalSupplyLimitModifier: Option[BigDecimal] = None,
  // hits = 1, isOptional = true, sample = -0.15
  harshTreatmentCost: Option[BigDecimal] = None,
  // hits = 1, isOptional = true, sample = -0.05
  heavyShipCost: Option[BigDecimal] = None,
  // hits = 1, isOptional = true, sample = 0.1
  imperialAuthority: Option[BigDecimal] = None,
  // hits = 1, isOptional = true, sample = 0.5
  innovativenessGain: Option[BigDecimal] = None,
  // hits = 1, isOptional = true, sample = true
  mayEstablishFrontier: Boolean = false,
  // hits = 1, isOptional = true, sample = -0.5
  missionaryMaintenanceCost: Option[BigDecimal] = None,
  // hits = 1, isOptional = true, sample = 0.5
  nativeAssimilation: Option[BigDecimal] = None,
  // hits = 1, isOptional = true, sample = -0.5
  nativeUprisingChance: Option[BigDecimal] = None,
  // hits = 1, isOptional = true, sample = 1.0
  navalTraditionFromTrade: Option[BigDecimal] = None,
  // hits = 1, isOptional = true, sample = 5
  placedMerchantPower: Option[Int] = None,
  // hits = 1, isOptional = true, sample = 1
  possibleDipPolicy: Option[Int] = None,
  // hits = 1, isOptional = true, sample = 1
  possiblePolicy: Option[Int] = None,
  // hits = 1, isOptional = true, sample = 0.1
  prCaptainsInfluence: Option[BigDecimal] = None,
  // hits = 1, isOptional = true, sample = 0.05
  recoverArmyMoraleSpeed: Option[BigDecimal] = None,
  // hits = 1, isOptional = true, sample = 0.15
  reformProgressGrowth: Option[BigDecimal] = None,
  // hits = 1, isOptional = true, sample = -0.25
  reinforceCostModifier: Option[BigDecimal] = None,
  // hits = 1, isOptional = true, sample = 1
  yearlyTribalAllegiance: Option[Int] = None,
) {
  @JsonCreator def this() = this(None)
}

object TagModifier extends FromJson[TagModifier] {
  def empty: TagModifier = TagModifier()
}