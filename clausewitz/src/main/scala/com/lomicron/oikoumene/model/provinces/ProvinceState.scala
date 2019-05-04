package com.lomicron.oikoumene.model.provinces

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.oikoumene.model.EntityState
import com.lomicron.oikoumene.model.provinces.ProvinceState.updatedFieldsFrom

import scala.collection.immutable.ListSet

case class ProvinceState
(baseTax: Int = 0,
 baseProduction: Int = 0,
 baseManpower: Int = 0,
 discoveredBy: Set[String] = ListSet.empty,
 tradeGood: Option[String] = None,
 capital: Option[String] = None,
 culture: Option[String] = None,
 cultureGroup: Option[String] = None,
 religion: Option[String] = None,
 religionGroup: Option[String] = None,
 owner: Option[String] = None,
 controller: Option[String] = None,
 cores: Set[String] = ListSet.empty,
 claims: Set[String] = ListSet.empty,
 isCity: Boolean = false,

 nativeSize: Int = 0,
 nativeFerocity: Int = 0,
 nativeHostileness: Int = 0,

 unrest: Int = 0,
 revoltRisk: Int = 0,
 revolt: Option[ProvinceRevolt] = None,

 buildings: Set[String] = ListSet.empty,
 centerOfTrade: Int = 0,
 estate: Option[String] = None,
 extraCost: Int = 0,
 localAutonomy: Int = 0,
 permanentModifiers: Seq[ProvinceModifier] = Seq.empty,
 triggeredModifiers: Set[String] = Set.empty,
 latentTradeGoods: Set[String] = ListSet.empty,
 isInHre: Boolean = true,
 reformationCenter: Option[String] = None,
 seatInParliament: Boolean = false,
 tradeCompany: Option[String] = None,
 tradeCompanyInvestment: Option[TradeCompanyInvestment] = None
) extends EntityState[ProvinceState, ProvinceUpdate] {
  self =>

  @JsonCreator def this() = this(0)

  override def next(update: ProvinceUpdate): ProvinceState =
      updatedFieldsFrom(update).foldLeft(self)((acc, f) => f(acc))

  def development: Int = baseTax + baseProduction + baseManpower

  def updateOwner(v: String): ProvinceState = copy(owner = Some(v))

  def updateController(v: String): ProvinceState = copy(controller = Some(v))

  def addCore(v: String): ProvinceState = copy(cores = cores + v)

  def addCores(v: Seq[String]): ProvinceState = copy(cores = cores ++ v)

  def removeCore(v: String): ProvinceState = copy(cores = cores - v)

  def removeCores(v: Seq[String]): ProvinceState = copy(cores = cores -- v)

  def discoverBy(v: Seq[String]): ProvinceState = copy(discoveredBy = discoveredBy ++ v)

  def updateReligion(v: String): ProvinceState = copy(religion = Some(v))

  def updateCulture(v: String): ProvinceState = copy(culture = Some(v))

  def updateTradeGood(v: String): ProvinceState = copy(tradeGood = Some(v))

  def updateCapital(v: String): ProvinceState = copy(capital = Some(v))

  def updateBaseTax(v: Int): ProvinceState = copy(baseTax = v)

  def updateBaseProduction(v: Int): ProvinceState = copy(baseProduction = v)

  def updateBaseManpower(v: Int): ProvinceState = copy(baseManpower = v)

  def addClaim(v: String): ProvinceState = copy(claims = claims + v)

  def removeClaim(v: String): ProvinceState = copy(claims = claims - v)

  def isCity(v: Boolean): ProvinceState = copy(isCity = v)

  def addBuilding(v: String): ProvinceState = copy(buildings = buildings + v)

  def removeBuilding(v: String): ProvinceState = copy(buildings = buildings - v)

  def extraCost(v: Int): ProvinceState = copy(extraCost = v)

  def centerOfTrade(v: Int): ProvinceState = copy(centerOfTrade = v)

  def hre(v: Boolean): ProvinceState = copy(isInHre = v)

  def reformationCenter(v: String): ProvinceState = copy(reformationCenter = Some(v))

  def seatInParliament(v: Boolean): ProvinceState = copy(seatInParliament = v)

  def updateSeatInParliament(v: Seq[Boolean]): ProvinceState =
    v.foldLeft(this)(_ seatInParliament _)

  def addTriggeredModifier(v: String): ProvinceState = copy(triggeredModifiers = triggeredModifiers + v)

  def addTriggeredModifiers(v: Seq[String]): ProvinceState = copy(triggeredModifiers = triggeredModifiers ++ v)

  def removeModifier(v: String): ProvinceState = copy(triggeredModifiers = triggeredModifiers - v)

  def updateUnrest(v: Int): ProvinceState = copy(unrest = v)

  def updateRevolt(v: ProvinceRevolt): ProvinceState =
    if (v.isEmpty) copy(revolt = None)
    else copy(revolt = Some(v))

  def updateRevolts(v: Seq[ProvinceRevolt]): ProvinceState =
    v.foldLeft(this)(_ updateRevolt _)

  def updateRevoltRisk(v: Int): ProvinceState = copy(revoltRisk = v)

  def updateNativeSize(v: Int): ProvinceState = copy(nativeSize = v)

  def updateNativeFerocity(v: Int): ProvinceState = copy(nativeFerocity = v)

  def updateNativeHostileness(v: Int): ProvinceState = copy(nativeHostileness = v)

  def estate(v: String): ProvinceState = copy(estate = Some(v))

  def addLocalAutonomy(v: Int): ProvinceState = copy(localAutonomy = localAutonomy + v)

  def addPermanentModifier(v: ProvinceModifier): ProvinceState = copy(permanentModifiers = permanentModifiers :+ v)

  def addPermanentModifiers(v: Seq[ProvinceModifier]): ProvinceState =
    v.foldLeft(this)(_ addPermanentModifier _)


  def latentTradeGoods(v: Seq[String]): ProvinceState = copy(latentTradeGoods = v.toSet)

  def addTradeCompanyInvestment(v: TradeCompanyInvestment): ProvinceState =
    copy(tradeCompanyInvestment = Some(v))

  def addTradeCompanyInvestments(v: Seq[TradeCompanyInvestment]): ProvinceState =
    v.foldLeft(this)(_ addTradeCompanyInvestment _)

  def addToTradeCompany(v: String): ProvinceState = copy(tradeCompany = Some(v))

  // TODO indian estate event updates
}

object ProvinceState {

  type StateUpdate[T] = (ProvinceState, T) => ProvinceState

  val empty = new ProvinceState()

  def updatedFieldsFrom(update: ProvinceUpdate): Seq[ProvinceState => ProvinceState] =
    Seq(
      nextF(update.owner, (s, v: String) => s.updateOwner(v)),
      nextF(update.controller, (s, v: String) => s.updateController(v)),
      nextF(update.addCore, (s, v: Seq[String]) => s.addCores(v)),
      nextF(update.removeCore, (s, v: Seq[String]) => s.removeCores(v)),
      nextF(update.discoveredBy, (s, v: Seq[String]) => s.discoverBy(v)),
      nextF(update.religion, (s, v: String) => s.updateReligion(v)),
      nextF(update.culture, (s, v: String) => s.updateCulture(v)),
      nextF(update.tradeGoods, (s, v: String) => s.updateTradeGood(v)),
      nextF(update.capital, (s, v: String) => s.updateCapital(v)),
      nextF(update.baseTax, (s, v: Int) => s.updateBaseTax(v)),
      nextF(update.baseProduction, (s, v: Int) => s.updateBaseProduction(v)),
      nextF(update.baseManpower, (s, v: Int) => s.updateBaseManpower(v)),
      nextF(update.addClaim, (s, v: String) => s.addClaim(v)),
      nextF(update.removeClaim, (s, v: String) => s.removeClaim(v)),
      nextF(update.isCity, (s, v: Boolean) => s.isCity(v)),
      nextF(update.addBuilding, (s, v: String) => s.addBuilding(v)),
      nextF(update.removeBuilding, (s, v: String) => s.removeBuilding(v)),
      nextF(update.extraCost, (s, v: Int) => s.extraCost(v)),
      nextF(update.centerOfTrade, (s, v: Int) => s.centerOfTrade(v)),
      nextF(update.hre, (s, v: Boolean) => s.hre(v)),
      nextF(update.reformationCenter, (s, v: String) => s.reformationCenter(v)),
      nextF(update.seatInParliament, (s, v: Seq[Boolean]) => s.updateSeatInParliament(v)),
      nextF(update.addProvinceTriggeredModifier, (s, v: Seq[String]) => s.addTriggeredModifiers(v)),
      nextF(update.removeProvinceModifier, (s, v: String) => s.removeModifier(v)),
      nextF(update.unrest, (s, v: Int) => s.updateUnrest(v)),
      nextF(update.revolt, (s, v: Seq[ProvinceRevolt]) => s.updateRevolts(v)),
      nextF(update.revoltRisk, (s, v: Int) => s.updateRevoltRisk(v)),
      nextF(update.nativeSize, (s, v: Int) => s.updateNativeSize(v)),
      nextF(update.nativeFerocity, (s, v: Int) => s.updateNativeFerocity(v)),
      nextF(update.nativeHostileness, (s, v: Int) => s.updateNativeHostileness(v)),
      nextF(update.estate, (s, v: String) => s.estate(v)),
      nextF(update.addLocalAutonomy, (s, v: Int) => s.addLocalAutonomy(v)),
      nextF(update.addPermanentProvinceModifier, (s, v: Seq[ProvinceModifier]) => s.addPermanentModifiers(v)),
      nextF(update.latentTradeGoods, (s, v: Seq[String]) => s.latentTradeGoods(v)),
      nextF(update.addTradeCompanyInvestment, (s, v: Seq[TradeCompanyInvestment]) => s.addTradeCompanyInvestments(v)),
      nextF(update.addToTradeCompany, (s, v: String) => s.addToTradeCompany(v))
    )
      .flatten

  private def updateValue[T](s: ProvinceState,
                             v: T,
                             f: StateUpdate[T]): ProvinceState = f(s, v)

  private def nextF[T](ov: Option[T], f: StateUpdate[T]): Option[ProvinceState => ProvinceState] =
    ov.map(v => updateValue(_: ProvinceState, v, f))

}
