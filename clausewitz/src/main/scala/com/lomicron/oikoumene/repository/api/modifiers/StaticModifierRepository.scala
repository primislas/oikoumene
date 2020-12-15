package com.lomicron.oikoumene.repository.api.modifiers

import com.lomicron.oikoumene.model.modifiers.Modifier

trait StaticModifierRepository extends ModifierRepository {

  def nullModifier: Option[Modifier] = find("null_modifier").toOption

  def governmentRank(id: Int): Option[Modifier] = find(s"configured_gov_rank_$id").toOption

  def difficultyVeryEasyPlayer: Option[Modifier] = find("difficulty_very_easy_player").toOption
  def difficultyEasyPlayer: Option[Modifier] = find("difficulty_easy_player").toOption
  def difficultyNormalPlayer: Option[Modifier] = find("difficulty_normal_player").toOption
  def difficultyHardPlayer: Option[Modifier] = find("difficulty_hard_player").toOption
  def difficultyVeryHardPlayer: Option[Modifier] = find("difficulty_very_hard_player").toOption

  def difficultyVeryEasyAi: Option[Modifier] = find("difficulty_very_easy_ai").toOption
  def difficultyEasyAi: Option[Modifier] = find("difficulty_easy_ai").toOption
  def difficultyNormalAi: Option[Modifier] = find("difficulty_normal_ai").toOption
  def difficultyHardAi: Option[Modifier] = find("difficulty_hard_ai").toOption
  def difficultyVeryHardAi: Option[Modifier] = find("difficulty_very_hard_ai").toOption

  def city: Option[Modifier] = find("city").toOption
  def port: Option[Modifier] = find("port").toOption
  def inState: Option[Modifier] = find("in_state").toOption
  def inCapitalState: Option[Modifier] = find("in_capital_state").toOption
  def coastal: Option[Modifier] = find("coastal").toOption
  def nonCoastal: Option[Modifier] = find("non_coastal").toOption
  def landProvince: Option[Modifier] = find("land_province").toOption
  def seaZone: Option[Modifier] = find("sea_zone").toOption
  def coastalSea: Option[Modifier] = find("coastal_sea").toOption

  def tropical: Option[Modifier] = find("tropical").toOption
  def arctic: Option[Modifier] = find("arctic").toOption
  def arid: Option[Modifier] = find("arid").toOption
  def mildWinter: Option[Modifier] = find("mild_winter").toOption
  def normalWinter: Option[Modifier] = find("normal_winter").toOption
  def severeWinter: Option[Modifier] = find("severe_winter").toOption
  def mildMonsoon: Option[Modifier] = find("mild_monsoon").toOption
  def normalMonsoon: Option[Modifier] = find("normal_monsoon").toOption
  def severeMonsoon: Option[Modifier] = find("severe_monsoon").toOption

  def provincialTaxIncome: Option[Modifier] = find("provincial_tax_income").toOption
  def provincialProductionSize: Option[Modifier] = find("provincial_production_size").toOption
  def manpower: Option[Modifier] = find("manpower").toOption
  def development: Option[Modifier] = find("development").toOption
  def developmentScaled: Option[Modifier] = find("development_scaled").toOption
  def hordeDevelopment: Option[Modifier] = find("horde_development").toOption

  def sailors: Option[Modifier] = find("sailors").toOption

  def knowledgeSharing: Option[Modifier] = find("knowledge_sharing").toOption
  def cardinalsSpreadInstitution: Option[Modifier] = find("cardinals_spread_institution").toOption
  def patriarchState: Option[Modifier] = find("patriarch_state").toOption
  def patriarchAuthorityLocal: Option[Modifier] = find("patriarch_authority_local").toOption
  def patriarchAuthorityGlobal: Option[Modifier] = find("patriarch_authority_global").toOption
  def pashaState: Option[Modifier] = find("pasha_state").toOption

  def colonyLevel: Option[Modifier] = find("colony_level").toOption
  def nativeAssimilation: Option[Modifier] = find("native_assimilation").toOption
  def nativeAggressiveness: Option[Modifier] = find("native_aggressiveness").toOption

  def core: Option[Modifier] = find("core").toOption
  def nonCore: Option[Modifier] = find("non_core").toOption
  def territoryCore: Option[Modifier] = find("territory_core").toOption
  def territoryNonCore: Option[Modifier] = find("territory_non_core").toOption
  def inTradeCompany: Option[Modifier] = find("in_trade_company").toOption
  def leftTradeCompany: Option[Modifier] = find("left_trade_company").toOption

  def marchBonus: Option[Modifier] = find("march_bonus").toOption
  def capitalCity: Option[Modifier] = find("capital_city").toOption
  def seatInParliament: Option[Modifier] = find("seat_in_parliament").toOption
  def noAdjacentControlled: Option[Modifier] = find("no_adjacent_controlled").toOption
  def provinceRazed: Option[Modifier] = find("province_razed").toOption
  def sameCultureGroup: Option[Modifier] = find("same_culture_group").toOption
  def nonAcceptedCulture: Option[Modifier] = find("non_accepted_culture").toOption
  def acceptedCultureDemoted: Option[Modifier] = find("accepted_culture_demoted").toOption
  def nonAcceptedCultureRepublic: Option[Modifier] = find("non_accepted_culture_republic").toOption
  def tolerance: Option[Modifier] = find("tolerance").toOption
  def intolerance: Option[Modifier] = find("intolerance").toOption

  def underSiege: Option[Modifier] = find("under_siege").toOption
  def blockaded: Option[Modifier] = find("blockaded").toOption
  def occupied: Option[Modifier] = find("occupied").toOption
  def devastation: Option[Modifier] = find("devastation").toOption
  def prosperity: Option[Modifier] = find("prosperity").toOption
  def slavesRaided: Option[Modifier] = find("slaves_raided").toOption
  def unrest: Option[Modifier] = find("unrest").toOption
  def recentUprising: Option[Modifier] = find("recent_uprising").toOption
  def nationalism: Option[Modifier] = find("nationalism").toOption
  def harshTreatment: Option[Modifier] = find("harsh_treatment").toOption
  def activeMissionary: Option[Modifier] = find("active_missionary").toOption
  def scorchedEarth: Option[Modifier] = find("scorched_earth").toOption

  def localAutonomyMultiplicative: Option[Modifier] = find("local_autonomy_multiplicative").toOption
  def localAutonomy: Option[Modifier] = find("local_autonomy").toOption
  def localAutonomyTradeCompanyMultiplicative: Option[Modifier] = find("local_autonomy_trade_company_multiplicative").toOption
  def localAutonomyTradeCompany: Option[Modifier] = find("local_autonomy_trade_company").toOption
  def friendlyRegiments: Option[Modifier] = find("friendly_regiments").toOption
  def nationalDefense: Option[Modifier] = find("national_defense").toOption
  def resourceDepleted: Option[Modifier] = find("resource_depleted").toOption
  def baseValues: Option[Modifier] = find("base_values").toOption
  def aiNation: Option[Modifier] = find("ai_nation").toOption
  def warTaxes: Option[Modifier] = find("war_taxes").toOption
  def stability: Option[Modifier] = find("stability").toOption
  def positiveStability: Option[Modifier] = find("positive_stability").toOption
  def privateering: Option[Modifier] = find("privateering").toOption
  def negativeStability: Option[Modifier] = find("negative_stability").toOption
  def positiveMandate: Option[Modifier] = find("positive_mandate").toOption
  def negativeMandate: Option[Modifier] = find("negative_mandate").toOption
  def lostMandateOfHeaven: Option[Modifier] = find("lost_mandate_of_heaven").toOption
  def cancelledLoan: Option[Modifier] = find("cancelled_loan").toOption
  def bankLoan: Option[Modifier] = find("bank_loan").toOption
  def inflation: Option[Modifier] = find("inflation").toOption
  def bankruptcy: Option[Modifier] = find("bankruptcy").toOption
  def war: Option[Modifier] = find("war").toOption
  def peace: Option[Modifier] = find("peace").toOption
  def unconditionalSurrender: Option[Modifier] = find("unconditional_surrender").toOption
  def callForPeace: Option[Modifier] = find("call_for_peace").toOption
  def warExhaustion: Option[Modifier] = find("war_exhaustion").toOption
  def doom: Option[Modifier] = find("doom").toOption
  def authority: Option[Modifier] = find("authority").toOption
  def regencyCouncil: Option[Modifier] = find("regency_council").toOption
  def tradeEfficiency: Option[Modifier] = find("trade_efficiency").toOption
  def productionEfficiency: Option[Modifier] = find("production_efficiency").toOption
  def tradeRefusal: Option[Modifier] = find("trade_refusal").toOption
  def mercantilism: Option[Modifier] = find("mercantilism").toOption
  def armyTradition: Option[Modifier] = find("army_tradition").toOption
  def navyTradition: Option[Modifier] = find("navy_tradition").toOption
  def positivePiety: Option[Modifier] = find("positive_piety").toOption
  def negativePiety: Option[Modifier] = find("negative_piety").toOption
  def defenderOfFaith: Option[Modifier] = find("defender_of_faith").toOption
  def defenderOfFaithRefusedCta: Option[Modifier] = find("defender_of_faith_refused_cta").toOption
  def emperor: Option[Modifier] = find("emperor").toOption
  def statesInHre: Option[Modifier] = find("states_in_hre").toOption
  def freeCitiesInHre: Option[Modifier] = find("free_cities_in_hre").toOption
  def freeCityInHre: Option[Modifier] = find("free_city_in_hre").toOption
  def memberInHre: Option[Modifier] = find("member_in_hre").toOption
  def occupiedImperial: Option[Modifier] = find("occupied_imperial").toOption
  def emperorRevokedReform: Option[Modifier] = find("emperor_revoked_reform").toOption
  def numOfMarriages: Option[Modifier] = find("num_of_marriages").toOption
  def numOfProvinces: Option[Modifier] = find("num_of_provinces").toOption
  def countryDevelopment: Option[Modifier] = find("country_development").toOption
  def tribalAllegiance: Option[Modifier] = find("tribal_allegiance").toOption
  def legitimacy: Option[Modifier] = find("legitimacy").toOption
  def hordeUnity: Option[Modifier] = find("horde_unity").toOption
  def devotion: Option[Modifier] = find("devotion").toOption
  def meritocracy: Option[Modifier] = find("meritocracy").toOption
  def lowMeritocracy: Option[Modifier] = find("low_meritocracy").toOption
  def corruption: Option[Modifier] = find("corruption").toOption
  def rootOutCorruption: Option[Modifier] = find("root_out_corruption").toOption
  def recoveryMotivation: Option[Modifier] = find("recovery_motivation").toOption
  def militarizedSociety: Option[Modifier] = find("militarized_society").toOption
  def luck: Option[Modifier] = find("luck").toOption
  def overExtension: Option[Modifier] = find("over_extension").toOption
  def prestige: Option[Modifier] = find("prestige").toOption
  def noDebateInParliament: Option[Modifier] = find("no_debate_in_parliament").toOption
  def republicanTradition: Option[Modifier] = find("republican_tradition").toOption
  def inverseRepublicanTradition: Option[Modifier] = find("inverse_republican_tradition").toOption
  def curiaController: Option[Modifier] = find("curia_controller").toOption
  def boughtIndulgence: Option[Modifier] = find("bought_indulgence").toOption
  def religiousUnity: Option[Modifier] = find("religious_unity").toOption
  def inverseReligiousUnity: Option[Modifier] = find("inverse_religious_unity").toOption
  def totalOccupation: Option[Modifier] = find("total_occupation").toOption
  def totalBlockaded: Option[Modifier] = find("total_blockaded").toOption
  def uncontestedCores: Option[Modifier] = find("uncontested_cores").toOption
  def numObjectivesFullfilled: Option[Modifier] = find("num_objectives_fullfilled").toOption
  def productionLeader: Option[Modifier] = find("production_leader").toOption
  def tradeCompanyBonus: Option[Modifier] = find("trade_company_bonus").toOption
  def bonusFromMerchantRepublics: Option[Modifier] = find("bonus_from_merchant_republics").toOption
  def bonusFromMerchantRepublicsForTradeLeagueMember: Option[Modifier] = find("bonus_from_merchant_republics_for_trade_league_member").toOption
  def merchantRepublicMechanicsModifier: Option[Modifier] = find("merchant_republic_mechanics_modifier").toOption
  def federationLeader: Option[Modifier] = find("federation_leader").toOption
  def tributaryStateBehindOverlordTechAdm: Option[Modifier] = find("tributary_state_behind_overlord_tech_adm").toOption
  def tributaryStateBehindOverlordTechDip: Option[Modifier] = find("tributary_state_behind_overlord_tech_dip").toOption
  def tributaryStateBehindOverlordTechMil: Option[Modifier] = find("tributary_state_behind_overlord_tech_mil").toOption
  def libertyDesire: Option[Modifier] = find("liberty_desire").toOption
  def isGreatPower: Option[Modifier] = find("is_great_power").toOption
  def inGoldenEra: Option[Modifier] = find("in_golden_era").toOption
  def absolutism: Option[Modifier] = find("absolutism").toOption
  def lowArmyProfessionalism: Option[Modifier] = find("low_army_professionalism").toOption
  def highArmyProfessionalism: Option[Modifier] = find("high_army_professionalism").toOption
  def streltsyModifier: Option[Modifier] = find("streltsy_modifier").toOption
  def powerProjection: Option[Modifier] = find("power_projection").toOption
  def powerProjection25: Option[Modifier] = find("power_projection_25").toOption
  def tradeCompanyStrong: Option[Modifier] = find("trade_company_strong").toOption
  def tradeCompanyDominant: Option[Modifier] = find("trade_company_dominant").toOption
  def largeColonialNation: Option[Modifier] = find("large_colonial_nation").toOption
  def marchSubject: Option[Modifier] = find("march_subject").toOption
  def vassalSubject: Option[Modifier] = find("vassal_subject").toOption
  def daimyoSubject: Option[Modifier] = find("daimyo_subject").toOption
  def unionSubject: Option[Modifier] = find("union_subject").toOption
  def allNations: Option[Modifier] = find("all_nations").toOption
  def subjectNation: Option[Modifier] = find("subject_nation").toOption
  def vassalNation: Option[Modifier] = find("vassal_nation").toOption
  def primitiveNation: Option[Modifier] = find("primitive_nation").toOption
  def maintainedForts: Option[Modifier] = find("maintained_forts").toOption
  def govRank1: Option[Modifier] = find("gov_rank_1").toOption
  def govRank2: Option[Modifier] = find("gov_rank_2").toOption
  def govRank3: Option[Modifier] = find("gov_rank_3").toOption
  def govRank4: Option[Modifier] = find("gov_rank_4").toOption
  def govRank5: Option[Modifier] = find("gov_rank_5").toOption
  def govRank6: Option[Modifier] = find("gov_rank_6").toOption
  def govRank7: Option[Modifier] = find("gov_rank_7").toOption
  def govRank8: Option[Modifier] = find("gov_rank_8").toOption
  def govRank9: Option[Modifier] = find("gov_rank_9").toOption
  def govRank10: Option[Modifier] = find("gov_rank_10").toOption
  def autonomyIncreased: Option[Modifier] = find("autonomy_increased").toOption
  def autonomyDecreased: Option[Modifier] = find("autonomy_decreased").toOption
  def revolutionTarget: Option[Modifier] = find("revolution_target").toOption
  def dishonouredAlliance: Option[Modifier] = find("dishonoured_alliance").toOption
  def drillingArmies: Option[Modifier] = find("drilling_armies").toOption
  def recruitmentSabotaged: Option[Modifier] = find("recruitment_sabotaged").toOption
  def merchantsSlandered: Option[Modifier] = find("merchants_slandered").toOption
  def discontentSowed: Option[Modifier] = find("discontent_sowed").toOption
  def reputationSabotaged: Option[Modifier] = find("reputation_sabotaged").toOption
  def corruptOfficials: Option[Modifier] = find("corrupt_officials").toOption
  def scaledTradeLeagueLeader: Option[Modifier] = find("scaled_trade_league_leader").toOption
  def inTradeLeague: Option[Modifier] = find("in_trade_league").toOption
  def tutorialColonialRange: Option[Modifier] = find("tutorial_colonial_range").toOption
  def customSetup: Option[Modifier] = find("custom_setup").toOption
  def embargoRivals: Option[Modifier] = find("embargo_rivals").toOption
  def scutage: Option[Modifier] = find("scutage").toOption
  def subsidizeArmies: Option[Modifier] = find("subsidize_armies").toOption
  def supportLoyalists: Option[Modifier] = find("support_loyalists").toOption
  def sendOfficers: Option[Modifier] = find("send_officers").toOption
  def divertTrade: Option[Modifier] = find("divert_trade").toOption
  def karmaJustRight: Option[Modifier] = find("karma_just_right").toOption
  def karmaTooHigh: Option[Modifier] = find("karma_too_high").toOption
  def karmaTooLow: Option[Modifier] = find("karma_too_low").toOption
  def invasionNation: Option[Modifier] = find("invasion_nation").toOption
  def nativePolicyCoexist: Option[Modifier] = find("native_policy_coexist").toOption
  def nativePolicyTrade: Option[Modifier] = find("native_policy_trade").toOption
  def nativePolicyHostile: Option[Modifier] = find("native_policy_hostile").toOption

  def highHarmony: Option[Modifier] = find("high_harmony").toOption
  def lowHarmony: Option[Modifier] = find("low_harmony").toOption
  def overlordDaimyoAtPeace: Option[Modifier] = find("overlord_daimyo_at_peace").toOption
  def overlordDaimyoAtPeaceMax: Option[Modifier] = find("overlord_daimyo_at_peace_max").toOption
  def overlordDaimyoAtPeaceMin: Option[Modifier] = find("overlord_daimyo_at_peace_min").toOption
  def overlordDaimyoSameIsolationism: Option[Modifier] = find("overlord_daimyo_same_isolationism").toOption
  def overlordDaimyoDifferentIsolationism: Option[Modifier] = find("overlord_daimyo_different_isolationism").toOption
  def overlordDaimyoIsolationismMax: Option[Modifier] = find("overlord_daimyo_isolationism_max").toOption
  def overlordDaimyoIsolationismMin: Option[Modifier] = find("overlord_daimyo_isolationism_min").toOption

  def overlordSankinKotai: Option[Modifier] = find("overlord_sankin_kotai").toOption
  def subjectSankinKotai: Option[Modifier] = find("subject_sankin_kotai").toOption
  def overlordExpelRonin: Option[Modifier] = find("overlord_expel_ronin").toOption
  def subjectExpelRonin: Option[Modifier] = find("subject_expel_ronin").toOption
  def overlordSwordHunt: Option[Modifier] = find("overlord_sword_hunt").toOption
  def subjectSwordHunt: Option[Modifier] = find("subject_sword_hunt").toOption

  def supplyDepotArea: Option[Modifier] = find("supply_depot_area").toOption
  def efficientTaxFarmingModifier: Option[Modifier] = find("efficient_tax_farming_modifier").toOption
  def landAcquisitionModifier: Option[Modifier] = find("land_acquisition_modifier").toOption
  def lenientTaxationModifier: Option[Modifier] = find("lenient_taxation_modifier").toOption
  def trainHorsemanshipModifier: Option[Modifier] = find("train_horsemanship_modifier").toOption
  def promoteCultureInGovernmentModifier: Option[Modifier] = find("promote_culture_in_government_modifier").toOption
  def seizeClericalHoldingsModifier: Option[Modifier] = find("seize_clerical_holdings_modifier").toOption
  def inviteMinoritiesModifier: Option[Modifier] = find("invite_minorities_modifier").toOption

  def hanafiScholarModifier: Option[Modifier] = find("hanafi_scholar_modifier").toOption
  def hanbaliScholarModifier: Option[Modifier] = find("hanbali_scholar_modifier").toOption
  def malikiScholarModifier: Option[Modifier] = find("maliki_scholar_modifier").toOption
  def shafiiScholarModifier: Option[Modifier] = find("shafii_scholar_modifier").toOption
  def ismailiScholarModifier: Option[Modifier] = find("ismaili_scholar_modifier").toOption
  def jafariScholarModifier: Option[Modifier] = find("jafari_scholar_modifier").toOption
  def zaidiScholarModifier: Option[Modifier] = find("zaidi_scholar_modifier").toOption

  def regimentDrillModifier: Option[Modifier] = find("regiment_drill_modifier").toOption
  def armyDrillModifier: Option[Modifier] = find("army_drill_modifier").toOption
  def janissaryRegiment: Option[Modifier] = find("janissary_regiment").toOption
  def revolutionaryGuardRegiment: Option[Modifier] = find("revolutionary_guard_regiment").toOption
  def innovativeness: Option[Modifier] = find("innovativeness").toOption
  def rajputRegiment: Option[Modifier] = find("rajput_regiment").toOption
  def raidingPartiesModifier: Option[Modifier] = find("raiding_parties_modifier").toOption
  def serfsRecievedByCossacks: Option[Modifier] = find("serfs_recieved_by_cossacks").toOption
  def cossacksModifier: Option[Modifier] = find("cossacks_modifier").toOption
  def expandAdministationModifier: Option[Modifier] = find("expand_administation_modifier").toOption
  def overGoverningCapacityModifier: Option[Modifier] = find("over_governing_capacity_modifier").toOption
  def lostHegemony: Option[Modifier] = find("lost_hegemony").toOption
  def atPeaceRevolutionary: Option[Modifier] = find("at_peace_revolutionary").toOption

}
