package com.lomicron.eu4.service.province

import com.lomicron.eu4.model.modifiers.{ActiveModifier, Modifier, StaticModifiers}
import com.lomicron.eu4.model.provinces._
import com.lomicron.eu4.repository.api.RepositoryFactory
import com.lomicron.utils.collection.CollectionUtils.toOption
import com.softwaremill.quicklens._

case class ProvinceService(repos: RepositoryFactory) {

  def bdo(d: Double): Option[BigDecimal] = Some(BigDecimal(d))

  val tradePowerAutonomyModifier: BigDecimal = 0.5

  def init(p: Province): Province = {
    var ps = p.state

    def addm(mId: String): ProvinceState = addModifier(ps, mId)

    ps = initDevelopment(ps)
    if (p.isLand) ps = addm("land_province")
    else ps = addm("sea_zone")
    ps = p.geography.terrain.map(t => addm(t)).getOrElse(ps)
    ps = p.geography.climate.lastOption.map(c => addm(c)).getOrElse(ps)
    if (p.isCoastal) ps = addm("coastal")
    if (ps.isCity) ps = addm("city")
    ps = addTradeGood(ps)
    ps = addBuildings(ps)
    ps = addCot(ps, p.geography.landlocked)
    ps = resolveModifiers(ps)

    val initialized = p.modify(_.history.state).setTo(ps)
    val production = productionOf(initialized)
    initialized.modify(_.history.state.production).setTo(production)
  }

  def initDevelopment(ps: ProvinceState): ProvinceState = {
    var ups = ps
    def addm(mId: String, factor: Int): Unit = {
      ups = findModifier(mId).map(_.multiply(factor)).map(ups.addModifier).getOrElse(ups)
    }
    addm(StaticModifiers.provincialTaxIncome, ups.baseTax)
    addm(StaticModifiers.provincialProductionSize, ups.baseProduction)
    addm(StaticModifiers.manpower, ups.baseManpower)
    addm(StaticModifiers.development, ups.development)

    ups
  }

  def addTradeGood(state: ProvinceState): ProvinceState =
    state
      .tradeGood
      .flatMap(repos.tradeGoods.find(_))
      .flatMap(tg => tg.province.map(_.copy(id = tg.id)))
      .map(state.addModifier)
      .getOrElse(state)

  def addBuildings(state: ProvinceState): ProvinceState =
    state.buildings
      .flatMap(id => repos.buildings.find(id))
      .map(b => b.modifier.copy(id = b.id))
      .foldLeft(state)(_ addModifier _)

  def addCot(ps: ProvinceState, isLandlocked: Boolean): ProvinceState = {
    repos
      .centersOfTrade
      .ofProvince(ps, isLandlocked)
      .flatMap(cot => cot.provinceModifiers.map(_.copy(id = cot.id)))
      .map(ActiveModifier.of)
      .map(ps.addModifier)
      .getOrElse(ps)
  }

  def findModifier(mId: String): Option[Modifier] =
    repos.modifiers.find(mId)

  def addModifier(p: Province, mId: String): Province =
    findModifier(mId)
      .map(m => ActiveModifier(name = mId, effect = Some(m)))
      .map(m => p.addModifier(m))
      .getOrElse(p)

  def addModifier(ps: ProvinceState, mId: String): ProvinceState =
    findModifier(mId)
      .map(m => ActiveModifier(name = mId, effect = Some(m)))
      .map(m => ps.addModifier(m))
      .getOrElse(ps)

  def resolveModifiers(state: ProvinceState): ProvinceState = {
    var resolved = state.triggeredModifiers.flatMap(findModifier).foldLeft(state)(_ addModifier _)
    resolved = state.permanentModifiers.flatMap(am => findModifier(am.name)).foldLeft(resolved)(_ addModifier _)

    resolved
  }


  def productionOf
  (
    p: Province,
    stateModifier: Modifier = Modifier.empty,
    tagModifier: Modifier = Modifier.empty
  ): ProvinceProduction = {
    val modifiers = Seq(p.state.modifier, stateModifier, tagModifier)
    val autonomyModifier = bd1 - p.state.localAutonomy / 100
    val tax = taxOf(p.state, modifiers) * autonomyModifier
    val price = marketPriceOf(p)
    val goodsProduced = goodsProducedOf(p.state, modifiers)
    val production = productionOf(p.state, goodsProduced, modifiers) * price * autonomyModifier
    val tradeValue = goodsProduced * price
    val tradePower = tradePowerOf(p.state, modifiers) * autonomyModifier * tradePowerAutonomyModifier
    val manpower = manpowerOf(p.state, modifiers) * autonomyModifier
    val landForcelimit = landForcelimitOf(p.state, modifiers)
    val sailors = sailorsOf(p.state, p.geography, modifiers) * autonomyModifier
    val navalForcelimit = navalForcelimitOf(p.state, p.geography, modifiers)

    ProvinceProduction(
      tax,
      goodsProduced,
      production,
      tradePower = tradePower,
      tradeValue = tradeValue,
      manpower = manpower,
      sailors = sailors,
      landForcelimit = landForcelimit,
      navalForcelimit = navalForcelimit,
    )

  }

  def getBuilding(id: String): Option[Building] =
    repos.buildings.find(id)

  val bd0 = BigDecimal(0)
  val bd1 = BigDecimal(1)
  val bdNeg1 = BigDecimal(-1)

  def effectiveEfficiency(e: BigDecimal): BigDecimal =
    if (e < bdNeg1) bd0 else bd1 + e

  def taxOf(p: ProvinceState, modifiers: Seq[Modifier] = Seq.empty): BigDecimal = {
    val taxIncome = modifiers.flatMap(_.localTaxIncome).sum
    val ltm = modifiers.flatMap(_.localTaxModifier).sum
    val provGtm = p.modifier.globalTaxModifier.getOrElse(bd0)
    val gtm = modifiers.flatMap(_.globalTaxModifier).sum - provGtm
    val efficiency = effectiveEfficiency(ltm + gtm)

    taxIncome * efficiency
  }

  def goodsProducedOf(p: ProvinceState, modifiers: Seq[Modifier] = Seq.empty): BigDecimal = {
    val baseGoodsProduced = p.modifier.tradeGoodsSize.getOrElse(bd0)
    val localProdEff = modifiers.flatMap(_.tradeGoodsSizeModifier).sum
    val provGPE = p.modifier.globalTradeGoodsSizeModifier.getOrElse(bd0)
    val globalProdEff = modifiers.flatMap(_.globalTradeGoodsSizeModifier).sum - provGPE
    val tradeGoodSizeModifiers = effectiveEfficiency(localProdEff + globalProdEff)

    baseGoodsProduced * tradeGoodSizeModifiers
  }

  def productionOf(p: ProvinceState, goodsProduced: BigDecimal, modifiers: Seq[Modifier]): BigDecimal = {
    val lpe = modifiers.flatMap(_.localProductionEfficiency).sum
    val provGpe = p.modifier.productionEfficiency.getOrElse(bd0)
    val gpe = modifiers.flatMap(_.productionEfficiency).sum - provGpe
    val productionEfficiency = effectiveEfficiency(lpe + gpe)

    goodsProduced * productionEfficiency
  }

  def tradePowerOf(p: ProvinceState, modifiers: Seq[Modifier] = Seq.empty): BigDecimal = {
    val m = p.modifier
    val baseTP = modifiers.flatMap(_.provinceTradePowerValue).sum
    val ltpm = modifiers.flatMap(_.provinceTradePowerModifier).sum
    val provGtpm = m.globalProvTradePowerModifier.getOrElse(bd0)
    val gtpm = modifiers.flatMap(_.globalProvTradePowerModifier).sum - provGtpm
    val efficiency = effectiveEfficiency(ltpm + gtpm)

    baseTP * efficiency
  }

  def marketPriceOf(p: Province): BigDecimal =
    p.state.tradeGood
      .flatMap(repos.tradeGoods.find(_))
      .map(_.price)
      .getOrElse(bd1)

  def manpowerOf(p: ProvinceState, modifiers: Seq[Modifier] = Seq.empty): BigDecimal = {
    val m = p.modifier
    val baseManpower = m.localManpower.getOrElse(bd0)
    val lmm = modifiers.flatMap(_.localManpowerModifier).sum
    val provGmm = m.globalManpowerModifier.getOrElse(bd0)
    val gmm = modifiers.flatMap(_.globalManpowerModifier).sum - provGmm
    val manpowerEfficiency = effectiveEfficiency(lmm + gmm)

    baseManpower * manpowerEfficiency * 1000
  }

  def sailorsOf(p: ProvinceState, g: ProvinceGeography, modifiers: Seq[Modifier] = Seq.empty): BigDecimal =
    if (g.isCoastal) {
      val m = p.modifier
      val baseSailors = m.localSailors.map(_ * p.development).getOrElse(0)
      val lsm = modifiers.flatMap(_.localSailorsModifier).sum
      val provGsm = m.globalSailorsModifier.getOrElse(bd0)
      val gsm = modifiers.flatMap(_.globalSailorsModifier).sum - provGsm
      val sailorEfficiency = effectiveEfficiency(lsm + gsm)

      sailorEfficiency * baseSailors
    } else
      bd0

  def landForcelimitOf(p: ProvinceState, modifiers: Seq[Modifier]): BigDecimal = {
    val m = p.modifier
    val baseForcelimit = m.landForcelimit.getOrElse(bd0)
    val lfm = modifiers.flatMap(_.landForcelimitModifier).sum
    val efficiency = effectiveEfficiency(lfm)

    baseForcelimit * efficiency
  }

  def navalForcelimitOf(p: ProvinceState, g: ProvinceGeography, modifiers: Seq[Modifier]): BigDecimal =
    if (g.isCoastal) {
      val m = p.modifier
      val baseForcelimit = m.navalForcelimit.getOrElse(bd0)
      val lfm = modifiers.flatMap(_.navalForcelimitModifier).sum
      val efficiency = effectiveEfficiency(lfm)

      baseForcelimit * efficiency
    } else
      bd0

}
