package com.lomicron.eu4.parsers.trade

import com.lomicron.eu4.model.trade.CenterOfTrade
import com.lomicron.eu4.parsers.{ClausewitzParser, ConfigField}
import com.lomicron.eu4.repository.api.RepositoryFactory
import com.lomicron.eu4.repository.api.trade.CenterOfTradeRepository

object CenterOfTradeParser {

  def apply(repos: RepositoryFactory, evalEntityFields: Boolean = false): CenterOfTradeRepository = {
    val cotFiles = repos.resources.getCentersOfTrade
    val localisation = repos.localisations
    val cots = ClausewitzParser
      .parseFileFieldsAsEntities(cotFiles)
      .map(localisation.setLocalisation)

    if (evalEntityFields)
      ConfigField.printCaseClass("CenterOfTrade", cots)

    cots
      .map(CenterOfTrade.fromJson)
      .foreach(repos.centersOfTrade.create)

    repos.centersOfTrade
  }

}
