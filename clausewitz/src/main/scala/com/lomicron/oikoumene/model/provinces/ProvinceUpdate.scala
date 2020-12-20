package com.lomicron.oikoumene.model.provinces

import com.fasterxml.jackson.annotation.{JsonCreator, JsonIgnore}
import com.lomicron.oikoumene.model.history.HistEvent
import com.lomicron.oikoumene.model.modifiers.ActiveModifier
import com.lomicron.utils.parsing.tokenizer.Date

case class ProvinceUpdate
(
  override val date: Option[Date] = None,
  // hits = 9400, isOptional = true, sample = "SWE"
  owner: Option[String] = None,
  // hits = 13305, isOptional = true, sample = "SWE"
  controller: Option[String] = None,
  // hits = 7771, isOptional = true, sample = "SWE"
  addCore: Option[Seq[String]] = None,
  // hits = 2939, isOptional = true, sample = "DAN"
  removeCore: Option[Seq[String]] = None,
  // hits = 6990, isOptional = true, sample = "ottoman"
  discoveredBy: Option[Seq[String]] = None,
  // hits = 4349, isOptional = true, sample = "catholic"
  religion: Option[String] = None,
  // hits = 4162, isOptional = true, sample = "swedish"
  culture: Option[String] = None,
  // hits = 3719, isOptional = true, sample = "grain"
  tradeGoods: Option[String] = None,
  // hits = 3560, isOptional = true, sample = "Stockholm"
  capital: Option[String] = None,
  // hits = 3232, isOptional = true, sample = 5
  baseProduction: Option[Int] = None,
  // hits = 3230, isOptional = true, sample = 5
  baseTax: Option[Int] = None,
  // hits = 3073, isOptional = true, sample = 3
  baseManpower: Option[Int] = None,
  // hits = 14, isOptional = true, sample = "ANS"
  addClaim: Option[Seq[String]] = None,
  // hits = 7, isOptional = true, sample = "FRA"
  removeClaim: Option[Seq[String]] = None,

  // hits = 2924, isOptional = true, sample = true
  isCity: Option[Boolean] = None,
  // hits = 486, isOptional = true, sample = "fort_15th"
  addBuilding: Option[Seq[String]] = None,
  // hits = 177, isOptional = true, sample = "fort_15th"
  removeBuilding: Option[Seq[String]] = None,
  // hits = 376, isOptional = true, sample = 16
  extraCost: Option[Int] = None,
  // hits = 331, isOptional = true, sample = 2
  centerOfTrade: Option[Int] = None,
  // hits = 3174, isOptional = true, sample = false
  hre: Option[Boolean] = None,
  // hits = 6, isOptional = true, sample = "protestant"
  reformationCenter: Option[String] = None,
  // hits = 11, isOptional = true, sample = true
  seatInParliament: Option[Seq[Boolean]] = None,
  // hits = 17, isOptional = true, sample = "non_catholic_rome"
  addProvinceTriggeredModifier: Option[Seq[String]] = None,
  // hits = 4, isOptional = true, sample = "skanemarket"
  removeProvinceModifier: Option[String] = None,

  // hits = 4629, isOptional = true, sample = 10
  unrest: Option[Int] = None,
  // hits = 1304, isOptional = true, sample = {"type":"pretender_rebels","size":1,"leader":"Karl Knutsson Bonde"}
  revolt: Option[Seq[ProvinceRevolt]] = None,
  // hits = 65, isOptional = true, sample = 3
  revoltRisk: Option[Int] = None,

  // hits = 993, isOptional = true, sample = 3
  nativeSize: Option[Int] = None,
  // hits = 980, isOptional = true, sample = 2
  nativeFerocity: Option[BigDecimal] = None,
  // hits = 975, isOptional = true, sample = 1
  nativeHostileness: Option[Int] = None,

  // hits = 269, isOptional = true, sample = "estate_nobles"
  estate: Option[String] = None,
  // hits = 170, isOptional = true, sample = 25
  addLocalAutonomy: Option[Int] = None,
  // hits = 82, isOptional = true, sample = {"name":"skanemarket","duration":-1}
  addPermanentProvinceModifier: Option[Seq[ActiveModifier]] = None,
  // hits = 53, isOptional = true, sample = ["coal"]
  latentTradeGoods: Option[Seq[String]] = None,
  // hits = 36, isOptional = true, sample = {"investment":"local_quarter","investor":"POR"}
  addTradeCompanyInvestment: Option[Seq[TradeCompanyInvestment]] = None,
  // hits = 35, isOptional = true, sample = "POR"
  addToTradeCompany: Option[String] = None,

  // hits = 27, isOptional = true, sample = true
  addRajputsOrMarathasOrNoblesEffect: Option[Boolean] = None,
  // hits = 25, isOptional = true, sample = true
  addBrahminsOrChurchEffect: Option[Boolean] = None,
  // hits = 9, isOptional = true, sample = true
  addVaisyasOrBurghersEffect: Option[Boolean] = None,
  // hits = 8, isOptional = true, sample = true
  addJainsOrBurghersEffect: Option[Boolean] = None,
) extends HistEvent {

  @JsonCreator def this() = this(controller = None)

  def addCoreCp(core: String): ProvinceUpdate = {
    val cores = addCore.map(_ :+ core).map(_.distinct).orElse(Some(Seq(core)))
    copy(addCore = cores)
  }

  @JsonIgnore def isEmpty: Boolean = !isNotEmpty

  @JsonIgnore def isNotEmpty: Boolean = fields().exists(_.isDefined)

  private def fields(): Seq[Option[Any]] = Seq(controller, owner, addCore, removeCore, discoveredBy, religion, culture,
    tradeGoods, capital, baseTax, baseProduction, baseManpower, addClaim, removeClaim, isCity, addBuilding, removeBuilding,
    extraCost, centerOfTrade, hre, reformationCenter, seatInParliament, addProvinceTriggeredModifier, removeProvinceModifier,
    unrest, revolt, revoltRisk, nativeSize, nativeFerocity, nativeHostileness, estate,
    addLocalAutonomy, addPermanentProvinceModifier, latentTradeGoods, addTradeCompanyInvestment, addToTradeCompany,
    addRajputsOrMarathasOrNoblesEffect, addBrahminsOrChurchEffect, addVaisyasOrBurghersEffect, addJainsOrBurghersEffect)

}

object ProvinceUpdate {
  def empty: ProvinceUpdate = ProvinceUpdate()
}
