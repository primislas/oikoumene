package com.lomicron.oikoumene.model.provinces

import com.lomicron.oikoumene.model.politics.{Culture, CultureGroup, Religion, ReligionGroup, Tag}
import com.lomicron.oikoumene.model.trade.TradeGood

import scala.collection.immutable.ListSet

case class ResolvedProvinceState
(
  baseTax: Int = 0,
  baseProduction: Int = 0,
  baseManpower: Int = 0,

  discoveredBy: Set[String] = ListSet.empty,
  tradeGood: Option[TradeGood] = None,
  capital: Option[String] = None,
  culture: Option[Culture] = None,
  cultureGroup: Option[CultureGroup] = None,
  religion: Option[Religion] = None,
  religionGroup: Option[ReligionGroup] = None,
  owner: Option[Tag] = None,
  controller: Option[Tag] = None,
  cores: List[Tag] = List.empty,
  claims: List[Tag] = List.empty,
  isCity: Boolean = false,

  nativeSize: Int = 0,
  nativeFerocity: BigDecimal = 0,
  nativeHostileness: Int = 0,

  unrest: Int = 0,
  revoltRisk: Int = 0,
  revolt: Option[ProvinceRevolt] = None,

  buildings: Map[String, Building] = Map.empty,
  centerOfTrade: Int = 0,
  estate: Option[String] = None,
  extraCost: Int = 0,
  localAutonomy: Int = 0,
  permanentModifiers: Seq[ActiveModifier] = Seq.empty,
  triggeredModifiers: Set[String] = Set.empty,
  latentTradeGoods: Set[String] = ListSet.empty,
  isInHre: Boolean = true,
  reformationCenter: Option[String] = None,
  seatInParliament: Boolean = false,
  tradeCompany: Option[String] = None,
  tradeCompanyInvestment: Option[TradeCompanyInvestment] = None
) {

}
