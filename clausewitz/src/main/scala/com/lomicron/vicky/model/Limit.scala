package com.lomicron.vicky.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.utils.json.FromJson

case class Limit
(
  // hits = 13, isOptional = true, sample = 1
  neoclassicalTheory: Option[Int] = None,
  // hits = 11, isOptional = true, sample = 1
  aeronautics: Option[Int] = None,
  // hits = 10, isOptional = true, sample = 1
  ideologicalThought: Option[Int] = None,
  // hits = 9, isOptional = true, sample = 1
  mechanicalProduction: Option[Int] = None,
  // hits = 9, isOptional = true, sample = 1
  revolutionNCounterrevolution: Option[Int] = None,
  // hits = 8, isOptional = true, sample = 1
  heavyArmamentDunno: Option[Int] = None,
  // hits = 8, isOptional = true, sample = 1
  medicine: Option[Int] = None,
  // hits = 7, isOptional = true, sample = 1
  collectivistTheory: Option[Int] = None,
  // hits = 7, isOptional = true, sample = 1
  massCultureNTheAvantGarde: Option[Int] = None,
  // hits = 7, isOptional = true, sample = 1
  theHistoricalTheory: Option[Int] = None,
  // hits = 6, isOptional = true, sample = 1
  electricity: Option[Int] = None,
  // hits = 6, isOptional = true, sample = 1
  mainArmament: Option[Int] = None,
  // hits = 5, isOptional = true, sample = 1
  enlightenmentThought: Option[Int] = None,
  // hits = 5, isOptional = true, sample = 1
  governmentInterventionism: Option[Int] = None,
  // hits = 5, isOptional = true, sample = 1
  inorganicChemistry: Option[Int] = None,
  // hits = 5, isOptional = true, sample = 1
  interchangeableParts: Option[Int] = None,
  // hits = 5, isOptional = true, sample = 1
  militaryDirectionism: Option[Int] = None,
  // hits = 5, isOptional = true, sample = 1
  nationalismNImperialism: Option[Int] = None,
  // hits = 5, isOptional = true, sample = 1
  stateNGovernment: Option[Int] = None,
  // hits = 4, isOptional = true, sample = 1
  advancedNavalDesign: Option[Int] = None,
  // hits = 4, isOptional = true, sample = 1
  analyticPhilosophy: Option[Int] = None,
  // hits = 4, isOptional = true, sample = 1
  antiRationalism: Option[Int] = None,
  // hits = 4, isOptional = true, sample = 1
  businessRegulations: Option[Int] = None,
  // hits = 4, isOptional = true, sample = 1
  cheapIron: Option[Int] = None,
  // hits = 4, isOptional = true, sample = 1
  electricalPowerGeneration: Option[Int] = None,
  // hits = 4, isOptional = true, sample = 1
  infiltration: Option[Int] = None,
  // hits = 4, isOptional = true, sample = 1
  keynesianEconomics: Option[Int] = None,
  // hits = 4, isOptional = true, sample = 1
  mechanizedMining: Option[Int] = None,
  // hits = 4, isOptional = true, sample = 1
  oilDrivenShips: Option[Int] = None,
  // hits = 4, isOptional = true, sample = 1
  organicChemistry: Option[Int] = None,
  // hits = 4, isOptional = true, sample = 1
  pointDefenseSystem: Option[Int] = None,
  // hits = 4, isOptional = true, sample = 1
  raiderGroupDoctrine: Option[Int] = None,
  // hits = 4, isOptional = true, sample = 1
  socialScience: Option[Int] = None,
  // hits = 4, isOptional = true, sample = 1
  steamers: Option[Int] = None,
  // hits = 3, isOptional = true, sample = 1
  armyDecisionMaking: Option[Int] = None,
  // hits = 3, isOptional = true, sample = 1
  armyNcoTraining: Option[Int] = None,
  // hits = 3, isOptional = true, sample = 1
  armyProfessionalism: Option[Int] = None,
  // hits = 3, isOptional = true, sample = 1
  armyRiskManagement: Option[Int] = None,
  // hits = 3, isOptional = true, sample = 1
  assemblyLine: Option[Int] = None,
  // hits = 3, isOptional = true, sample = 1
  behaviorism: Option[Int] = None,
  // hits = 3, isOptional = true, sample = 1
  businessBanks: Option[Int] = None,
  // hits = 3, isOptional = true, sample = 1
  combustionEngine: Option[Int] = None,
  // hits = 3, isOptional = true, sample = 1
  deepDefenseSystem: Option[Int] = None,
  // hits = 3, isOptional = true, sample = 1
  earlyClassicalTheoryAndCritique: Option[Int] = None,
  // hits = 3, isOptional = true, sample = 1
  economicResponsibility: Option[Int] = None,
  // hits = 3, isOptional = true, sample = 1
  expressionism: Option[Int] = None,
  // hits = 3, isOptional = true, sample = 1
  idealism: Option[Int] = None,
  // hits = 3, isOptional = true, sample = 1
  impressionism: Option[Int] = None,
  // hits = 3, isOptional = true, sample = 1
  investmentBanks: Option[Int] = None,
  // hits = 3, isOptional = true, sample = 1
  ironSteamers: Option[Int] = None,
  // hits = 3, isOptional = true, sample = 1
  lateClassicalTheory: Option[Int] = None,
  // hits = 3, isOptional = true, sample = 1
  managementStrategy: Option[Int] = None,
  // hits = 3, isOptional = true, sample = 1
  marketRegulations: Option[Int] = None,
  // hits = 3, isOptional = true, sample = 1
  marketStructure: Option[Int] = None,
  // hits = 3, isOptional = true, sample = 1
  massPolitics: Option[Int] = None,
  // hits = 3, isOptional = true, sample = 1
  militaryLogistics: Option[Int] = None,
  // hits = 3, isOptional = true, sample = 1
  militaryPlans: Option[Int] = None,
  // hits = 3, isOptional = true, sample = 1
  militaryStaffSystem: Option[Int] = None,
  // hits = 3, isOptional = true, sample = 1
  militaryStatistics: Option[Int] = None,
  // hits = 3, isOptional = true, sample = 1
  modernArmyDoctrine: Option[Int] = None,
  // hits = 3, isOptional = true, sample = 1
  modernDivisionalStructure: Option[Int] = None,
  // hits = 3, isOptional = true, sample = 1
  modernNavalDesign: Option[Int] = None,
  // hits = 3, isOptional = true, sample = 1
  navalDecisionMaking: Option[Int] = None,
  // hits = 3, isOptional = true, sample = 1
  navalDirectionism: Option[Int] = None,
  // hits = 3, isOptional = true, sample = 1
  navalLogistics: Option[Int] = None,
  // hits = 3, isOptional = true, sample = 1
  navalNcoTraining: Option[Int] = None,
  // hits = 3, isOptional = true, sample = 1
  navalPlans: Option[Int] = None,
  // hits = 3, isOptional = true, sample = 1
  navalProfessionalism: Option[Int] = None,
  // hits = 3, isOptional = true, sample = 1
  navalRiskManagement: Option[Int] = None,
  // hits = 3, isOptional = true, sample = 1
  navalStatistics: Option[Int] = None,
  // hits = 3, isOptional = true, sample = {"OR":{"invention":["oligopoly_structure","monopoly_structure"]}}
  not: Option[ObjectNode] = None,
  // hits = 3, isOptional = true, sample = 1
  organizedFactories: Option[Int] = None,
  // hits = 3, isOptional = true, sample = 1
  realism: Option[Int] = None,
  // hits = 3, isOptional = true, sample = 1
  romanticism: Option[Int] = None,
  // hits = 3, isOptional = true, sample = 1
  scientificManagement: Option[Int] = None,
  // hits = 3, isOptional = true, sample = 1
  semiAutomatization: Option[Int] = None,
  // hits = 3, isOptional = true, sample = 1
  stockExchange: Option[Int] = None,
  // hits = 3, isOptional = true, sample = 1
  timeSavingMeasures: Option[Int] = None,
  // hits = 3, isOptional = true, sample = 1
  weaponPlatforms: Option[Int] = None,
  // hits = 2, isOptional = true, sample = 1
  adHocMoneyBillPrinting: Option[Int] = None,
  // hits = 2, isOptional = true, sample = 1
  advancedMetallurgy: Option[Int] = None,
  // hits = 2, isOptional = true, sample = 1
  bankInspectionBoard: Option[Int] = None,
  // hits = 2, isOptional = true, sample = 1
  battleshipColumnDoctrine: Option[Int] = None,
  // hits = 2, isOptional = true, sample = 1
  biologism: Option[Int] = None,
  // hits = 2, isOptional = true, sample = 1
  blueAndBrownWaterSchools: Option[Int] = None,
  // hits = 2, isOptional = true, sample = 1
  breechLoadedRifles: Option[Int] = None,
  // hits = 2, isOptional = true, sample = 1
  centralBankMoneyBillPrinting: Option[Int] = None,
  // hits = 2, isOptional = true, sample = 1
  cheapSteel: Option[Int] = None,
  // hits = 2, isOptional = true, sample = 1
  cleanCoal: Option[Int] = None,
  // hits = 2, isOptional = true, sample = 1
  empiricism: Option[Int] = None,
  // hits = 2, isOptional = true, sample = 1
  fireControlSystems: Option[Int] = None,
  // hits = 2, isOptional = true, sample = 1
  highSeaBattleFleet: Option[Int] = None,
  // hits = 2, isOptional = true, sample = 1
  modernCentralBankSystem: Option[Int] = None,
  // hits = 2, isOptional = true, sample = 1
  modernNavalDoctrine: Option[Int] = None,
  // hits = 2, isOptional = true, sample = 1
  modernNavalTraining: Option[Int] = None,
  // hits = 2, isOptional = true, sample = 1
  navalIntegration: Option[Int] = None,
  // hits = 2, isOptional = true, sample = 1
  organizationalDevelopment: Option[Int] = None,
  // hits = 2, isOptional = true, sample = 1
  phenomenologyNHermeneutic: Option[Int] = None,
  // hits = 2, isOptional = true, sample = 1
  privateBankMoneyBillPrinting: Option[Int] = None,
  // hits = 2, isOptional = true, sample = 1
  socialAlienation: Option[Int] = None,
  // hits = 2, isOptional = true, sample = 1
  steelSteamers: Option[Int] = None,
  // hits = 2, isOptional = true, sample = 1
  strategicMobility: Option[Int] = None,
  // hits = 2, isOptional = true, sample = 1
  syntheticPolymers: Option[Int] = None,
  // hits = 2, isOptional = true, sample = 1870
  year: Option[Int] = None,
  // hits = 1, isOptional = true, sample = 1
  boltActionRifles: Option[Int] = None,
  // hits = 1, isOptional = true, sample = 1
  clipperDesign: Option[Int] = None,
  // hits = 1, isOptional = true, sample = 1
  electricFurnace: Option[Int] = None,
  // hits = 1, isOptional = true, sample = 1
  flintlockRifles: Option[Int] = None,
  // hits = 1, isOptional = true, sample = 1
  machineGuns: Option[Int] = None,
  // hits = 1, isOptional = true, sample = 1
  marketDeterminedExchangeRates: Option[Int] = None,
  // hits = 1, isOptional = true, sample = 1
  mutualFunds: Option[Int] = None,
  // hits = 1, isOptional = true, sample = 1
  muzzleLoadedRifles: Option[Int] = None,
  // hits = 1, isOptional = true, sample = {"government":["hms_government","prussian_constitutionalism","absolute_monarchy"]}
  or: Option[ObjectNode] = None,
  // hits = 1, isOptional = true, sample = 1
  postNapoleonicThought: Option[Int] = None,
  // hits = 1, isOptional = true, sample = 1
  practicalSteamEngine: Option[Int] = None,
  // hits = 1, isOptional = true, sample = 1
  steamTurbineShips: Option[Int] = None,
  // hits = 1, isOptional = true, sample = 1
  waterWheelPower: Option[Int] = None,
) {
  @JsonCreator def this() = this(None)
}

object Limit extends FromJson[Limit] {
  val empty: Limit = Limit()
}
