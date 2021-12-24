package com.lomicron.eu4.engine

import com.lomicron.eu4.parsers.diplomacy.{CasusBelliParser, DiplomacyParser, WarGoalTypeParser, WarHistoryParser}
import com.lomicron.eu4.parsers.government._
import com.lomicron.eu4.parsers.localisation.LocalisationParser
import com.lomicron.eu4.parsers.map.MapParser
import com.lomicron.eu4.parsers.modifiers.ModifierParser
import com.lomicron.eu4.parsers.politics._
import com.lomicron.eu4.parsers.provinces.{BuildingParser, GeographyParser, ProvinceParser}
import com.lomicron.eu4.parsers.trade.{CenterOfTradeParser, TradeGoodParser, TradeNodeParser}
import com.lomicron.eu4.repository.api.{GameFilesSettings, RepositoryFactory}
import com.lomicron.eu4.repository.fs.CacheReader
import com.lomicron.eu4.repository.inmemory.InMemoryRepositoryFactory
import com.lomicron.eu4.service.government.TagService
import com.lomicron.eu4.service.province.ProvinceService
import com.lomicron.utils.collection.CollectionUtils.toOption
import com.typesafe.scalalogging.LazyLogging

object Oikoumene extends LazyLogging {

  def main(args: Array[String]): Unit = {
    logger.info("Starting the known world...")

    val gameDir = "D:\\Steam\\steamapps\\common\\Europa Universalis IV"
    val repos = InMemoryRepositoryFactory(GameFilesSettings(gameDir))
    loadConfigs(repos)

    logger.info("Bye")
  }

  def loadConfigs(repos: RepositoryFactory): RepositoryFactory = {
    val cacheIsConfigured = repos.settings.cacheDir.isDefined
    val cached = CacheReader(repos).load
    if (cached.isEmpty) {
      val parsed = parseConfigs(repos)
      if (cacheIsConfigured)
        parsed.storeToCache
      else
        parsed
    } else
      cached.get
  }

  def parseConfigs(repos: RepositoryFactory): RepositoryFactory = {
    logger.info("Parsing configs...")

    val les = LocalisationParser(repos)
    logger.info(s"Loaded ${les.size} localisation entries")

    //    ModifierAnalyzer(repos)

    val tags = TagParser(repos)
    logger.info(s"Loaded ${tags.size} tags")
    val buildings = BuildingParser(repos)
    logger.info(s"Loaded ${buildings.size} buildingIds")
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

    val govs = GovernmentParser(repos)
    logger.info(s"Loaded ${govs.size} governments")
    val govReforms = GovernmentReformParser(repos)
    logger.info(s"Loaded ${govReforms.size} government reforms")
    val tech = TechnologyParser(repos)
    logger.info(s"Loaded ${tech.groups.size} tech groups")
    val ideas = IdeaParser(repos)
    logger.info(s"Loaded ${ideas.size} idea groups")
    val policies = PolicyParser(repos)
    logger.info(s"Loaded ${policies.size} policies")
    val rulerPersonalities = RulerPersonalityParser(repos)
    logger.info(s"Loaded ${rulerPersonalities.size} ruler personalities")
    val edicts = StateEdictParser(repos)
    logger.info(s"Loaded ${edicts.size} state edicts")

    val tradeGoods = TradeGoodParser(repos)
    logger.info(s"Loaded ${tradeGoods.size} trade goods")
    val tradeNodes = TradeNodeParser(repos)
    logger.info(s"Loaded ${tradeNodes.size} trade nodes")
    val centersOfTrade = CenterOfTradeParser(repos)
    logger.info(s"Loaded ${centersOfTrade.size} center of trade configs")

    logger.info("Loading event modifiers...")
    val eventModifiers = ModifierParser(repos)
    logger.info(s"Loaded ${eventModifiers.size} event modifiers")

    logger.info("Loading map...")
    val geography = MapParser(repos)
    logger.info(s"Loaded ${geography.map.mercator.provinces.size} map provinces")

    logger.info("Loading province configs...")
    val provinces = ProvinceParser(repos)
    logger.info(s"Loaded ${provinces.size} province configs")

    logger.info(s"Configs loaded")
    repos
  }

  def initModifiers(repos: RepositoryFactory): RepositoryFactory = {
    val tagService = TagService(repos)
    val tags = repos.tags
    tags.findAll.map(tagService.init).foreach(tags.update)
    logger.info("Tag modifiers initialized")

    val provinces = repos.provinces
    val provService = ProvinceService(repos)
    provinces.findAll
      .map(provService.init)
      .foreach(provinces.update)
    logger.info("Province modifiers initialized")

    repos
  }

}
