package com.lomicron.oikoumene.model.save.tag

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.databind.JsonNode
import com.lomicron.oikoumene.model.{Color, Entity}
import com.lomicron.utils.json.{FromJson, JsonMapper}
import com.lomicron.utils.parsing.tokenizer.Date

@JsonCreator
case class TagSave
(
  // hits = 1179, isOptional = false, sample = "---"
  id: String = Entity.UNDEFINED,
  // hits = 1179, isOptional = false, sample = {}
  activeIdeaGroups: JsonNode = JsonMapper.objectNode,
  // hits = 1179, isOptional = false, sample = {"SCA":{"has_changed":true},"GBR":{"has_changed":true},"SPA":{"has_changed":true},"TUS":{"has_changed":true},"RUS":{"has_changed":true},"TIM":{"has_changed":true},"JAP":{"has_changed":true},"BHA":{"has_changed":true},"C00":{"has_colony_claim":true},"C01":{"has_colony_claim":true},"C02":{"has_colony_claim":true},"C03":{"has_colony_claim":true},"C04":{"has_colony_claim":true},"C05":{"has_colony_claim":true},"C06":{"has_colony_claim":true},"C07":{"has_colony_claim":true},"C08":{"has_colony_claim":t
  activeRelations: Map[String, TagRelation] = Map.empty,
  // hits = 1179, isOptional = false, sample = [0.000,0.000,0.000,0.000]
  ageScore: Seq[BigDecimal] = Seq.empty,
  // hits = 1179, isOptional = false, sample = {"initialized":false,"initialized_attitudes":false,"static":false,"personality":"nopersonality","last_recalc_date":"1444.11.11","hre_interest":false,"papacy_interest":false,"needs_regiments":-1,"needs_money":false,"needs_buildings":true,"needs_ships":true,"powers":[0,0,0]}
  ai: TagAI = TagAI.empty,
  // hits = 1179, isOptional = false, sample = 0.000
  blockadeMission: BigDecimal = BigDecimal(0),
  // hits = 1179, isOptional = false, sample = false
  canTakeWartaxes: Boolean = false,
  // hits = 1179, isOptional = false, sample = 0
  capital: Int = 0,
  // hits = 1179, isOptional = false, sample = 0.000
  cappedDevelopment: BigDecimal = BigDecimal(0),
  // hits = 1179, isOptional = false, sample = false
  casusBellisRefresh: Option[Boolean] = None,
  // hits = 1179, isOptional = false, sample = 1.000
  colonialMaintenance: BigDecimal = BigDecimal(0),
  // hits = 1179, isOptional = false, sample = {"map_color":[150,150,150],"country_color":[150,150,150]}
  colors: TagColors = TagColors.empty,
  // hits = 1179, isOptional = false, sample = [0,0,0,0,0,0,0]
  continent: Seq[Int] = Seq.empty,
  // hits = 1179, isOptional = false, sample = {}
  countryMissions: JsonNode = JsonMapper.objectNode,
  // hits = 1179, isOptional = false, sample = -1
  decisionSeed: BigDecimal = BigDecimal(0),
  // hits = 1179, isOptional = false, sample = false
  dirtyColony: Option[Boolean] = None,
  // hits = 1179, isOptional = false, sample = 0.000
  estimatedLoan: BigDecimal = BigDecimal(0),
  // hits = 1179, isOptional = false, sample = 0.000
  estimatedMonthlyIncome: BigDecimal = BigDecimal(0),
  // hits = 1179, isOptional = false, sample = 2
  governmentRank: Int = 0,
  // hits = 1179, isOptional = false, sample = 1
  highestPossibleFort: Int = 0,
  // hits = 1179, isOptional = false, sample = "native_palisade"
  highestPossibleFortBuilding: String = Entity.UNDEFINED,
  // hits = 1179, isOptional = false, sample = {"starting_num_of_states":0,"starting_development":0.000,"starting_income":0.000}
  historicStatsCache: JsonNode = JsonMapper.objectNode,
  // hits = 1179, isOptional = false, sample = [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]
  ideaMayCache: Seq[Int] = Seq.empty,
  // hits = 1179, isOptional = false, sample = "1.1.1"
  inauguration: Date = Date.zero,
  // hits = 1179, isOptional = false, sample = false
  initializedRivals: Option[Boolean] = None,
  // hits = 1179, isOptional = false, sample = 0.000
  innovativeness: BigDecimal = BigDecimal(0),
  // hits = 1179, isOptional = false, sample = [0,0,0,0,0,0,0]
  institutions: Seq[Int] = Seq.empty,
  // hits = 1179, isOptional = false, sample = 1
  isolationism: Int = 0,
  // hits = 1179, isOptional = false, sample = 1.000
  landMaintenance: BigDecimal = BigDecimal(0),
  // hits = 1179, isOptional = false, sample = "1.1.1"
  lastWarEnded: Date = Date.zero,
  // hits = 1179, isOptional = false, sample = {"totalexpensetable":[0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000]}
  ledger: JsonNode = JsonMapper.objectNode,
  // hits = 1179, isOptional = false, sample = 6
  loanSize: Int = 0,
  // hits = 1179, isOptional = false, sample = {"members":[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]}
  losses: JsonNode = JsonMapper.objectNode,
  // hits = 1179, isOptional = false, sample = 0.000
  manpower: BigDecimal = BigDecimal(0),
  // hits = 1179, isOptional = false, sample = 1.000
  maxManpower: BigDecimal = BigDecimal(0),
  // hits = 1179, isOptional = false, sample = 0.000
  maxSailors: BigDecimal = BigDecimal(0),
  // hits = 1179, isOptional = false, sample = 10.000
  mercantilism: BigDecimal = BigDecimal(0),
  // hits = 1179, isOptional = false, sample = 1.000
  missionaryMaintenance: BigDecimal = BigDecimal(0),
  // hits = 1179, isOptional = false, sample = 1.000
  navalMaintenance: BigDecimal = BigDecimal(0),
  // hits = 1179, isOptional = false, sample = -1.00000
  navyStrength: BigDecimal = BigDecimal(0),
  // hits = 1179, isOptional = false, sample = true
  needsRebelUnitRefresh: Option[Boolean] = None,
  // hits = 1179, isOptional = false, sample = true
  needsRefresh: Option[Boolean] = None,
  // hits = 1179, isOptional = false, sample = 0
  numOfCapturedShipsWithBoardingDoctrine: Int = 0,
  // hits = 1179, isOptional = false, sample = 0
  numOfConsorts: Int = 0,
  // hits = 1179, isOptional = false, sample = 0
  numUncontestedCores: Int = 0,
  // hits = 1179, isOptional = false, sample = [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
  opinionCache: Seq[Int] = Seq.empty,
  // hits = 1179, isOptional = false, sample = [0,0,0]
  powers: Seq[Int] = Seq.empty,
  // hits = 1179, isOptional = false, sample = 0.000
  prestige: BigDecimal = BigDecimal(0),
  // hits = 1179, isOptional = false, sample = 0.000
  realmDevelopment: BigDecimal = BigDecimal(0),
  // hits = 1179, isOptional = false, sample = true
  recalculateStrategy: Option[Boolean] = None,
  // hits = 1179, isOptional = false, sample = 191
  scorePlace: Int = 0,
  // hits = 1179, isOptional = false, sample = [121,136,127]
  scoreRank: Color = Color.black,
  // hits = 1179, isOptional = false, sample = [0.000,0.000,0.000]
  scoreRating: Seq[BigDecimal] = Seq.empty,
  // hits = 1179, isOptional = false, sample = 0.000
  splendor: BigDecimal = BigDecimal(0),
  // hits = 1179, isOptional = false, sample = 0.000
  stability: BigDecimal = BigDecimal(0),
  // hits = 1179, isOptional = false, sample = 0
  subjectFocus: Int = 0,
  // hits = 1179, isOptional = false, sample = {"adm_tech":0,"dip_tech":0,"mil_tech":0}
  technology: JsonNode = JsonMapper.objectNode,
  // hits = 1179, isOptional = false, sample = [0,0,0,0,0,0,0]
  totalCount: Seq[Int] = Seq.empty,
  // hits = 1179, isOptional = false, sample = 0.000
  tradeMission: BigDecimal = BigDecimal(0),
  // hits = 1179, isOptional = false, sample = 0
  tradePort: Int = 0,
  // hits = 1179, isOptional = false, sample = 0.000
  transferHomeBonus: BigDecimal = BigDecimal(0),
  // hits = 1179, isOptional = false, sample = 0.000
  treasury: BigDecimal = BigDecimal(0),
  // hits = 1179, isOptional = false, sample = [0,0,0,0,0,0,0]
  underConstruction: Seq[Int] = Seq.empty,
  // hits = 1179, isOptional = false, sample = [0,0,0,0,0,0,0]
  underConstructionQueued: Seq[Int] = Seq.empty,
  // hits = 1179, isOptional = false, sample = false
  updateOpinionCache: Option[Boolean] = None,
  // hits = 1179, isOptional = false, sample = [0.000,0.000,0.000,0.000]
  vcAgeScore: Seq[BigDecimal] = Seq.empty,
  // hits = 1177, isOptional = true, sample = 1.000
  religiousUnity: Option[BigDecimal] = None,
  // hits = 1168, isOptional = true, sample = true
  assignedEstates: Boolean = false,
  // hits = 1168, isOptional = true, sample = [0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.003,0.002,0.002,0.002,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000]
  inflationHistory: Seq[BigDecimal] = Seq.empty,
  // hits = 1168, isOptional = true, sample = "western"
  unitType: Option[String] = None,
  // hits = 1167, isOptional = true, sample = 100.000
  legitimacy: Option[BigDecimal] = None,
  // hits = 1167, isOptional = true, sample = 0.000
  meritocracy: Option[BigDecimal] = None,
  // hits = 1162, isOptional = true, sample = "1444.11.11"
  lastElection: Option[Date] = None,
  // hits = 1155, isOptional = true, sample = 0.000
  devotion: Option[BigDecimal] = None,
  // hits = 1106, isOptional = true, sample = 3.180
  technologyCost: Option[BigDecimal] = None,
  // hits = 830, isOptional = true, sample = {"technology_group":"western","1448.9.2":{"leader":{"name":"Abdallah Nazhar","type":"general","manuever":4,"shock":1,"siege":1,"activation":"1448.9.2","id":{"id":2199,"type":49}}},"1452.12.20":{"leader":{"name":"Jayaprakasamalla Yadav","type":"general","manuever":1,"fire":3,"siege":1,"personality":"glory_seeker_personality","activation":"1452.12.20","id":{"id":2592,"type":49}}},"1456.5.11":{"leader":{"name":"Isma'il Shammas","type":"general","fire":3,"personality":"goal_oriented_personality","ac
  history: Option[JsonNode] = None,
  // hits = 820, isOptional = true, sample = "western"
  technologyGroup: Option[String] = None,
  // hits = 817, isOptional = true, sample = {"government":"monarchy","reform_stack":{"reforms":["monarchy_mechanic","feudalism_reform","quash_noble_power_reform","centralize_reform","of_noble_bearing_reform","royal_decree_reform"],"history":["feudalism_reform","quash_noble_power_reform","centralize_reform","of_noble_bearing_reform","royal_decree_reform"]}}
  government: Option[JsonNode] = None,
  // hits = 817, isOptional = true, sample = "default_monarchy"
  governmentName: Option[String] = None,
  // hits = 817, isOptional = true, sample = 1
  originalCapital: Option[Int] = None,
  // hits = 817, isOptional = true, sample = {"infantry":"italian_condotta","cavalry":"schwarze_reiter","artillery":"culverin","heavy_ship":"carrack","light_ship":"caravel","galley":"galley","transport":"cog"}
  subUnit: Option[JsonNode] = None,
  // hits = 816, isOptional = true, sample = "swedish"
  dominantCulture: Option[String] = None,
  // hits = 816, isOptional = true, sample = "catholic"
  dominantReligion: Option[String] = None,
  // hits = 816, isOptional = true, sample = "swedish"
  primaryCulture: Option[String] = None,
  // hits = 816, isOptional = true, sample = "catholic"
  religion: Option[String] = None,
  // hits = 774, isOptional = true, sample = 750.000
  governmentReformProgress: Option[BigDecimal] = None,
  // hits = 657, isOptional = true, sample = 0.100
  rootOutCorruptionSlider: Option[BigDecimal] = None,
  // hits = 639, isOptional = true, sample = {"id":14132,"type":48}
  monarch: Option[JsonNode] = None,
  // hits = 592, isOptional = true, sample = true
  hasSetGovernmentName: Boolean = false,
  // hits = 588, isOptional = true, sample = 100.000
  armyTradition: Option[BigDecimal] = None,
  // hits = 587, isOptional = true, sample = {"0":2787,"1":4699,"2":201}
  admSpentIndexed: Option[JsonNode] = None,
  // hits = 579, isOptional = true, sample = 0.09280
  maxHistoricArmyProfessionalism: Option[BigDecimal] = None,
  // hits = 576, isOptional = true, sample = 0.06781
  armyProfessionalism: Option[BigDecimal] = None,
  // hits = 567, isOptional = true, sample = "Munk"
  originalDynasty: Option[String] = None,
  // hits = 565, isOptional = true, sample = [{"id":2360,"type":48},{"id":12292,"type":48},{"id":13654,"type":48},{"id":13537,"type":48},{"id":13980,"type":48}]
  previousMonarch: Seq[JsonNode] = Seq.empty,
  // hits = 549, isOptional = true, sample = {"0":1628,"1":4027,"22":152}
  dipSpentIndexed: Option[JsonNode] = None,
  // hits = 542, isOptional = true, sample = {"0":809,"1":5396,"7":740}
  milSpentIndexed: Option[JsonNode] = None,
  // hits = 541, isOptional = true, sample = 0.126
  corruption: Option[BigDecimal] = None,
  // hits = 504, isOptional = true, sample = "1542.1.29"
  antiNationRuiningEndDate: Option[Date] = None,
  // hits = 496, isOptional = true, sample = 4.758
  navyTradition: Option[BigDecimal] = None,
  // hits = 488, isOptional = true, sample = 104.000
  greatPowerScore: Option[BigDecimal] = None,
  // hits = 484, isOptional = true, sample = ["build_army_mission","high_income_mission"]
  completedMissions: Seq[String] = Seq.empty,
  // hits = 476, isOptional = true, sample = {"has_won_war":"1536.12.14"}
  hiddenFlags: Option[JsonNode] = None,
  // hits = 474, isOptional = true, sample = {"last_jousting_tournament_held":"1483.1.2","SWE_had_event_3219":"1521.12.18","epic_to_monarch":"1525.3.13","commedia_dellarte":"1527.10.29"}
  flags: Option[JsonNode] = None,
  // hits = 472, isOptional = true, sample = "1693.1.6"
  lastSentAllianceOffer: Option[Date] = None,
  // hits = 412, isOptional = true, sample = [{"target":"DNZ","modifier":"embargoing_rival","current":3.000},{"target":"KLE","modifier":"embargoing_rival","current":2.088},{"target":"SHL","modifier":"current_age_power_projection","current":3.000},{"target":"SHL","modifier":"has_rival","current":15.000}]
  powerProjection: Seq[JsonNode] = Seq.empty,
  // hits = 407, isOptional = true, sample = [1,2,3,4,5,7,8,9,11,25,27,1930,1985,4163,4164,4166]
  coreProvinces: Seq[Int] = Seq.empty,
  // hits = 385, isOptional = true, sample = "Caraibas"
  adjective: Option[String] = None,
  // hits = 385, isOptional = true, sample = "Caraibas"
  name: Option[String] = None,
  // hits = 332, isOptional = true, sample = 35.000
  recoveryMotivation: Option[BigDecimal] = None,
  // hits = 316, isOptional = true, sample = {"given_teacher_culture":-1.000,"new_monarch_culture":-1.000,"new_ruler_culture":-1.000,"new_ruler_religion":-1.000,"other_advisor_culture":-1.000}
  variables: Option[JsonNode] = None,
  // hits = 289, isOptional = true, sample = 2142.608
  sailors: Option[BigDecimal] = None,
  // hits = 226, isOptional = true, sample = true
  newMonarch: Boolean = false,
  // hits = 224, isOptional = true, sample = "1512.11.5"
  lastFocusMove: Option[Date] = None,
  // hits = 175, isOptional = true, sample = 265429.346
  delayedTreasure: Option[BigDecimal] = None,
  // hits = 170, isOptional = true, sample = 128.879
  papalInfluence: Option[BigDecimal] = None,
  // hits = 162, isOptional = true, sample = "hanafi_school"
  religiousSchool: Option[String] = None,
  // hits = 158, isOptional = true, sample = 60.000
  baseTax: Option[BigDecimal] = None,
  // hits = 147, isOptional = true, sample = "ab_no_distance_for_core"
  activeAgeAbility: Seq[String] = Seq.empty,
  // hits = 139, isOptional = true, sample = "finnish"
  acceptedCulture: Seq[String] = Seq.empty,
  // hits = 137, isOptional = true, sample = 13.230
  averageAutonomy: Option[BigDecimal] = None,
  // hits = 137, isOptional = true, sample = 13.230
  averageAutonomyAboveMin: Option[BigDecimal] = None,
  // hits = 137, isOptional = true, sample = 0.000
  averageEffectiveUnrest: Option[BigDecimal] = None,
  // hits = 137, isOptional = true, sample = 13.230
  averageHomeAutonomy: Option[BigDecimal] = None,
  // hits = 137, isOptional = true, sample = -52.599
  averageUnrest: Option[BigDecimal] = None,
  // hits = 137, isOptional = true, sample = 0.750
  inlandSeaRatio: Option[BigDecimal] = None,
  // hits = 137, isOptional = true, sample = 104.000
  nonOverseasDevelopment: Option[BigDecimal] = None,
  // hits = 137, isOptional = true, sample = 2
  numOfAllies: Option[Int] = None,
  // hits = 137, isOptional = true, sample = 5
  numOfCities: Option[Int] = None,
  // hits = 137, isOptional = true, sample = 5
  numOfControlledCities: Option[Int] = None,
  // hits = 137, isOptional = true, sample = 4
  numOfCorePorts: Option[Int] = None,
  // hits = 137, isOptional = true, sample = [1,0,0,0]
  numOfFreeLeaders: Seq[Int] = Seq.empty,
  // hits = 137, isOptional = true, sample = [0,0,0,0,1,1,0,1,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]
  numOfGoodsProduced: Seq[Int] = Seq.empty,
  // hits = 137, isOptional = true, sample = [2,0,0,0]
  numOfLeaders: Seq[Int] = Seq.empty,
  // hits = 137, isOptional = true, sample = [1,0,0,0]
  numOfLeadersWithTraits: Seq[Int] = Seq.empty,
  // hits = 137, isOptional = true, sample = 4
  numOfPorts: Option[Int] = None,
  // hits = 137, isOptional = true, sample = 5
  numOfProvincesInStates: Option[Int] = None,
  // hits = 137, isOptional = true, sample = 0
  numOfProvincesInTerritories: Option[Int] = None,
  // hits = 137, isOptional = true, sample = 18
  numOfRegulars: Option[Int] = None,
  // hits = 137, isOptional = true, sample = 0
  numOfRoyalMarriages: Option[Int] = None,
  // hits = 137, isOptional = true, sample = 4
  numOfTotalPorts: Option[Int] = None,
  // hits = 137, isOptional = true, sample = 5
  numOwnedHomeCores: Option[Int] = None,
  // hits = 137, isOptional = true, sample = [0.000,0.000,0.000,0.000,0.240,4.180,0.000,1.980,1.973,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000]
  producedGoodsValue: Seq[BigDecimal] = Seq.empty,
  // hits = 137, isOptional = true, sample = 131
  totalWarWorth: Option[Int] = None,
  // hits = 137, isOptional = true, sample = [0.000,2.421,0.389,0.850,2.712,5.711,0.115,1.423,4.162,0.258,0.087,0.893,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.158,0.000,2.595,0.000,0.276,0.323,0.036,0.000,0.000]
  traded: Seq[BigDecimal] = Seq.empty,
  // hits = 135, isOptional = true, sample = 228.293
  spyPropensity: Option[BigDecimal] = None,
  // hits = 132, isOptional = true, sample = 146
  fixedCapital: Option[Int] = None,
  // hits = 131, isOptional = true, sample = 1.000
  piety: Option[BigDecimal] = None,
  // hits = 129, isOptional = true, sample = 100.000
  republicanTradition: Option[BigDecimal] = None,
  // hits = 128, isOptional = true, sample = [{"id":{"id":87586,"type":54},"name":"1st Army","rebel_faction":{"id":56065,"type":50},"previous":412,"previous_war":412,"location":4342,"regiment":{"id":{"id":87587,"type":54},"name":"Natukhai's 1st Mercenary Infantry ","home":463,"type":"saxon_infantry","morale":2.500,"strength":0.777,"category":1},"movement_progress_last_updated":"1749.8.22","graphical_culture":"easterngfx","main_army":true,"attrition_level":3.000,"visible_to_ai":true,"attrition":false},{"id":{"id":57265,"type":50},"name":"1s
  army: Seq[JsonNode] = Seq.empty,
  // hits = 128, isOptional = true, sample = [649,1177,1179,1178,2864,1176,2880,646,607,4083,2948,301,1241,133,2698,1192,1187,2697,1138,70,219,1770,4087,4349,1778,4085,656,134,76,1097,217,4092,1826,265,642,372,431,2951,215,1983,4093,130,2769,2715,4379,2764,1186,2219]
  controlledProvinces: Seq[Int] = Seq.empty,
  // hits = 128, isOptional = true, sample = [{"modifier":"national_bank_inflation","date":"1762.6.21"},{"modifier":"the_conventicle_act","date":"-1.1.1"},{"modifier":"the_test_act","date":"-1.1.1"},{"modifier":"the_popery_act","date":"-1.1.1"},{"modifier":"sunday_schools","date":"-1.1.1"},{"modifier":"the_blasphemy_act","date":"-1.1.1"},{"modifier":"the_dissolution_of_the_monasteries","date":"-1.1.1"},{"modifier":"discontent_sowed","date":"1751.3.20"},{"modifier":"minor_trade_crisis","date":"1749.10.25"},{"modifier":"dip_jusitified_demand
  modifier: Seq[JsonNode] = Seq.empty,
  // hits = 127, isOptional = true, sample = [44,1857,2996,46,45]
  borderProvinces: Seq[Int] = Seq.empty,
  // hits = 127, isOptional = true, sample = 88.719
  development: Option[BigDecimal] = None,
  // hits = 127, isOptional = true, sample = {"envoy":[{"action":2,"name":"Ernst Augustenborg","type":3,"id":0},{"action":2,"name":"Karl Ludwig Rantzau","type":3,"id":1},{"action":2,"name":"Johann Christian Oldenburg","type":3,"id":2},{"action":2,"name":"Kaspar Rantzau","type":3,"id":3},{"name":"Karl Anton Holstein-Gottorp","type":3,"id":4},{"name":"Magnus Albert","type":3,"id":5},{"name":"Philip Ludwig von Barner","type":3,"id":6},{"name":"Bruno Günther","type":3,"id":7}]}
  diplomats: Option[JsonNode] = None,
  // hits = 127, isOptional = true, sample = ["SHL"]
  friendTags: Seq[String] = Seq.empty,
  // hits = 127, isOptional = true, sample = {"envoy":[{"action":2,"name":"Karl Rantzau","type":1,"id":0},{"action":2,"name":"Karl Gustav von Dellwigh","type":1,"id":1},{"action":2,"name":"Johann Oldenburg","type":1,"id":2},{"action":2,"name":"Alexander von Dellwigh","type":1,"id":3}]}
  merchants: Option[JsonNode] = None,
  // hits = 127, isOptional = true, sample = {"envoy":{"name":"Johann Albert","type":2,"id":0}}
  missionaries: Option[JsonNode] = None,
  // hits = 127, isOptional = true, sample = {"1":41.000,"3":63.000}
  numOfReligionsDev: Option[JsonNode] = None,
  // hits = 127, isOptional = true, sample = {"1":2,"3":3}
  numOfReligionsIndexed: Option[JsonNode] = None,
  // hits = 127, isOptional = true, sample = [44,1857,2996,46,45]
  ownedProvinces: Seq[Int] = Seq.empty,
  // hits = 127, isOptional = true, sample = 104.000
  rawDevelopment: Option[BigDecimal] = None,
  // hits = 124, isOptional = true, sample = 0.063
  warExhaustion: Option[BigDecimal] = None,
  // hits = 123, isOptional = true, sample = 43.350
  absolutism: Option[BigDecimal] = None,
  // hits = 121, isOptional = true, sample = {"81":0.083,"125":0.083,"136":0.666,"140":0.166}
  borderPct: Option[JsonNode] = None,
  // hits = 121, isOptional = true, sample = {"81":8.000,"125":8.000,"136":14.000,"140":5.000}
  borderSit: Option[JsonNode] = None,
  // hits = 121, isOptional = true, sample = ["KLE","LUN","BRA","DNZ"]
  neighbours: Seq[String] = Seq.empty,
  // hits = 119, isOptional = true, sample = {"3":3,"11":1,"12":2,"14":4,"16":1,"17":2,"18":2,"21":2,"22":2,"23":3}
  numOfBuildingsIndexed: Option[JsonNode] = None,
  // hits = 116, isOptional = true, sample = ["KLE","LUN","BRA","DNZ"]
  coreNeighbours: Seq[String] = Seq.empty,
  // hits = 116, isOptional = true, sample = ["KLE","LUN","BRA","DNZ"]
  homeNeighbours: Seq[String] = Seq.empty,
  // hits = 115, isOptional = true, sample = [{"policy":"the_dissolution_act","date":"1696.10.15"},{"policy":"the_tenures_abolition_act","date":"1683.7.5"}]
  activePolicy: Seq[JsonNode] = Seq.empty,
  // hits = 107, isOptional = true, sample = "1539.8.1"
  lastBankrupt: Option[Date] = None,
  // hits = 103, isOptional = true, sample = 23.000
  currentPowerProjection: Option[BigDecimal] = None,
  // hits = 102, isOptional = true, sample = [{"id":18540,"type":49},{"id":18590,"type":49},{"id":18786,"type":49},{"id":18799,"type":49},{"id":18826,"type":49},{"id":18937,"type":49},{"id":18975,"type":49},{"id":18981,"type":49},{"id":19016,"type":49},{"id":18930,"type":49},{"id":19081,"type":49},{"id":19089,"type":49},{"id":19094,"type":49},{"id":19097,"type":49},{"id":19124,"type":49},{"id":19137,"type":49},{"id":19138,"type":49},{"id":19139,"type":49},{"id":19146,"type":49},{"id":19155,"type":49},{"id":19161,"type":49},{"id":19163,"typ
  leader: Seq[JsonNode] = Seq.empty,
  // hits = 98, isOptional = true, sample = 1
  nativePolicy: Option[Int] = None,
  // hits = 97, isOptional = true, sample = [{"type":"estate_church","loyalty":50.000,"interaction_use":{"interaction":1,"date":"1734.5.8"},"active_loyalties":[5]},{"type":"estate_nobles","loyalty":50.000,"active_loyalties":[10]},{"type":"estate_burghers","loyalty":32.708,"influence_modifier":{"value":-10.000,"desc":"EST_VAL_CITY_PRIVILEGES_DENIED","date":"1764.5.11"},"active_influences":[0,3,9]}]
  estate: Seq[JsonNode] = Seq.empty,
  // hits = 96, isOptional = true, sample = [{"country":"KLE","date":"1725.2.8"},{"country":"BRA","date":"1733.4.10"},{"country":"DNZ","date":"1736.8.12"}]
  rival: Seq[JsonNode] = Seq.empty,
  // hits = 94, isOptional = true, sample = ["DNZ","BRA","KLE"]
  ourSpyNetwork: Seq[String] = Seq.empty,
  // hits = 93, isOptional = true, sample = ["BRA","DNZ","KLE"]
  enemy: Seq[String] = Seq.empty,
  // hits = 84, isOptional = true, sample = ["AAC","BOH"]
  callToArmsFriends: Seq[String] = Seq.empty,
  // hits = 83, isOptional = true, sample = {"id":19522,"type":48}
  heir: Option[JsonNode] = None,
  // hits = 83, isOptional = true, sample = ["DNZ","BRA","KLE"]
  theirSpyNetwork: Seq[String] = Seq.empty,
  // hits = 82, isOptional = true, sample = ["AAC","BOH"]
  allies: Seq[String] = Seq.empty,
  // hits = 82, isOptional = true, sample = ["AAC","BOH"]
  extendedAllies: Seq[String] = Seq.empty,
  // hits = 81, isOptional = true, sample = "ADM"
  nationalFocus: Option[String] = None,
  // hits = 81, isOptional = true, sample = 1
  numOfAgeObjectives: Option[Int] = None,
  // hits = 79, isOptional = true, sample = {"last_debate":"1740.4.1","recalculate_issues":false,"enacted_parliament_issue":"impressment_of_sailors"}
  parliament: Option[JsonNode] = None,
  // hits = 75, isOptional = true, sample = [{"id":67807,"type":51},{"id":66759,"type":51}]
  advisor: Seq[JsonNode] = Seq.empty,
  // hits = 73, isOptional = true, sample = 8.402
  inflation: Option[BigDecimal] = None,
  // hits = 70, isOptional = true, sample = [4141]
  claimProvinces: Seq[Int] = Seq.empty,
  // hits = 70, isOptional = true, sample = "shiva"
  personalDeity: Option[String] = None,
  // hits = 69, isOptional = true, sample = true
  inDebt: Boolean = false,
  // hits = 68, isOptional = true, sample = [{"id":{"id":84039,"type":54},"name":"1st Fleet","previous":1268,"previous_war":1268,"location":44,"ship":[{"id":{"id":84040,"type":54},"name":"Grömtz","home":1775,"type":"great_frigate","morale":4.158,"morale_damage":0.200,"last_target":{"id":72308,"type":54}},{"id":{"id":81688,"type":54},"name":"Wedel","home":1775,"type":"great_frigate","morale":4.158,"morale_damage":0.200,"last_target":{"id":72308,"type":54}},{"id":{"id":81304,"type":54},"name":"Ostsee","home":1775,"type":"great_frigate","mor
  navy: Seq[JsonNode] = Seq.empty,
  // hits = 65, isOptional = true, sample = 3
  forts: Option[Int] = None,
  // hits = 64, isOptional = true, sample = ["yemoja_cult","roog_cult","nyame_cult","islam_cult"]
  unlockCult: Seq[String] = Seq.empty,
  // hits = 55, isOptional = true, sample = ["AAC","HAB"]
  gaveAccess: Seq[String] = Seq.empty,
  // hits = 54, isOptional = true, sample = "1640.8.16"
  wartax: Option[Date] = None,
  // hits = 49, isOptional = true, sample = "1551.8.1"
  lastMigration: Option[Date] = None,
  // hits = 48, isOptional = true, sample = 3
  numOfHeathenProvs: Option[Int] = None,
  // hits = 43, isOptional = true, sample = 2
  numOfTradeEmbargos: Option[Int] = None,
  // hits = 43, isOptional = true, sample = ["DNZ","KLE"]
  tradeEmbargoes: Seq[String] = Seq.empty,
  // hits = 42, isOptional = true, sample = "islam_cult"
  fetishistCult: Option[String] = None,
  // hits = 42, isOptional = true, sample = 2
  numOfHereticProvs: Option[Int] = None,
  // hits = 42, isOptional = true, sample = {"prominent_merchant":"Konstantinos Rendis"}
  savedNames: Option[JsonNode] = None,
  // hits = 41, isOptional = true, sample = {"id":19561,"type":48}
  queen: Option[JsonNode] = None,
  // hits = 40, isOptional = true, sample = {"nahuatl_reforms":[0,0,0,0,0]}
  activeReligiousReform: Option[JsonNode] = None,
  // hits = 38, isOptional = true, sample = {"incident_neo_confucianism":6.000,"incident_nanban":4.000,"incident_firearms":2.000,"incident_spread_of_christianity":8.000,"incident_shogunate_authority":0.000,"incident_ikko_shu":4.000,"incident_wokou":9.000,"incident_urbanization":0.000}
  incidentVariables: Option[JsonNode] = None,
  // hits = 38, isOptional = true, sample = ["incident_shogunate_authority","incident_urbanization"]
  potentialIncidents: Seq[String] = Seq.empty,
  // hits = 37, isOptional = true, sample = "POR"
  colonialParent: Option[String] = None,
  // hits = 37, isOptional = true, sample = "MNG"
  overlord: Option[String] = None,
  // hits = 36, isOptional = true, sample = "1725.2.7"
  lastConversion: Option[Date] = None,
  // hits = 36, isOptional = true, sample = ["GBR"]
  transferTradePowerTo: Seq[String] = Seq.empty,
  // hits = 35, isOptional = true, sample = {"envoy":[{"action":2,"name":"Rettil Trolle","type":0,"id":0},{"action":2,"name":"Einar Rehnskiöld","type":0,"id":1}]}
  colonists: Option[JsonNode] = None,
  // hits = 34, isOptional = true, sample = ["BHA","K00","K01","K02"]
  currentAtWarWith: Seq[String] = Seq.empty,
  // hits = 34, isOptional = true, sample = true
  isAtWar: Boolean = false,
  // hits = 31, isOptional = true, sample = "1589.3.24"
  lastSoldProvince: Option[Date] = None,
  // hits = 31, isOptional = true, sample = ["DNZ","KLE"]
  tradeEmbargoedBy: Seq[String] = Seq.empty,
  // hits = 30, isOptional = true, sample = {"power":70.332,"aspect":["holy_sacraments_aspect","parish_registers_aspect","priests_may_marry_aspect"]}
  church: Option[JsonNode] = None,
  // hits = 30, isOptional = true, sample = ["DAN"]
  historicalRivals: Seq[String] = Seq.empty,
  // hits = 30, isOptional = true, sample = {"22":1}
  numOfBuildingsUnderConstructionIndexed: Option[JsonNode] = None,
  // hits = 28, isOptional = true, sample = ["MNG"]
  currentWarAllies: Seq[String] = Seq.empty,
  // hits = 28, isOptional = true, sample = [{"influence":48.419,"type":"mr_aristocrats","old_influence":48.419},{"influence":1.021,"type":"mr_traders","old_influence":1.021},{"influence":50.560,"type":"mr_guilds","old_influence":50.560}]
  faction: Seq[JsonNode] = Seq.empty,
  // hits = 28, isOptional = true, sample = {"value":2.651}
  fervor: Option[JsonNode] = None,
  // hits = 28, isOptional = true, sample = 6
  topFaction: Option[Int] = None,
  // hits = 27, isOptional = true, sample = "westerngfx"
  graphicalCulture: Option[String] = None,
  // hits = 25, isOptional = true, sample = 1
  goldtype: Option[Int] = None,
  // hits = 24, isOptional = true, sample = 10.000
  karma: Option[BigDecimal] = None,
  // hits = 23, isOptional = true, sample = 100.000
  harmony: Option[BigDecimal] = None,
  // hits = 22, isOptional = true, sample = {"timed_modifier":"demanded_additional_tribute","time_duration":{"arrayElements":{"type":193,"value":55.163}}}
  temporaryLibertyDesire: Seq[JsonNode] = Seq.empty,
  // hits = 21, isOptional = true, sample = 501
  colonialNameSource: Option[Int] = None,
  // hits = 21, isOptional = true, sample = [{"id":{"id":487273,"type":4713},"lender":"---","interest":3.250,"fixed_interest":false,"amount":1093,"expiry_date":"1753.2.1","spawned":false},{"id":{"id":491015,"type":4713},"lender":"---","interest":3.250,"fixed_interest":false,"amount":1130,"expiry_date":"1752.11.1","spawned":false},{"id":{"id":517415,"type":4713},"lender":"---","interest":3.250,"fixed_interest":false,"amount":1173,"expiry_date":"1752.3.1","spawned":false},{"id":{"id":520251,"type":4713},"lender":"---","interest":3.250,"fixe
  loan: Seq[JsonNode] = Seq.empty,
  // hits = 20, isOptional = true, sample = {"name":"Palatine-Bavarian Nationalist War","value":1.000,"date":"1749.1.15"}
  effectiveScoreImpact: Option[JsonNode] = None,
  // hits = 20, isOptional = true, sample = "SCA"
  previousCountryTags: Option[String] = None,
  // hits = 19, isOptional = true, sample = ["DAN"]
  historicalFriends: Seq[String] = Seq.empty,
  // hits = 19, isOptional = true, sample = ["incident_neo_confucianism","incident_wokou","incident_ikko_shu","incident_spread_of_christianity","incident_nanban","incident_firearms"]
  pastIncidents: Seq[String] = Seq.empty,
  // hits = 16, isOptional = true, sample = 3.031
  monthlyWarExhaustion: Option[BigDecimal] = None,
  // hits = 16, isOptional = true, sample = 6
  numOfCardinals: Option[Int] = None,
  // hits = 16, isOptional = true, sample = 1
  numOfRebelArmies: Option[Int] = None,
  // hits = 16, isOptional = true, sample = 22
  rebelThreat: Option[Int] = None,
  // hits = 16, isOptional = true, sample = [1983]
  rebelsInCountry: Seq[Int] = Seq.empty,
  // hits = 15, isOptional = true, sample = 0.011
  blockadedPercent: Option[BigDecimal] = None,
  // hits = 15, isOptional = true, sample = 3
  numOfOverseas: Option[Int] = None,
  // hits = 15, isOptional = true, sample = 1
  numOfRebelControlledProvinces: Option[Int] = None,
  // hits = 15, isOptional = true, sample = 0.250
  tariff: Option[BigDecimal] = None,
  // hits = 14, isOptional = true, sample = 36.300
  doom: Option[BigDecimal] = None,
  //  // hits = 14, isOptional = true, sample = "1572.8.7"
  //  lastThreatened: Option[Date] = None,
  //  // hits = 14, isOptional = true, sample = 4
  //  numOfMercenaries: Option[Int] = None,
  //  // hits = 14, isOptional = true, sample = "PRO"
  //  preferredEmperor: Option[String] = None,
  //  // hits = 14, isOptional = true, sample = ["BRI","C04","C12","C15","C20"]
  //  transferTradePowerFrom: Seq[String] = Seq.empty,
  //  // hits = 13, isOptional = true, sample = 58.859
  //  authority: Option[BigDecimal] = None,
  //  // hits = 13, isOptional = true, sample = [4378]
  //  mothballedForts: Seq[Int] = Seq.empty,
  //  // hits = 12, isOptional = true, sample = {"iqta_mechanic":"1750.7.15"}
  //  cooldowns: Option[JsonNode] = None,
  //  // hits = 12, isOptional = true, sample = true
  //  hasPrivateers: Boolean = false,
  //  // hits = 12, isOptional = true, sample = 100.000
  //  hordeUnity: Option[BigDecimal] = None,
  //  // hits = 12, isOptional = true, sample = 31
  //  numShipsPrivateering: Option[Int] = None,
  //  // hits = 12, isOptional = true, sample = 1
  //  tributeType: Option[Int] = None,
  //  // hits = 11, isOptional = true, sample = ["encourage_warriors_of_the_faith","legitimize_government"]
  //  blessing: Seq[String] = Seq.empty,
  //  // hits = 11, isOptional = true, sample = true
  //  convert: Boolean = false,
  //  // hits = 11, isOptional = true, sample = 0.100
  //  patriarchAuthority: Option[BigDecimal] = None,
  //  // hits = 10, isOptional = true, sample = "1534.11.2"
  //  lastSacrifice: Option[Date] = None,
  //  // hits = 10, isOptional = true, sample = "orthodox"
  //  secondaryReligion: Option[String] = None,
  //  // hits = 9, isOptional = true, sample = [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0]
  //  disasterStarted: Seq[Int] = Seq.empty,
  //  // hits = 9, isOptional = true, sample = true
  //  hasCircumnavigatedWorld: Boolean = false,
  //  // hits = 9, isOptional = true, sample = "BHA"
  //  influencedBy: Option[String] = None,
  //  // hits = 9, isOptional = true, sample = true
  //  isGreatPower: Boolean = false,
  //  // hits = 9, isOptional = true, sample = 1
  //  numOfNonRivalTradeEmbargos: Option[Int] = None,
  //  // hits = 9, isOptional = true, sample = {"6":4}
  //  numOfSubjectCountIndexed: Option[JsonNode] = None,
  //  // hits = 9, isOptional = true, sample = 4
  //  numOfSubjects: Option[Int] = None,
  //  // hits = 9, isOptional = true, sample = 1
  //  numOfWarReparations: Option[Int] = None,
  //  // hits = 9, isOptional = true, sample = ["C04","C12","C15","C20"]
  //  subjects: Seq[String] = Seq.empty,
  //  // hits = 9, isOptional = true, sample = 0.046
  //  treasureFleetGold: Option[BigDecimal] = None,
  //  // hits = 8, isOptional = true, sample = 1.447
  //  lastMonthTreasureFleetGold: Option[BigDecimal] = None,
  //  // hits = 8, isOptional = true, sample = 5
  //  numOfNonCores: Option[Int] = None,
  //  // hits = 8, isOptional = true, sample = 0.285
  //  overextensionPercentage: Option[BigDecimal] = None,
  //  // hits = 8, isOptional = true, sample = true
  //  wantsToBeGreatPower: Boolean = false,
  //  // hits = 8, isOptional = true, sample = true
  //  wantsToBeGreatPowerNext: Boolean = false,
  //  // hits = 7, isOptional = true, sample = ["POR"]
  //  guarantees: Seq[String] = Seq.empty,
  //  // hits = 7, isOptional = true, sample = true
  //  isElector: Boolean = false,
  //  // hits = 7, isOptional = true, sample = "1647.12.12"
  //  lastConversionSecondary: Option[Date] = None,
  //  // hits = 7, isOptional = true, sample = "1.1.1"
  //  lastHreVote: Option[Date] = None,
  //  // hits = 6, isOptional = true, sample = {"native_adm_ideas":[1,1,0,0,0],"native_dip_ideas":[1,1,1,1,0],"native_mil_ideas":[1,1,1,1,0]}
  //  activeNativeAdvancement: Option[JsonNode] = None,
  //  // hits = 6, isOptional = true, sample = 8
  //  debaseRechargeNeed: Option[Int] = None,
  //  // hits = 6, isOptional = true, sample = {"event":"republic_factions.6","days":18,"scope":{"country":"VEN","scope_is_valid":true,"seed":779439810,"random":779439810,"from":{"country":"VEN","scope_is_valid":true,"seed":1716735987,"random":1716735987}}}
  //  delayedEvent: Option[JsonNode] = None,
  //  // hits = 6, isOptional = true, sample = "1502.4.1"
  //  goldenEraDate: Option[Date] = None,
  //  // hits = 6, isOptional = true, sample = [false,true,false,false,false,false,false]
  //  subjectInteractions: Seq[Boolean] = Seq.empty,
  //  // hits = 5, isOptional = true, sample = ["incident_wokou"]
  //  activeIncidents: Seq[String] = Seq.empty,
  //  // hits = 5, isOptional = true, sample = true
  //  atWarWithOtherReligiousGroup: Boolean = false,
  //  // hits = 5, isOptional = true, sample = [0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,41.000,0.000,0.000,0.000,0.000,0.000,0.000]
  //  disasterProgress: Seq[BigDecimal] = Seq.empty,
  //  // hits = 4, isOptional = true, sample = "1740.5.18"
  //  aiCondottieriMalusUntil: Option[Date] = None,
  //  // hits = 4, isOptional = true, sample = 2
  //  cachedColonies: Option[Int] = None,
  //  // hits = 4, isOptional = true, sample = 2.200
  //  libertyDesire: Option[BigDecimal] = None,
  //  // hits = 4, isOptional = true, sample = 2
  //  numOfColonies: Option[Int] = None,
  //  // hits = 4, isOptional = true, sample = 1
  //  numOfRevolts: Option[Int] = None,
  //  // hits = 3, isOptional = true, sample = "french_revolution"
  //  activeDisaster: Option[String] = None,
  //  // hits = 3, isOptional = true, sample = "BHA"
  //  clientParent: Option[String] = None,
  //  // hits = 3, isOptional = true, sample = [{"level":2,"index":40,"name":"","desc":""},{"level":1,"index":150,"name":"","desc":""},{"level":2,"index":64,"name":"","desc":""},{"level":1,"index":128,"name":"","desc":""},{"level":3,"index":6,"name":"","desc":""},{"level":1,"index":21,"name":"","desc":""},{"level":2,"index":102,"name":"","desc":""},{"level":2,"index":141,"name":"","desc":""},{"level":3,"index":19,"name":"","desc":""},{"level":2,"index":8,"name":"","desc":""}]
  //  customNationalIdeas: Seq[JsonNode] = Seq.empty,
  //  // hits = 3, isOptional = true, sample = 4
  //  customNationalIdeasLevel: Option[Int] = None,
  //  // hits = 3, isOptional = true, sample = "1714.12.14"
  //  forcedBreakAllianceDate: Option[Date] = None,
  //  // hits = 3, isOptional = true, sample = "RUS"
  //  preferredCoalitionAgainstUs: Option[String] = None,
  //  // hits = 3, isOptional = true, sample = [12,13,20]
  //  tradedBonus: Seq[Int] = Seq.empty,
  //  // hits = 3, isOptional = true, sample = ["FRA"]
  //  warnings: Seq[String] = Seq.empty,
  //  // hits = 2, isOptional = true, sample = 2
  //  colonialCore: Option[Int] = None,
  //  // hits = 2, isOptional = true, sample = [5,2]
  //  harmonizedReligionGroups: Seq[Int] = Seq.empty,
  //  // hits = 2, isOptional = true, sample = [12]
  //  harmonizedReligions: Seq[Int] = Seq.empty,
  //  // hits = 2, isOptional = true, sample = 825.000
  //  preferredCoalitionScore: Option[BigDecimal] = None,
  //  // hits = 2, isOptional = true, sample = "SCA"
  //  preferredCoalitionTarget: Option[String] = None,
  //  // hits = 1, isOptional = true, sample = false
  //  automaticMaintenance: Boolean = true,
  //  // hits = 1, isOptional = true, sample = true
  //  excommunicated: Boolean = false,
  //  // hits = 1, isOptional = true, sample = true
  //  human: Boolean = false,
  //  // hits = 1, isOptional = true, sample = ["acceptance_of_religious_sects","abolish_state_firearm_regiments","anti_muslim_edict","abolish_standardized_uniforms","enlist_privateers"]
  //  ignoreDecision: Seq[String] = Seq.empty,
  //  // hits = 1, isOptional = true, sample = [2,3,528,534,538,540,561,562,574,581,593,615,616]
  //  interestingCountries: Seq[Int] = Seq.empty,
  //  // hits = 1, isOptional = true, sample = 4
  //  numOfJanissaries: Option[Int] = None,
  //  // hits = 1, isOptional = true, sample = 1.457
  //  tribalAllegiance: Option[BigDecimal] = None,
  //  // hits = 1, isOptional = true, sample = [2390,2391,593,2394,594,595,599,2393,598,2392,596,597,2686,2678,622,623,621,2677,2680,2681,2676,620,2682,2675,618,619,2685,2674,2673,617,659]
  //  vitalProvinces: Seq[Int] = Seq.empty,
  //  // hits = 1, isOptional = true, sample = true
  //  wasPlayer: Boolean = false,
)

object TagSave extends FromJson[TagSave]
