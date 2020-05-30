package com.lomicron.oikoumene.model.politics

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.oikoumene.model.Entity
import com.lomicron.oikoumene.model.history.HistState
import com.lomicron.utils.json.FromJson

case class TagState
(
  // hits = 793, isOptional = false, sample = "east_african"
  technologyGroup: String = Entity.UNDEFINED,
  // hits = 790, isOptional = true, sample = 2776
  capital: Option[Int] = None,
  // hits = 132, isOptional = true, sample = 2977
  fixedCapital: Option[Int] = None,
  // hits = 790, isOptional = true, sample = "monarchy"
  government: Option[String] = None,
  // hits = 400, isOptional = true, sample = 1
  governmentRank: Option[Int] = None,
  // hits = 74, isOptional = true, sample = "administrative_monarchy"
  legacyGovernment: Option[String] = None,
  // hits = 31, isOptional = true, sample = "eng_had_end_of_hundred_years_war"
  globalFlags: Seq[String] = Seq.empty,
  // hits = 16, isOptional = true, sample = ["total_war_series","total_war"]
  countryFlags: Seq[String] = Seq.empty,

  // hits = 8023, isOptional = true, sample = {"name":"Yusuf","dynasty":"Jaladi","dip":3,"mil":1,"adm":2}
  monarch: Option[Monarch] = None,
  // hits = 3723, isOptional = true, sample = {"name":"Ali Shah","monarch_name":"Ali Shah I","dynasty":"Shah Miri","birth_date":"1389.1.1","death_date":"1420.6.1","claim":95,"ADM":1,"DIP":3,"MIL":2}
  heir: Option[Heir] = None,
  // hits = 274, isOptional = true, sample = {"name":"Tenagne","country_of_origin":"ETH","dynasty":"Solomonid","birth_date":"1420.1.1","death_date":"1460.1.1","adm":3,"dip":2,"mil":2}
  queen: Option[Queen] = None,
  // hits = 845, isOptional = true, sample = {"name":"Zheng He","type":"explorer","fire":1,"shock":1,"manuever":6,"siege":0,"death_date":"1433.1.1"}
  leaders: Seq[Leader] = Seq.empty,

  // hits = 716, isOptional = true, sample = "iqta"
  governmentReforms: Seq[String] = Seq.empty,
  // hits = 40, isOptional = true, sample = "abolish_slavery_act"
  decisions: Seq[String] = Seq.empty,
  // hits = 24, isOptional = true, sample = 25
  mercantilism: Option[Int] = None,
  // hits = 11, isOptional = true, sample = "MIL"
  nationalFocus: Option[String] = None,
  // hits = 28, isOptional = true, sample = true
  isHreElector: Boolean = false,
  enableHreLeagues: Boolean = false,
  // hits = 31, isOptional = true, sample = "protestant"
  hreLeague: Option[String] = None,
  hreReligionTreaty: Boolean = false,
  isRevolutionTarget: Boolean = false,

  // hits = 789, isOptional = true, sample = "somali"
  primaryCulture: Option[String] = None,
  // hits = 55, isOptional = true, sample = ["marathi","telegu"]
  acceptedCultures: Seq[String] = Seq.empty,
  // hits = 789, isOptional = true, sample = "sunni"
  religion: Option[String] = None,
  // hits = 161, isOptional = true, sample = "shafii_school"
  religiousSchools: Seq[String] = Seq.empty,
  // hits = 62, isOptional = true, sample = ["yemoja_cult","roog_cult","nyame_cult"]
  cults: Seq[String] = Seq.empty,
  // hits = 7, isOptional = true, sample = "confucianism"
  secondaryReligion: Option[String] = None,
  // hits = 2, isOptional = true, sample = "mahayana"
  harmonizedReligions: Seq[String] = Seq.empty,
  // hits = 1, isOptional = true, sample = -0.25
  piety: Option[BigDecimal] = None,

  // hits = 82, isOptional = true, sample = "sub_saharan"
  unitType: Option[String] = None,
  // hits = 25, isOptional = true, sample = 0.05
  armyProfessionalism: Option[BigDecimal] = None,

  // hits = 28, isOptional = true, sample = "COC"
  historicalRivals: Seq[String] = Seq.empty,
  // hits = 21, isOptional = true, sample = "VEN"
  historicalFriends: Seq[String] = Seq.empty,
  // hits = 4, isOptional = true, sample = "CAS"
  changedTagFrom: Option[String] = None,

  // hits = 21, isOptional = true, sample = {"trade_goods":"gems","key":"FACETING","value":0.25,"duration":-1}
  priceModifiers: Seq[PriceModifier] = Seq.empty,
  // hits = 1, isOptional = true, sample = {"name":"tur_janissary","duration":-1}
  countryModifiers: Seq[CountryModifier] = Seq.empty,
  // hits = 1, isOptional = true, sample = {"name":"shahrukhs_empire"}
  rulerModifiers: Seq[RulerModifier] = Seq.empty,

  ambientObjects: Seq[String] = Seq.empty,


) extends HistState[TagState, TagUpdate] { self =>

  @JsonCreator def this() = this(Entity.UNDEFINED)

  override def next(update: TagUpdate): TagState =
    TagState.updatedFieldsFrom(update).foldLeft(self)((acc, f) => f(acc))



  def updateTechnologyGroup(technologyGroup: String): TagState = copy(technologyGroup = technologyGroup)

  def updateCapital(capital: Int): TagState = copy(capital = Option(capital))

  def updateFixedCapital(fixedCapital: Int): TagState = copy(fixedCapital = Option(fixedCapital))

  def updateGovernment(government: String): TagState = copy(government = Option(government))

  def updateGovernmentRank(governmentRank: Int): TagState = copy(governmentRank = Some(governmentRank))

  def updateLegacyGovernment(legacyGovernment: String): TagState = copy(legacyGovernment = Option(legacyGovernment))

  def setGlobalFlag(globalFlag: String): TagState = copy(globalFlags = globalFlags :+ globalFlag)

  def setGlobalFlags(flags: Seq[String]): TagState = copy(globalFlags = globalFlags ++ flags)

  def setCountryFlag(flag: String): TagState = setCountryFlags(Seq(flag))

  def setCountryFlags(flags: Seq[String]): TagState = copy(countryFlags = countryFlags ++ flags)

  def clearCountryFlag(flag: String): TagState = copy(countryFlags = countryFlags.filter(_ != flag))

  def clearCountryFlags(flags: Seq[String]): TagState = copy(countryFlags = countryFlags.filter(!flags.contains(_)))



  def updateMonarch(monarch: Monarch): TagState = copy(monarch = Option(monarch))

  def updateHeir(heir: Heir): TagState = copy(heir = Option(heir))

  def updateQueen(queen: Queen): TagState = copy(queen = Option(queen))

  def addLeader(leader: Leader): TagState = copy(leaders = leaders :+ leader)

  def addLeaders(leaders: Seq[Leader]): TagState = copy(leaders = this.leaders ++ leaders)

  def removeLeader(leader: Leader): TagState = copy(leaders = leaders.filter(_ != leader))

  def addRulerPersonalities(personalities: Seq[String]): TagState = copy(monarch = monarch.map(m => m.copy(personalities =  m.personalities ++ personalities)))

  def addRulerPersonality(personality: String): TagState = copy(monarch = monarch.map(m => m.copy(personalities =  m.personalities :+ personality)))

  def addQueenPersonalities(personalities: Seq[String]): TagState = copy(queen = queen.map(q => q.copy(personalities = q.personalities ++ personalities)))

  def addQueenPersonality(personality: String): TagState = copy(queen = queen.map(q => q.copy(personalities = q.personalities :+ personality)))

  def addHeirPersonalities(personalities: Seq[String]): TagState = copy(heir = heir.map(h => h.copy(personalities = h.personalities ++ personalities)))

  def addHeirPersonality(personality: String): TagState = copy(heir = heir.map(h => h.copy(personalities = h.personalities :+ personality)))

  def clearPersonalities(): TagState = copy(
    monarch = monarch.map(_.clearPersonalities),
    queen = queen.map(_.clearPersonalities),
    heir = heir.map(_.clearPersonalities),
    leaders = leaders.map(_.clearPersonalities)
  )



  def addGovernmentReform(reform: String): TagState = copy(governmentReforms = governmentReforms :+ reform)

  def addDecision(decision: String): TagState = addDecisions(Seq(decision))

  def addDecisions(decisions: Seq[String]): TagState = copy(decisions = this.decisions ++ decisions)

  def setMercantilism(mercantilism: Int): TagState = copy(mercantilism = Option(mercantilism))

  def setNationalFocus(focus: String): TagState = copy(nationalFocus = Option(focus))

  def enableHreLeagues(isEnabled: Boolean): TagState = copy(enableHreLeagues = isEnabled)

  def isHreElector(isHreElector: Boolean): TagState = copy(isHreElector = isHreElector)

  def joinHreLeague(league: String): TagState = copy(hreLeague = Option(league))

  def leaveHreLeague(): TagState = copy(hreLeague = None)

  def hreReligionTreaty(treaty: Boolean): TagState = copy(hreReligionTreaty = treaty)

  def revolutionTarget(isTarget: Boolean): TagState = copy(isRevolutionTarget = isTarget)



  def setPrimaryCulture(culture: String): TagState = copy(primaryCulture = Option(culture))

  def addAcceptedCulture(culture: String): TagState = addAcceptedCultures(Seq(culture))

  def addAcceptedCultures(cultures: Seq[String]): TagState = copy(acceptedCultures = acceptedCultures ++ cultures)

  def removeAcceptedCulture(culture: String): TagState = copy(acceptedCultures = acceptedCultures.filter(_ != culture))

  def removeAcceptedCultures(cultures: Seq[String]): TagState = copy(acceptedCultures = acceptedCultures.filter(!cultures.contains(_)))



  def setReligion(religion: String): TagState = copy(religion = Option(religion))

  def setReligiousSchools(religiousSchools: Seq[String]): TagState = copy(religiousSchools = religiousSchools)

  def setCults(cults: Seq[String]): TagState = copy(cults = cults)

  def setSecondaryReligion(religion: String): TagState = copy(secondaryReligion = Option(religion))

  def addHarmonizedReligion(religion: String): TagState = copy(harmonizedReligions = harmonizedReligions :+ religion)

  def addPiety(p: BigDecimal): TagState = copy(piety = piety.map(_ + p).orElse(Option(p)))



  def setUnitType(unitType: String): TagState = copy(unitType = Option(unitType))

  def setArmyProfessionalism(p: BigDecimal): TagState = copy(armyProfessionalism = Option(p))

  def addArmyProfessionalism(p: BigDecimal): TagState = copy(armyProfessionalism = Option(p).map(_ + p).orElse(Option(p)))



  def addHistoricalRival(r: String): TagState = addHistoricalRivals(Seq(r))

  def addHistoricalRivals(rs: Seq[String]): TagState = copy(historicalRivals = historicalRivals ++ rs)

  def addHistoricalFriend(f: String): TagState = addHistoricalFriends(Seq(f))

  def addHistoricalFriends(fs: Seq[String]): TagState = copy(historicalFriends = historicalFriends ++ fs)

  def changeTagFrom(tag: String): TagState = copy(changedTagFrom = Option(tag))

  def addPriceModifiers(pms: Seq[PriceModifier]): TagState = copy(priceModifiers = priceModifiers ++ pms)

  def addCountryModifier(cm: CountryModifier): TagState = copy(countryModifiers = countryModifiers :+ cm)

  def addRulerModifier(rm: RulerModifier): TagState = copy(rulerModifiers = rulerModifiers :+ rm)



  def showAmbientObject(obj: String): TagState = copy(ambientObjects = ambientObjects :+ obj)

  def hideAmbientObject(obj: String): TagState = copy(ambientObjects = ambientObjects.filter(_ != obj))

}

object TagState extends FromJson[TagState] {

  type StateUpdate[T] = (TagState, T) => TagState

  val empty = new TagState()

  def updatedFieldsFrom(update: TagUpdate): Seq[TagState => TagState] =
    Seq(
      nextF(update.technologyGroup, (s, v: String) => s.updateTechnologyGroup(v)),
      nextF(update.capital, (s, v: Int) => s.updateCapital(v)),
      nextF(update.fixedCapital, (s, v: Int) => s.updateFixedCapital(v)),
      nextF(update.government, (s, v: String) => s.updateGovernment(v)),
      nextF(update.governmentRank, (s, v: Int) => s.updateGovernmentRank(v)),
      nextF(update.setLegacyGovernment, (s, v: String) => s.updateLegacyGovernment(v)),
      nextF(update.setGlobalFlag, (s, v: Seq[String]) => s.setGlobalFlags(v)),
      nextF(update.setCountryFlag, (s, v: Seq[String]) => s.setCountryFlags(v)),
      nextF(update.clrCountryFlag, (s, v: Seq[String]) => s.clearCountryFlags(v)),
      nextF(update.changedTagFrom, (s, v: String) => s.changeTagFrom(v)),

      nextF(update.monarch, (s, v: Monarch) => s.updateMonarch(v)),
      nextF(update.heir, (s, v: Heir) => s.updateHeir(v)),
      nextF(update.queen, (s, v: Queen) => s.updateQueen(v)),
      nextF(update.leader, (s, v: Seq[Leader]) => s.addLeaders(v)),
      nextF(update.addRulerPersonality, (s, v: Seq[String]) => s.addRulerPersonalities(v)),
      nextF(update.addQueenPersonality, (s, v: String) => s.addQueenPersonality(v)),
      nextF(update.addHeirPersonality, (s, v: Seq[String]) => s.addHeirPersonalities(v)),
      nextF(update.clearScriptedPersonalities, (s, v: Boolean) => if (v) s.clearPersonalities() else s),

      nextF(update.addGovernmentReform, (s, v: String) => s.addGovernmentReform(v)),
      nextF(update.decision, (s, v: Seq[String]) => s.addDecisions(v)),
      nextF(update.mercantilism, (s, v: Int) => s.setMercantilism(v)),
      nextF(update.nationalFocus, (s, v: String) => s.setNationalFocus(v)),
      nextF(update.changePrice, (s, v: Seq[PriceModifier]) => s.addPriceModifiers(v)),
      nextF(update.addCountryModifier, (s, v: CountryModifier) => s.addCountryModifier(v)),

      nextF(update.historicalRival, (s, v: Seq[String]) => s.addHistoricalRivals(v)),
      nextF(update.historicalFriend, (s, v: Seq[String]) => s.addHistoricalFriends(v)),
      nextF(update.elector, (s, v: Boolean) => s.isHreElector(v)),
      nextF(update.enableHreLeagues, (s, v: Boolean) => s.enableHreLeagues(v)),
      nextF(update.joinLeague, (s, v: String) => s.joinHreLeague(v)),
      nextF(update.leaveLeague, (s, v: String) => s.leaveHreLeague()),
      nextF(update.setHreReligionTreaty, (s, v: Boolean) => s.hreReligionTreaty(v)),
      nextF(update.revolutionTarget, (s, v: Boolean) => s.revolutionTarget(v)),

      nextF(update.primaryCulture, (s, v: String) => s.setPrimaryCulture(v)),
      nextF(update.addAcceptedCulture, (s, v: Seq[String]) => s.addAcceptedCultures(v)),
      nextF(update.removeAcceptedCulture, (s, v: Seq[String]) => s.removeAcceptedCultures(v)),
      nextF(update.religion, (s, v: String) => s.setReligion(v)),
      nextF(update.secondaryReligion, (s, v: String) => s.setSecondaryReligion(v)),
      nextF(update.addHarmonizedReligion, (s, v: String) => s.addHarmonizedReligion(v)),
      nextF(update.religiousSchool, (s, v: Seq[String]) => s.setReligiousSchools(v)),
      nextF(update.unlockCult, (s, v: Seq[String]) => s.setCults(v)),
      nextF(update.addPiety, (s, v: BigDecimal) => s.addPiety(v)),

      nextF(update.unitType, (s, v: String) => s.setUnitType(v)),
      nextF(update.addArmyProfessionalism, (s, v: BigDecimal) => s.addArmyProfessionalism(v)),
      nextF(update.changeUnitType, (s, v: String) => s.setUnitType(v)),

      nextF(update.hideAmbientObject, (s, v: String) => s.hideAmbientObject(v)),
      nextF(update.showAmbientObject, (s, v: String) => s.showAmbientObject(v)),
    )
      .flatten

  private def updateValue[T](s: TagState,
                             v: T,
                             f: StateUpdate[T]): TagState = f(s, v)

  private def nextF[T](ov: Option[T], f: StateUpdate[T]): Option[TagState => TagState] =
    ov.map(v => updateValue(_: TagState, v, f))

}
