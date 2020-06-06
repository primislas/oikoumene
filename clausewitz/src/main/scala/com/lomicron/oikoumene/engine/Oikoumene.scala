package com.lomicron.oikoumene.engine

import com.lomicron.oikoumene.parsers.diplomacy.{CasusBelliParser, DiplomacyParser, WarGoalTypeParser, WarHistoryParser}
import com.lomicron.oikoumene.parsers.government.IdeaParser
import com.lomicron.oikoumene.parsers.map.MapParser
import com.lomicron.oikoumene.parsers.politics._
import com.lomicron.oikoumene.parsers.provinces.{BuildingParser, GeographyParser, ProvinceParser}
import com.lomicron.oikoumene.parsers.trade.{TradeGoodParser, TradeNodeParser}
import com.lomicron.oikoumene.repository.api.RepositoryFactory
import com.lomicron.oikoumene.repository.inmemory.InMemoryRepositoryFactory
import com.typesafe.scalalogging.LazyLogging

object Oikoumene extends LazyLogging {

  def main(args: Array[String]) {
    logger.info("Starting the known world...")

    val gameDir = "D:\\Steam\\steamapps\\common\\Europa Universalis IV"
    val modDir = ""

    val repos = InMemoryRepositoryFactory(gameDir, modDir)
    parseConfigs(repos)

    logger.info("Bye")
  }

  def parseConfigs(repos: RepositoryFactory): RepositoryFactory = {
    logger.info("Parsing configs")

    val tags = TagParser(repos)
    logger.info(s"Loaded ${tags.size} tags")
    val buildings = BuildingParser(repos)
    logger.info(s"Loaded ${buildings.size} buildings")
    GeographyParser(repos)
    logger.info(s"Loaded geographical definitions")

    val religions = ReligionParser(repos)
    logger.info(s"Loaded ${religions.size} religions")
    val cultures = CultureParser(repos)
    logger.info(s"Loaded ${cultures.size} cultures")

    val diplomacy = DiplomacyParser(repos)
    logger.info(s"Loaded ${diplomacy.size} historical diplomatic relations")
    val wars = WarHistoryParser(repos)
    logger.info(s"Loaded ${wars.size} historical wars")
    val warGoalTypes = WarGoalTypeParser(repos)
    logger.info(s"Loaded ${warGoalTypes.size} war goal types")
    val cbTypes = CasusBelliParser(repos)
    logger.info(s"Loaded ${cbTypes.size} casus belli configs")

    val ideas = IdeaParser(repos)
    logger.info(s"Loaded ${ideas.size} idea groups")

    val tradeGoods = TradeGoodParser(repos)
    logger.info(s"Loaded ${tradeGoods.size} trade goods")
    val tradeNodes = TradeNodeParser(repos)
    logger.info(s"Loaded ${tradeNodes.size} trade nodes")

    logger.info("Loading map...")
    val geography = MapParser(repos)
    logger.info(s"Loaded ${geography.map.tileRoutes.map(_.source).distinct.size} map provinces")

    logger.info("Loading provinces...")
    val provinces = ProvinceParser(repos)
    logger.info(s"Parsed ${provinces.size} province configs")

    logger.info(s"Configs loaded")
    repos
  }

}
