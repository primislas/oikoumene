package com.lomicron.oikoumene.model.politics

import com.fasterxml.jackson.annotation.{JsonCreator, JsonProperty}
import com.lomicron.oikoumene.model.events.TagCondition
import com.lomicron.oikoumene.model.history.HistEvent
import com.lomicron.oikoumene.model.modifiers.ActiveModifier
import com.lomicron.utils.json.FromJson
import com.lomicron.utils.parsing.tokenizer.Date

@JsonCreator
case class TagUpdate
(
  // hits = 13270, isOptional = true, sample = {"year":1730,"month":1,"day":1}
  override val date: Option[Date] = None,

  @JsonProperty("if") condition: Option[TagCondition] = None,

  // hits = 796, isOptional = true, sample = "east_african"
  technologyGroup: Option[String] = None,
  // hits = 938, isOptional = true, sample = 2776
  capital: Option[Int] = None,
  // hits = 132, isOptional = true, sample = 2977
  fixedCapital: Option[Int] = None,
  // hits = 926, isOptional = true, sample = "monarchy"
  government: Option[String] = None,
  // hits = 525, isOptional = true, sample = 1
  governmentRank: Option[Int] = None,
  // hits = 74, isOptional = true, sample = "administrative_monarchy"
  setLegacyGovernment: Option[String] = None,
  // hits = 31, isOptional = true, sample = "eng_had_end_of_hundred_years_war"
  setGlobalFlag: Option[Seq[String]] = None,
  // hits = 16, isOptional = true, sample = ["total_war_series","total_war"]
  setCountryFlag: Option[Seq[String]] = None,
  // hits = 2, isOptional = true, sample = ["religious_turmoil","religious_intolerance"]
  clrCountryFlag: Option[Seq[String]] = None,
  // hits = 4, isOptional = true, sample = "CAS"
  changedTagFrom: Option[String] = None,


  // hits = 8023, isOptional = true, sample = {"name":"Yusuf","dynasty":"Jaladi","dip":3,"mil":1,"adm":2}
  monarch: Option[Monarch] = None,
  // hits = 3723, isOptional = true, sample = {"name":"Ali Shah","monarch_name":"Ali Shah I","dynasty":"Shah Miri","birth_date":"1389.1.1","death_date":"1420.6.1","claim":95,"ADM":1,"DIP":3,"MIL":2}
  heir: Option[Heir] = None,
  // hits = 274, isOptional = true, sample = {"name":"Tenagne","country_of_origin":"ETH","dynasty":"Solomonid","birth_date":"1420.1.1","death_date":"1460.1.1","adm":3,"dip":2,"mil":2}
  queen: Option[Queen] = None,
  // hits = 845, isOptional = true, sample = {"name":"Zheng He","type":"explorer","fire":1,"shock":1,"manuever":6,"siege":0,"death_date":"1433.1.1"}
  leader: Option[Seq[Leader]] = None,
  // hits = 1, isOptional = true, sample = {"name":"shahrukhs_empire"}
  addRulerModifier: Option[Seq[RulerModifier]] = None,
  // hits = 291, isOptional = true, sample = ["scholar_personality","just_personality"]
  addRulerPersonality: Option[Seq[String]] = None,
  // hits = 38, isOptional = true, sample = "well_advised_personality"
  addHeirPersonality: Option[Seq[String]] = None,
  // hits = 19, isOptional = true, sample = "charismatic_negotiator_personality"
  addQueenPersonality: Option[String] = None,
  // hits = 310, isOptional = true, sample = true
  clearScriptedPersonalities: Option[Boolean] = None,

  // hits = 842, isOptional = true, sample = "iqta"
  addGovernmentReform: Option[Seq[String]] = None,
  // hits = 40, isOptional = true, sample = "abolish_slavery_act"
  decision: Option[Seq[String]] = None,
  // hits = 26, isOptional = true, sample = 25
  mercantilism: Option[BigDecimal] = None,
  // hits = 11, isOptional = true, sample = "MIL"
  nationalFocus: Option[String] = None,
  // hits = 21, isOptional = true, sample = {"trade_goods":"gems","key":"FACETING","value":0.25,"duration":-1}
  changePrice: Option[Seq[PriceModifier]] = None,
  // hits = 1, isOptional = true, sample = {"name":"tur_janissary","duration":-1}
  addCountryModifier: Option[Seq[ActiveModifier]] = None,

  // hits = 28, isOptional = true, sample = "COC"
  historicalRival: Option[Seq[String]] = None,
  // hits = 21, isOptional = true, sample = "VEN"
  historicalFriend: Option[Seq[String]] = None,
  // hits = 28, isOptional = true, sample = true
  elector: Option[Boolean] = None,
  // hits = 1, isOptional = true, sample = true
  enableHreLeagues: Option[Boolean] = None,
  // hits = 31, isOptional = true, sample = "protestant"
  joinLeague: Option[String] = None,
  // hits = 27, isOptional = true, sample = "protestant"
  leaveLeague: Option[String] = None,
  // hits = 1, isOptional = true, sample = true
  setHreReligionTreaty: Option[Boolean] = None,
  // hits = 1, isOptional = true, sample = true
  revolutionTarget: Option[Boolean] = None,

  // hits = 799, isOptional = true, sample = "somali"
  primaryCulture: Option[String] = None,
  // hits = 74, isOptional = true, sample = ["marathi","telegu"]
  addAcceptedCulture: Option[Seq[String]] = None,
  // hits = 6, isOptional = true, sample = ["gascon","normand"]
  removeAcceptedCulture: Option[Seq[String]] = None,
  // hits = 893, isOptional = true, sample = "sunni"
  religion: Option[String] = None,
  // hits = 7, isOptional = true, sample = "confucianism"
  secondaryReligion: Option[String] = None,
  // hits = 2, isOptional = true, sample = "mahayana"
  addHarmonizedReligion: Option[String] = None,
  // hits = 188, isOptional = true, sample = "shafii_school"
  religiousSchool: Option[Seq[String]] = None,
  // hits = 62, isOptional = true, sample = ["yemoja_cult","roog_cult","nyame_cult"]
  unlockCult: Option[Seq[String]] = None,
  // hits = 105, isOptional = true, sample = -0.3
  addPiety: Option[BigDecimal] = None,



  // hits = 82, isOptional = true, sample = "sub_saharan"
  unitType: Option[String] = None,
  // hits = 38, isOptional = true, sample = 0.05
  addArmyProfessionalism: Option[BigDecimal] = None,
  // hits = 1, isOptional = true, sample = "indian"
  changeUnitType: Option[String] = None,

  // hits = 1, isOptional = true, sample = "hagia_sophia"
  hideAmbientObject: Option[String] = None,
  // hits = 1, isOptional = true, sample = "hagia_sophia_minarets"
  showAmbientObject: Option[String] = None,
) extends HistEvent

object TagUpdate extends FromJson[TagUpdate] {
  val empty: TagUpdate = TagUpdate()
}
