package com.lomicron.vicky.model.province

import com.fasterxml.jackson.annotation.{JsonCreator, JsonIgnore}
import com.lomicron.oikoumene.model.WithCumulativeModifier
import com.lomicron.oikoumene.model.history.HistState
import com.lomicron.oikoumene.model.modifiers.{ActiveModifier, Modifier}
import com.lomicron.vicky.model.politics.Pop
import com.lomicron.vicky.model.province.ProvinceState.updatedFieldsFrom

import scala.collection.immutable.ListSet

@JsonCreator
case class ProvinceState
(
  owner: Option[String] = None,
  controller: Option[String] = None,
  cores: ListSet[String] = ListSet.empty,
  lifeRating: Int = 0,
  tradeGoods: Option[String] = None,
  pops: Seq[Pop] = Seq.empty,

  @JsonIgnore override val activeModifiers: Map[String, ActiveModifier] = Map.empty,
  @JsonIgnore override val modifier: Modifier = Modifier(),

  fort: Int = 0,
  navalBase: Int = 0,
  railroad: Int = 0,
  stateBuilding: Seq[StateBuilding] = Seq.empty,

  partyLoyalty: Option[PartyLoyalty] = None,
  isSlave: Boolean = false,

  colonial: Option[Int] = None,
  colony: Int = 0,
) extends HistState[ProvinceState, ProvinceUpdate] with WithCumulativeModifier[ProvinceState] {
  self =>

  override def next(update: ProvinceUpdate): ProvinceState =
    updatedFieldsFrom(update).foldLeft(self)((acc, f) => f(acc))

  override def self: ProvinceState = self

  override def withModifiers(activeModifiers: Map[String, ActiveModifier], cumulative: Modifier): ProvinceState =
    copy(activeModifiers = activeModifiers, modifier = cumulative)

  def updateOwner(v: String): ProvinceState =
    if (v != "---") copy(owner = Some(v)) else copy(owner = None)

  def updateController(v: String): ProvinceState =
    if (v != "---") copy(controller = Some(v)) else copy(controller = None)

  def addCore(v: String): ProvinceState = copy(cores = cores + v)

  def addCores(v: Seq[String]): ProvinceState = copy(cores = cores ++ v)

  def removeCore(v: String): ProvinceState = copy(cores = cores - v)

  def removeCores(v: Seq[String]): ProvinceState = copy(cores = cores -- v)

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
//      nextF(update.discoveredBy, (s, v: Seq[String]) => s.discoverBy(v)),
//      nextF(update.religion, (s, v: String) => s.updateReligion(v)),
//      nextF(update.culture, (s, v: String) => s.updateCulture(v)),
//      nextF(update.tradeGoods, (s, v: String) => s.updateTradeGood(v)),
//      nextF(update.capital, (s, v: String) => s.updateCapital(v)),
//      nextF(update.baseTax, (s, v: Int) => s.updateBaseTax(v)),
//      nextF(update.baseProduction, (s, v: Int) => s.updateBaseProduction(v)),
//      nextF(update.baseManpower, (s, v: Int) => s.updateBaseManpower(v)),
//      nextF(update.addClaim, (s, v: Seq[String]) => s.addClaim(v)),
//      nextF(update.removeClaim, (s, v: Seq[String]) => s.removeClaim(v)),
//      nextF(update.isCity, (s, v: Boolean) => s.isCity(v)),
//      nextF(update.addBuilding, (s, v: Seq[String]) => s.addBuildings(v)),
//      nextF(update.removeBuilding, (s, v: Seq[String]) => s.removeBuildings(v)),
//      nextF(update.extraCost, (s, v: Int) => s.extraCost(v)),
//      nextF(update.centerOfTrade, (s, v: Int) => s.centerOfTrade(v)),
//      nextF(update.hre, (s, v: Boolean) => s.hre(v)),
//      nextF(update.reformationCenter, (s, v: String) => s.reformationCenter(v)),
//      nextF(update.seatInParliament, (s, v: Seq[Boolean]) => s.updateSeatInParliament(v)),
//      nextF(update.addProvinceTriggeredModifier, (s, v: Seq[String]) => s.addTriggeredModifiers(v)),
//      nextF(update.removeProvinceModifier, (s, v: String) => s.removeModifier(v)),
//      nextF(update.unrest, (s, v: Int) => s.updateUnrest(v)),
//      nextF(update.revolt, (s, v: Seq[ProvinceRevolt]) => s.updateRevolts(v)),
//      nextF(update.revoltRisk, (s, v: Int) => s.updateRevoltRisk(v)),
//      nextF(update.nativeSize, (s, v: Int) => s.updateNativeSize(v)),
//      nextF(update.nativeFerocity, (s, v: BigDecimal) => s.updateNativeFerocity(v)),
//      nextF(update.nativeHostileness, (s, v: Int) => s.updateNativeHostileness(v)),
//      nextF(update.estate, (s, v: String) => s.estate(v)),
//      nextF(update.addLocalAutonomy, (s, v: Int) => s.addLocalAutonomy(v)),
//      nextF(update.addPermanentProvinceModifier, (s, v: Seq[ActiveModifier]) => s.addPermanentModifiers(v)),
//      nextF(update.latentTradeGoods, (s, v: Seq[String]) => s.latentTradeGoods(v)),
//      nextF(update.addTradeCompanyInvestment, (s, v: Seq[TradeCompanyInvestment]) => s.addTradeCompanyInvestments(v)),
//      nextF(update.addToTradeCompany, (s, v: String) => s.addToTradeCompany(v))
    )
      .flatten

  private def updateValue[T](s: ProvinceState,
                             v: T,
                             f: StateUpdate[T]): ProvinceState = f(s, v)

  private def nextF[T](ov: Option[T], f: StateUpdate[T]): Option[ProvinceState => ProvinceState] =
    ov.map(v => updateValue(_: ProvinceState, v, f))

}
