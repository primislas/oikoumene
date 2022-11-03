package com.lomicron.vicky.engine

import com.lomicron.oikoumene.engine.Oikoumene.logger
import com.lomicron.oikoumene.io.FileIO
import com.lomicron.oikoumene.repository.api.GameFilesSettings
import com.lomicron.oikoumene.tools.ClausewitzMapBuilder
import com.lomicron.oikoumene.tools.map.MapBuilder
import com.lomicron.vicky.repository.inmemory.InMemoryRepositoryFactory
import com.typesafe.scalalogging.LazyLogging
import com.lomicron.utils.collection.CollectionUtils.toOption
import com.lomicron.vicky.model.map.WorldMap
import com.lomicron.vicky.parsers.map.GeographyParser
import com.lomicron.vicky.parsers.{BuildingParser, InventionParser, LocalisationParser, MapParser, PopTypeParser, ProductionTypeParser, ProvinceParser, TradeGoodParser, UnitParser}
import com.lomicron.vicky.parsers.politics.TagParser
import com.lomicron.vicky.repository.api.RepositoryFactory
import com.lomicron.vicky.service.map.SvgMapService

import java.nio.file.Paths

object AgeOfIndustry extends LazyLogging {

  def main(args: Array[String]) {
    logger.info("Starting the known world...")

    val gameDir = "D:\\Steam\\steamapps\\common\\Victoria 2"
    val repos = InMemoryRepositoryFactory(GameFilesSettings(gameDir))
    loadConfigs(repos)

    logger.info("Bye")
  }

  def loadConfigs(repos: RepositoryFactory): RepositoryFactory = {
    // generate handling here
    // load from cache if available

    parseConfigs(repos)
  }

  def parseConfigs(repos: RepositoryFactory): RepositoryFactory = {
    logger.info("Parsing configs...")

    val les = LocalisationParser(repos)
    logger.info(s"Loaded ${les.size} localisation entries")

    val doEvalEntityFields = true
    val dontEvalEntityFields = false

    val tradeGoods = TradeGoodParser(repos, dontEvalEntityFields)
    logger.info(s"Loaded ${tradeGoods.size} buildings")

    val productionTypes = ProductionTypeParser(repos, dontEvalEntityFields)
    logger.info(s"Loaded ${productionTypes.size} production types")

    val buildings = BuildingParser(repos, dontEvalEntityFields)
    logger.info(s"Loaded ${buildings.size} buildings")

    val inventions = InventionParser(repos, dontEvalEntityFields)
    logger.info(s"Loaded ${inventions.size} inventions")

    val units = UnitParser(repos, dontEvalEntityFields)
    logger.info(s"Loaded ${units.size} units")

    val popTypes = PopTypeParser(repos, dontEvalEntityFields)
    logger.info(s"Loaded ${popTypes.size} pop types")

    val tags = TagParser(repos, dontEvalEntityFields)
    logger.info(s"Loaded ${tags.size} tags")

    val geography = GeographyParser(repos, doEvalEntityFields)
    logger.info(s"Loaded ${geography.regions.size} regions")

    val map = MapParser(repos)
    logger.info(s"Loaded ${map.map.mercator.provinces.size} map provinces")

    val provinces = ProvinceParser(repos, dontEvalEntityFields)
    logger.info(s"Loaded ${provinces.size} provinces")

    val svg = SvgMapService(repos).worldSvg(WorldMap(repos))
    FileIO.writeUTF(Paths.get("C:\\Users\\konst\\Desktop"), "vicky.svg", svg)

    repos
  }

  def generateMap(repos: RepositoryFactory): RepositoryFactory = {
//    val svgMap = MapBuilder.buildMap(repos, settings.mapSettings)
//    val fname = "victoria2.svg"
//    val fp = FileIO.writeUTF(Paths.get("."), fname, svgMap)
//    logger.info(s"Stored map to $fp")

    repos
  }

}
