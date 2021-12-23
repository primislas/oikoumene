package com.lomicron.eu4.parsers.trade

import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.eu4.model.trade.{Price, TradeGood}
import com.lomicron.eu4.repository.api.RepositoryFactory
import com.lomicron.eu4.repository.api.trade.TradeGoodRepository
import com.lomicron.oikoumene.parsers.{ClausewitzParser, ConfigField}
import com.lomicron.utils.json.JsonMapper._

object TradeGoodParser {

  def apply(repos: RepositoryFactory, evalEntityFields: Boolean = false): TradeGoodRepository = {
    val tgFiles = repos.resources.getTradeGoods
    val priceFiles = repos.resources.getPrices
    val localisation = repos.localisations

    val goods = ClausewitzParser
      .parseFileFieldsAsEntities(tgFiles)
      .map(localisation.setLocalisation)
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
