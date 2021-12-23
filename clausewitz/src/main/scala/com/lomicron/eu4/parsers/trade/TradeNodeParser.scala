package com.lomicron.eu4.parsers.trade

import com.lomicron.eu4.model.trade.TradeNode
import com.lomicron.eu4.parsers.{ClausewitzParser, ConfigField}
import com.lomicron.eu4.repository.api.RepositoryFactory
import com.lomicron.eu4.repository.api.trade.TradeNodeRepository
import com.lomicron.utils.json.JsonMapper._

object TradeNodeParser {

  def apply
  (
    repos: RepositoryFactory,
    evalEntityFields: Boolean = false
  ): TradeNodeRepository = {

    val files = repos.resources.getTradeNodes
    val localisation = repos.localisations

    val nodes = ClausewitzParser
      .parseFileFieldsAsEntities(files)
      .map(localisation.setLocalisation)

    if (evalEntityFields) {
      val routes = nodes.flatMap(_.getSeqOfObjects("outgoing"))
      ConfigField.printCaseClass("TradeNodeRoute", routes)
      ConfigField.printCaseClass("TradeNode", nodes)
    }

    nodes.map(TradeNode.fromJson).foreach(repos.tradeNodes.create)
    repos.tradeNodes
  }

}
