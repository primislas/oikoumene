package com.lomicron.imperator.engine

import com.lomicron.eu4.io.FileIO
import com.lomicron.eu4.repository.api.GameFilesSettings
import com.lomicron.imperator.model.map.WorldMap
import com.lomicron.imperator.parsers.SetupParser
import com.lomicron.imperator.parsers.localisation.ImperatorLocalisationParser
import com.lomicron.imperator.parsers.map.{GeographyParser, MapParser}
import com.lomicron.imperator.parsers.politics.ImperatorTagParser
import com.lomicron.imperator.parsers.provinces.ProvinceSetupParser
import com.lomicron.imperator.repository.api.RepositoryFactory
import com.lomicron.imperator.repository.inmemory.InMemoryRepositoryFactory
import com.lomicron.imperator.service.svg.SvgMapService
import com.lomicron.utils.collection.CollectionUtils.toOption
import com.typesafe.scalalogging.LazyLogging

import java.nio.file.Paths

object Imperator extends LazyLogging {

  def main(args: Array[String]): Unit = {
    logger.info("Starting the known world...")

    val gameDir = "D:\\Steam\\steamapps\\common\\ImperatorRome"
    val repos = InMemoryRepositoryFactory(GameFilesSettings(gameDir))
    loadConfigs(repos)

    logger.info("Bye")
  }

  def loadConfigs(repos: RepositoryFactory): RepositoryFactory = {
    parseConfigs(repos)
  }

  def parseConfigs(repos: RepositoryFactory): RepositoryFactory = {
    logger.info("Parsing configs...")

    val les = ImperatorLocalisationParser(repos)
    logger.info(s"Loaded ${les.size} localisation entries")

    val tags = ImperatorTagParser(repos)
    logger.info(s"Parsed ${tags.size} tags")

    val provinces = ProvinceSetupParser(repos)
    logger.info(s"Parsed ${provinces.size} provinces")

    SetupParser(repos)

    val geography = MapParser(repos)
    logger.info(s"Parsed world map: ${geography.map.findAll.size} tiles")

    GeographyParser(repos)

    val svgService = SvgMapService(repos)
    val mapSvg = svgService.worldSvg(WorldMap(repos))
    val fname = "imperator_political.svg"
    FileIO.writeUTF(Paths.get("."), fname, mapSvg)
    logger.info(s"Produced $fname")

    logger.info(s"Configs loaded")
    repos
  }


}
