package com.lomicron.oikoumene.service.province

import com.lomicron.oikoumene.model.modifiers.Modifier
import com.lomicron.oikoumene.model.provinces._
import com.lomicron.oikoumene.model.trade.TradeGood
import com.lomicron.oikoumene.repository.api.RepositoryFactory

case class ProvinceService(repos: RepositoryFactory) {

  def bdo(d: Double): Option[BigDecimal] = Some(BigDecimal(d))

  val tradePowerAutonomyModifier: BigDecimal = 0.5

  val localRecruitmentTimePerTaxDev: BigDecimal = -0.01
  val taxPerTaxDev: BigDecimal = 1
  val localGoodsProducedPerProductionDev: BigDecimal = 0.2
  val localShipBuildingTimePerProductionDev: BigDecimal = -0.01
  val manpowerPerManpowerDev: BigDecimal = 250
  val garrisonGrowthPerManpowerDev: BigDecimal = 0.01

  val possibleNumberOfBuildingsPerDev: BigDecimal = 0.1
  val supplyLimitModifierPerDev: BigDecimal = 0.02
  val localMissionaryStrengthPerDev: BigDecimal = -0.1
  val tradePowerPerDev: BigDecimal = 0.2
  val sailorsPerDev: BigDecimal = 30

  def productionOf(p: Province): ProvinceProduction = {
    val provinceModifiers = p.state.modifiers.values.toSeq.flatMap(_.effect)
    //    val buildingModifiers = p.state.buildings.flatMap(getBuilding).map(_.modifier)
    //    val nationModifiers =
    //    val modifiers = provinceModifiers ++ buildingModifiers ++ nationModifiers
    val modifiers = provinceModifiers
    val autonomyModifier = (BigDecimal(100) - p.state.localAutonomy) / 100
    val tax = taxOf(p.state, modifiers)  * autonomyModifier
    val goodsProduced = goodsProducedOf(p.state, modifiers)
    val production = productionOf(goodsProduced, modifiers) * autonomyModifier
    val tradeValue = goodsProduced * marketPriceOf(p)
    val tradePower = tradePowerOf(p.state, modifiers) * autonomyModifier * tradePowerAutonomyModifier
    val manpower = manpowerOf(p.state, modifiers) * autonomyModifier
    val sailors = sailorsOf(p.state, modifiers) * autonomyModifier

    ProvinceProduction(
      tax,
      goodsProduced,
      production,
      tradePower = tradePower,
      tradeValue = tradeValue,
      manpower = manpower,
      sailors = sailors,
    )

  }

  def getBuilding(id: String): Option[Building] =
    repos.buildings.find(id).toOption

  def taxOf(p: ProvinceState, modifiers: Seq[Modifier] = Seq.empty): BigDecimal = {
    val baseTax = BigDecimal(p.baseTax)
    val localTaxIncome = modifiers.flatMap(_.taxIncome).sum
    val taxIncomeEfficiency = modifiers.flatMap(_.localTaxModifier).sum / 100

    (baseTax + localTaxIncome) * taxIncomeEfficiency / 12
  }

  def productionValueOf(g: TradeGood, goodsProduced: BigDecimal, modifiers: Seq[Modifier]): BigDecimal = {
    val productionEfficiency = modifiers.flatMap(_.productionEfficiency).sum / 100
    goodsProduced * (1 + productionEfficiency) * g.price
  }

  def goodsProducedOf(p: ProvinceState, modifiers: Seq[Modifier] = Seq.empty): BigDecimal = {
    val baseGoodsProduced = localGoodsProducedPerProductionDev * p.baseProduction
    val extraGoodsProduced = modifiers.flatMap(_.tradeGoodsSize).sum
    val productionModifiers = modifiers.flatMap(_.globalTradeGoodsSizeModifier).sum / 100
    (baseGoodsProduced + extraGoodsProduced) * productionModifiers
  }

  def productionOf(goodsProduced: BigDecimal, modifiers: Seq[Modifier] = Seq.empty): BigDecimal = {
    val productionEfficiency = modifiers.flatMap(_.productionEfficiency).sum / 100
    goodsProduced * productionEfficiency
  }

  def tradePowerOf(p: ProvinceState, modifiers: Seq[Modifier] = Seq.empty): BigDecimal = {
    val baseTP = p.development * tradePowerPerDev
    val addedTP = modifiers.flatMap(_.provinceTradePowerValue).sum
    val efficiency = modifiers.flatMap(_.provinceTradePowerModifier).sum / 100
    (baseTP + addedTP) * efficiency
  }

  def marketPriceOf(p: Province): BigDecimal =
    p.state.tradeGood
      .flatMap(repos.tradeGoods.find(_).toOption)
      .map(_.price)
      .getOrElse(1)

  def manpowerOf(p: ProvinceState, modifiers: Seq[Modifier] = Seq.empty): BigDecimal = {
    val baseManpower = p.baseManpower * manpowerPerManpowerDev
    val manpowerEfficiency = modifiers.flatMap(_.localManpowerModifier).sum / 100
    baseManpower * manpowerEfficiency
  }

  def sailorsOf(p: ProvinceState, modifiers: Seq[Modifier] = Seq.empty): BigDecimal = {
    val baseSailors = BigDecimal(p.development) * sailorsPerDev
    val sailorEfficiency = modifiers.flatMap(_.localSailorsModifier).sum / 100
    baseSailors * sailorEfficiency
  }

}
