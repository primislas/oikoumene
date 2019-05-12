package com.lomicron.oikoumene.parsers.trade

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.oikoumene.model.Entity
import com.lomicron.oikoumene.model.trade.TradeGood
import com.lomicron.oikoumene.parsers.{ClausewitzParser, ConfigField}
import com.lomicron.oikoumene.parsers.ClausewitzParser.setLocalisation
import com.lomicron.oikoumene.repository.api.RepositoryFactory
import com.lomicron.oikoumene.repository.api.trade.TradeGoodRepository
import com.lomicron.utils.json.FromJson
import com.lomicron.utils.json.JsonMapper._

object TradeGoodParser {

  def apply(repos: RepositoryFactory, evalEntityFields: Boolean = false): TradeGoodRepository = {
    val tgFiles = repos.resources.getTradeGoods
    val priceFiles = repos.resources.getPrices
    val localisation = repos.localisations

    val goods = ClausewitzParser
      .parseFileFieldsAsEntities(tgFiles)
      .map(setLocalisation(_, localisation))
      .map(parseTradeGood)
    val withIndex = ClausewitzParser.setIndex(goods)

    if (evalEntityFields)
      ConfigField.printCaseClass("TradeGood", withIndex)

    val prices = ClausewitzParser
      .parseFileFieldsAsEntities(priceFiles)
      .map(Price.fromJson)
    withIndex
      .map(TradeGood.fromJson)
      .map(g => prices.find(_.id == g.id).map(_.basePrice).map(g.withPrice).getOrElse(g))
      .foreach(repos.tradeGoods.create)

    repos.tradeGoods
  }

  def parseTradeGood(o: ObjectNode): ObjectNode =
    o.getArray("color")
      .map(ClausewitzParser.decimalColorToInt)
      .map(c => o.setEx("color", c))
      .getOrElse(o)

}

case class Price(id: String, basePrice: BigDecimal = BigDecimal(0)) {
  @JsonCreator def this() = this(Entity.UNDEFINED)
}

object Price extends FromJson[Price]
