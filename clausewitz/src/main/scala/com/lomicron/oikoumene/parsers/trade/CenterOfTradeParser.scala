package com.lomicron.oikoumene.parsers.trade

import com.lomicron.oikoumene.model.trade.CenterOfTrade
import com.lomicron.oikoumene.parsers.ClausewitzParser.setLocalisation
import com.lomicron.oikoumene.parsers.{ClausewitzParser, ConfigField}
import com.lomicron.oikoumene.repository.api.RepositoryFactory
import com.lomicron.oikoumene.repository.api.trade.CenterOfTradeRepository

object CenterOfTradeParser {

  def apply(repos: RepositoryFactory, evalEntityFields: Boolean = false): CenterOfTradeRepository = {
    val cotFiles = repos.resources.getCentersOfTrade
    val localisation = repos.localisations
    val cots = ClausewitzParser
      .parseFileFieldsAsEntities(cotFiles)
      .map(setLocalisation(_, localisation))

    if (evalEntityFields)
      ConfigField.printCaseClass("CenterOfTrade", cots)

    cots
      .map(CenterOfTrade.fromJson)
      .foreach(repos.centersOfTrade.create)

    repos.centersOfTrade
  }

}
