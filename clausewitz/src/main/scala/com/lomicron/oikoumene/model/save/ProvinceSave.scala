package com.lomicron.oikoumene.model.save

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.databind.JsonNode
import com.lomicron.oikoumene.model.Entity
import com.lomicron.utils.json.FromJson
import com.lomicron.utils.parsing.tokenizer.Date

@JsonCreator
case class ProvinceSave
(
  // hits = 4650, isOptional = false, sample = 1
  id: Int = 0,
  // hits = 4650, isOptional = false, sample = ["REB","SWE","DAN","NOR","SHL","SCA","ALB","ATH","BOS","BYZ","CEP","CYP","KNI","MOL","NAX","RAG","SER","WAL","HUN","TUR","CNN","ENG","LEI","MNS","SCO","TYR","GBR","ULS","DMS","SLN","KID","HSC","ORD","TRY","FLY","MCM","LOI","DNZ","KRA","LIT","LIV","MAZ","POL","RIG","TEU","VOL","KIE","CHR","OKA","ALS","BRI","BUR","FRA","NEV","PRO","AAC","ANH","ANS","AUG","BAD","BAV","BOH","BRA","BRE","BRU","EFR","FRN","HAB","HAM","HES","KLE","KOL","LAU","LOR","LUN","MAG","MAI","MKL","MUN","OLD","PAL","POM","SAX","SIL","SLZ","SWI","TRI","ULM","WBG","WUR","NUM","MEM","VER","NSA","RVA","DTT","ARA","CAS","GRA","NAV","POR","SPA","FER","GEN","MAN","MLO","NAP","PAP","SAV","SIE","TUS","URB","VEN","MFA","LUC","LAN","BRB","FLA","FRI","GEL","HOL","LIE","UTR","ARM","AST","CRI","GEO","KAZ","MOS","NOV","PSK","RUS","RYA","TVE","YAR","NOG","SIB","PRM","FEO","BLO","RSO","GOL","ADE","ALH","ANZ","ARD","BHT","DAW","FAD","HDR","HED","MDA","MFL","MHR","NAJ","NJR","OMA","RAS","SHM","SRV","YAS","YEM","HSN","BTL","AKK","CND","DUL","KAR","TRE","RAM","AVR","MLK","SME","ARL","MSY","MAM","MOR","TUN","TFL","SOS","TLC","TGT","GHD","FZA","MZB","MRK","KZH","KHI","SHY","KOK","AFG","KHO","QAR","TIM","TRS","KRY","CIR","GAZ","IME","TAB","ORM","LRI","SIS","BPI","FRS","KRM","ISF","QOM","HUR","SHA","ZAP","BEN","ETH","KON","MAL","NUB","SON","ZAN","ZIM","ADA","KBO","OYO","JOL","SFA","MLI","AJU","MDI","MED","WAR","WLY","ABB","SYO","KSJ","KIK","YAK","KUB","BUU","NKO","KRW","BSG","TBK","ANT","ANN","ATJ","AYU","BLI","BEI","CHA","CHG","DAI","JAP","ANU","KOR","LNA","LXA","MKS","MLC","MNG","OIR","PAT","PEG","RYU","SUK","SUL","MYR","MJZ","KAS","CHH","UTS","GUG","PHA","BAL","BNG","DLH","VIJ","GUJ","JNP","MLW","MER","NPL","ORI","SND","KGR","LDK","CHD","NGP","SDY","BHA","DGL","SKK","PAN","RTT","DTI","GRK","HSA","APA","BLA","CAD","CHO","CHY","COM","ILL","MMI","OJI","PAW","POT","PUE","SHO","SIO","SUS","WCR","AIR","BON","DAH","DGB","FUL","JNN","KNG","KTS","MSI","TMB","YAT","ZAF","ZZZ","NDO","AVA","KED","LIG","MKA","KAL","HSI","BPR","HOD","CHV","KMC","BRT","ARP","CNK","HDA","SAL","TOT","WIC","XIU","BTN","PGR","PLB","PSA","SAK","SUN","LUW","TID","TDO","CEB","BTU","MPC","GUA","CUA","HST","CCM","KER","MSC","LIP","CHT","YAQ","C00","C01","C02","C03","C04","C05","C06","C07","C08","C09","C10","C11","C12","C13","C14","C15","C16","C17","C18","C19","C20","K00","K01","K02","E00","T00","T01"]
  discoveredBy: Seq[String] = Seq.empty,
  // hits = 4650, isOptional = false, sample = [100.000,100.000,100.000,100.000,100.000,100.000,100.000]
  institutions: Seq[BigDecimal] = Seq.empty,
  // hits = 4650, isOptional = false, sample = "Stokholm"
  name: String = Entity.UNDEFINED,
  // hits = 4650, isOptional = false, sample = 0
  patrol: Int = 0,
  // hits = 4650, isOptional = false, sample = "grain"
  tradeGoods: String = Entity.UNDEFINED,
  // hits = 3634, isOptional = true, sample = "particularist_rebels"
  likelyRebels: Option[String] = None,
  // hits = 3633, isOptional = true, sample = false
  ub: Boolean = true,
  // hits = 3632, isOptional = true, sample = {"add_core":"SWE","owner":"SWE","controller":{"tag":"SWE"},"culture":"swedish","religion":"catholic","hre":false,"base_tax":5.000,"base_production":5.000,"trade_goods":"grain","base_manpower":3.000,"capital":"Stockholm","is_city":true,"discovered_by":["eastern","western","muslim","ottoman"],"extra_cost":16.000,"1436.4.28":{"revolt":{"type":"pretender_rebels","leader":"Karl Knutsson Bonde","size":1},"controller":{"tag":"REB"}},"1438.3.6":{"controller":{"tag":"SWE"}},"1438.10.1":{"revolt":{"type":"pretender_rebels","leader":"Karl Knutsson Bonde","size":1},"controller":{"tag":"REB"}},"1440.9.1":{"controller":{"tag":"SWE"}},"1444.11.12":{"owner":"SWE"},"1468.3.14":{"discovered_by":"REB"},"1491.2.9":{"marketplace":true},"1509.12.15":{"temple":true},"1514.8.3":{"shipyard":true},"1538.6.6":{"dock":true},"1542.3.1":[{"add_core":"DAN"},{"controller":{"tag":"DAN"}},{"owner":"DAN"},{"fake_owner":"SCA"},{"add_core":"SCA"}],"1568.4.5":{"regimental_camp":true},"1625.9.2":{"controller":{"tag":"REB","rebel":"Danish Noble Rebels"}},"1626.6.12":{"controller":{"tag":"DAN"}},"1626.11.1":{"unrest":0.000},"1634.4.10":{"controller":{"tag":"REB","rebel":"Danish Noble Rebels"}},"1636.4.20":[{"controller":{"tag":"DAN"}},{"controller":{"tag":"SCA"}}],"1648.11.6":{"remove_core":"DAN"},"1744.2.1":{"advisor":{"name":"Christian Rosenørn","type":"treasurer","skill":1,"location":1,"culture":"swedish","religion":"catholic","date":"1743.2.1","id":{"id":68665,"type":51}}}}
  history: Option[JsonNode] = None,
  // hits = 3623, isOptional = true, sample = 20.148
  tradePower: Option[BigDecimal] = None,
  // hits = 3622, isOptional = true, sample = {"1":"1.1.1","2":"1.1.1","3":"1.1.1","4":"1.1.1","5":"1544.11.11","6":"1544.11.11","7":"1544.11.11","8":"1544.11.11","9":"1544.11.11","10":"1544.11.11","11":"1574.7.5","12":"1694.11.11","13":"1544.11.11"}
  discoveryDates2: Option[JsonNode] = None,
  // hits = 3622, isOptional = true, sample = {"1":"1444.11.11","4":"1561.8.2","5":"1444.11.11","6":"1444.11.11","7":"1444.11.11","8":"1444.11.11","9":"1444.11.11","10":"1594.11.11","11":"1588.10.24","12":"1594.11.11","13":"1594.11.11","14":"1594.11.11","15":"1560.2.4","17":"1594.11.11","18":"1548.6.1","19":"1574.7.5","21":"1744.11.11","22":"1744.11.11","24":"1494.1.1"}
  discoveryReligionDates2: Option[JsonNode] = None,
  // hits = 3095, isOptional = true, sample = "baltic_sea"
  trade: Option[String] = None,
  // hits = 3000, isOptional = true, sample = 11.000
  baseManpower: Option[BigDecimal] = None,
  // hits = 3000, isOptional = true, sample = 5.000
  baseProduction: Option[BigDecimal] = None,
  // hits = 3000, isOptional = true, sample = 5.000
  baseTax: Option[BigDecimal] = None,
  // hits = 3000, isOptional = true, sample = "swedish"
  culture: Option[String] = None,
  // hits = 3000, isOptional = true, sample = "swedish"
  originalCulture: Option[String] = None,
  // hits = 3000, isOptional = true, sample = 13.000
  originalTax: Option[BigDecimal] = None,
  // hits = 3000, isOptional = true, sample = "catholic"
  religion: Option[String] = None,
  // hits = 2997, isOptional = true, sample = "Stockholm"
  capital: Option[String] = None,
  // hits = 2989, isOptional = true, sample = "catholic"
  originalReligion: Option[String] = None,
  // hits = 2962, isOptional = true, sample = "SCA"
  controller: Option[String] = None,
  // hits = 2962, isOptional = true, sample = "SCA"
  owner: Option[String] = None,
  // hits = 2962, isOptional = true, sample = {"id":45760,"type":50}
  rebelFaction: Option[JsonNode] = None,
  // hits = 2956, isOptional = true, sample = ["SWE","SCA"]
  cores: Seq[String] = Seq.empty,
  // hits = 2956, isOptional = true, sample = true
  isCity: Boolean = false,
  // hits = 2768, isOptional = true, sample = "DAN"
  previousController: Option[String] = None,
  // hits = 2256, isOptional = true, sample = "1498.9.1"
  lastLooted: Option[Date] = None,
  // hits = 2004, isOptional = true, sample = {"marketplace":"SWE","temple":"SWE","shipyard":"DAN","dock":"SWE","regimental_camp":"DAN"}
  buildingBuilders: Option[JsonNode] = None,
  // hits = 2004, isOptional = true, sample = {"marketplace":true,"temple":true,"shipyard":true,"dock":true,"regimental_camp":true}
  buildings: Option[JsonNode] = None,
  // hits = 1177, isOptional = true, sample = 3
  fortInfluencing: Option[Int] = None,
  // hits = 1113, isOptional = true, sample = "1445.11.11"
  lastEstateGrant: Option[Date] = None,
  // hits = 1082, isOptional = true, sample = 8
  improveCount: Option[Int] = None,
  // hits = 961, isOptional = true, sample = [{"modifier":"stora_kopparberget_modifier","date":"-1.1.1","permanent":true},{"modifier":"rotten_borough","date":"-1.1.1"}]
  modifier: Seq[JsonNode] = Seq.empty,
  // hits = 942, isOptional = true, sample = 2
  nativeFerocity: Option[Int] = None,
  // hits = 932, isOptional = true, sample = 1
  nativeHostileness: Option[Int] = None,
  // hits = 776, isOptional = true, sample = 93.329
  colonysize: Option[BigDecimal] = None,
  // hits = 732, isOptional = true, sample = {"province_had_influenza":"1701.3.25"}
  flags: Option[JsonNode] = None,
  // hits = 728, isOptional = true, sample = "SHY"
  originalColoniser: Option[String] = None,
  // hits = 721, isOptional = true, sample = 0.500
  formerNativeSize: Option[BigDecimal] = None,
  // hits = 595, isOptional = true, sample = 2000.000
  garrison: Option[BigDecimal] = None,
  // hits = 578, isOptional = true, sample = 100.042
  missionaryProgress: Option[BigDecimal] = None,
  // hits = 494, isOptional = true, sample = 28.391
  devastation: Option[BigDecimal] = None,
  // hits = 465, isOptional = true, sample = {"id":110713,"type":54}
  unit: Seq[JsonNode] = Seq.empty,
  // hits = 391, isOptional = true, sample = ["RUS"]
  claims: Seq[String] = Seq.empty,
  // hits = 338, isOptional = true, sample = 45.825
  localAutonomy: Option[BigDecimal] = None,
  // hits = 329, isOptional = true, sample = 1
  centerOfTrade: Option[Int] = None,
  // hits = 257, isOptional = true, sample = 25.000
  nativeSizeBeforeMigration: Option[BigDecimal] = None,
  // hits = 246, isOptional = true, sample = 5
  estate: Option[Int] = None,
  // hits = 227, isOptional = true, sample = "TUR"
  territorialCore: Option[String] = None,
  // hits = 175, isOptional = true, sample = "1722.9.1"
  lastNativeUprising: Option[Date] = None,
  // hits = 168, isOptional = true, sample = true
  activeTradeCompany: Boolean = false,
  // hits = 167, isOptional = true, sample = true
  hre: Boolean = false,
  // hits = 164, isOptional = true, sample = {"bribe":"pay_legitimacy"}
  seatInParliament: Option[JsonNode] = None,
  // hits = 158, isOptional = true, sample = 10
  nationalism: Option[Int] = None,
  // hits = 124, isOptional = true, sample = [{"start_date":"1749.7.9","total":56,"original_total":56,"progress":0.785,"date":"1749.9.3","envoy":-1,"country":"LUN","cost":10.000,"type":"prussian_frederickian"},{"start_date":"1749.8.23","total":56,"original_total":56,"date":"1749.10.18","envoy":-1,"country":"LUN","cost":10.000,"type":"prussian_frederickian"},{"start_date":"1749.8.23","total":84,"original_total":84,"date":"1749.11.15","envoy":-1,"country":"LUN","cost":25.000,"type":"prussian_uhlan"}]
  militaryConstruction: Seq[JsonNode] = Seq.empty,
  // hits = 118, isOptional = true, sample = 0.225
  lootRemaining: Option[BigDecimal] = None,
  // hits = 112, isOptional = true, sample = 0.250
  hostileCoreCreationCost: Option[BigDecimal] = None,
  // hits = 112, isOptional = true, sample = "Between a Kingdom and the Empire: §G+25.0%§!\\n"
  hostileCoreCreationDesc: Option[String] = None,
  // hits = 112, isOptional = true, sample = "SHL"
  hostileCoreCreationTag: Option[String] = None,
  // hits = 90, isOptional = true, sample = [{"start_date":"1746.10.10","total":23,"original_total":23,"date":"1746.11.2","envoy":2,"country":"E00","actor":"E00","recipient":"SCA","action":"Improve Relation","from_province":1778,"to_province":12,"once":false,"action_token":"improve_relation"},{"start_date":"1749.1.7","total":35,"original_total":35,"date":"1749.2.11","envoy":1,"country":"KNG","actor":"KNG","recipient":"SCA","action":"Improve Relation","from_province":1119,"to_province":12,"once":false,"action_token":"improve_relation"},{"start_date":"1749.1.17","total":5,"original_total":5,"date":"1749.1.22","envoy":3,"country":"DNZ","actor":"DNZ","recipient":"SCA","action":"Improve Relation","from_province":43,"to_province":12,"once":false,"action_token":"improve_relation"},{"start_date":"1749.4.26","total":3,"original_total":3,"date":"1749.4.29","envoy":3,"country":"KLE","actor":"KLE","recipient":"SCA","action":"Improve Relation","from_province":55,"to_province":12,"once":false,"action_token":"improve_relation"}]
  diplomacyConstruction: Seq[JsonNode] = Seq.empty,
  // hits = 84, isOptional = true, sample = true
  hreLiberated: Boolean = false,
  // hits = 77, isOptional = true, sample = [{"start_date":"1444.11.29","total":7,"original_total":-527027,"progress":1.000,"date":"1444.12.6","envoy":0,"country":"RUS","node":33,"from":295,"to":33,"direction":1,"type":0},{"start_date":"1554.2.2","total":13,"original_total":-566877,"progress":1.000,"date":"1554.2.15","envoy":0,"country":"DNZ","node":33,"from":48,"to":33,"direction":1,"type":1},{"start_date":"1610.6.19","total":15,"original_total":-587454,"progress":1.000,"date":"1610.7.4","envoy":3,"country":"SCA","node":33,"from":12,"to":33,"direction":1,"type":1},{"start_date":"1648.1.10","total":20,"original_total":-601164,"progress":1.000,"date":"1648.1.30","envoy":0,"country":"PRM","node":33,"from":1963,"to":33,"direction":1,"type":1},{"start_date":"1683.7.2","total":16,"original_total":-614112,"progress":1.000,"date":"1683.7.18","envoy":1,"country":"SHL","node":33,"from":1775,"to":33,"direction":1,"type":1},{"start_date":"1695.5.11","total":10,"original_total":-618440,"progress":1.000,"date":"1695.5.21","envoy":2,"country":"E00","node":33,"from":1778,"to":33,"direction":1,"type":0}]
  merchantConstruction: Seq[JsonNode] = Seq.empty,
  // hits = 74, isOptional = true, sample = {"spy_network":[{"location":43,"attacker":"BRA","envoy":1},{"location":43,"attacker":"SHL","envoy":3}]}
  spyActions: Option[JsonNode] = None,
  // hits = 70, isOptional = true, sample = {"start_date":"1747.7.3","total":914,"original_total":914,"progress":0.853,"date":"1750.1.3","envoy":-1,"country":"SHL","cost":170.000,"building":22,"builder":9}
  buildingConstruction: Option[JsonNode] = None,
  // hits = 48, isOptional = true, sample = {"id":67896,"type":50}
  occupyingRebelFaction: Option[JsonNode] = None,
  // hits = 48, isOptional = true, sample = {"start_date":"1701.5.13","total":1,"original_total":-620631,"progress":17621.000,"date":"1701.5.14","envoy":0,"country":"SCA"}
  settlementGrowthConstruction: Option[JsonNode] = None,
  // hits = 44, isOptional = true, sample = 5.000
  nativeSize: Option[BigDecimal] = None,
  // hits = 32, isOptional = true, sample = "1507.1.28"
  lastRazed: Option[Date] = None,
  // hits = 32, isOptional = true, sample = "CRI"
  lastRazedBy: Option[String] = None,
  // hits = 30, isOptional = true, sample = 1
  previousWinter: Option[Int] = None,
  // hits = 24, isOptional = true, sample = 1
  winter: Option[Int] = None,
  // hits = 23, isOptional = true, sample = {"start_date":"1747.5.19","total":1095,"original_total":1095,"progress":0.753,"date":"1750.5.19","power":24.000,"envoy":-1,"country":"C04"}
  buildCoreConstruction: Option[JsonNode] = None,
  // hits = 19, isOptional = true, sample = true
  mothballCommand: Boolean = false,
  // hits = 17, isOptional = true, sample = ["religious_center","non_catholic_rome"]
  triggeredModifier: Seq[String] = Seq.empty,
  // hits = 14, isOptional = true, sample = 4.000
  siege: Option[BigDecimal] = None,
  // hits = 13, isOptional = true, sample = true
  blockade: Boolean = false,
  // hits = 13, isOptional = true, sample = 1.000
  blockadeEfficiency: Option[BigDecimal] = None,
  // hits = 9, isOptional = true, sample = "religious_center"
  appliedTriggeredModifier: Seq[String] = Seq.empty,
  // hits = 9, isOptional = true, sample = 0.182
  unrest: Option[BigDecimal] = None,
  // hits = 8, isOptional = true, sample = {"owned_by":"SCA","start_date":"1748.11.1","total":365000,"original_total":365000,"date":"2748.11.1","envoy":0,"country":"SCA"}
  missionaryConstruction: Option[JsonNode] = None,
  // hits = 6, isOptional = true, sample = {"start_date":"1748.10.11","total":49,"original_total":-637889,"progress":6.428,"date":"1748.11.29","envoy":0,"country":"SHY"}
  colonyConstruction: Option[JsonNode] = None,
  // hits = 1, isOptional = true, sample = {"event":"parlaments.99","days":312,"scope":{"province":2294,"scope_is_valid":true,"seed":579742863,"random":579742863,"from":{"country":"GBR","scope_is_valid":true,"seed":1707249620,"random":1707249620}}}
  delayedEvent: Option[JsonNode] = None,
  // hits = 1, isOptional = true, sample = 18.000
  fortFlipProgress: Option[BigDecimal] = None,
  // hits = 1, isOptional = true, sample = 180
  fortFlipperProv: Option[Int] = None,
  // hits = 1, isOptional = true, sample = true
  userChangedName: Boolean = false,
)

object ProvinceSave extends FromJson[ProvinceSave]
