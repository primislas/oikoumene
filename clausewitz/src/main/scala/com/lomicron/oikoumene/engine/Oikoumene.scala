package com.lomicron.oikoumene.engine

import java.nio.file.Paths

import com.lomicron.oikoumene.model.map.Tile
import com.lomicron.oikoumene.parsers.diplomacy.{CasusBelliParser, DiplomacyParser, WarGoalTypeParser, WarHistoryParser}
import com.lomicron.oikoumene.parsers.government.IdeaParser
import com.lomicron.oikoumene.parsers.politics._
import com.lomicron.oikoumene.parsers.provinces.{BuildingParser, GeographyParser, ProvinceParser}
import com.lomicron.oikoumene.repository.api.RepositoryFactory
import com.lomicron.oikoumene.repository.inmemory.InMemoryRepositoryFactory
import com.typesafe.scalalogging.LazyLogging

object Oikoumene extends LazyLogging {

  def main(args: Array[String]) {
    logger.info("Starting the known world...")

    val gameDir = "D:\\Steam\\steamapps\\common\\Europa Universalis IV"
    val modDir = ""

    val repos = InMemoryRepositoryFactory(gameDir, modDir)
    populateRepos(repos)

    logger.info("Bye")
  }

  def populateRepos(repos: RepositoryFactory): RepositoryFactory = {
    logger.info("Parsing configs...")
    val tags = TagParser(repos)
    logger.info(s"Loaded ${tags.size} tags...")
    val buildings = BuildingParser(repos)
    logger.info(s"Loaded ${buildings.size} buildings...")
    GeographyParser(repos)
    logger.info(s"Loaded geographical definitions...")
    val religions = ReligionParser(repos)
    logger.info(s"Loaded ${religions.size} religions...")
    val cultures = CultureParser(repos)
    logger.info(s"Loaded ${cultures.size} cultures...")
    val diplomacy = DiplomacyParser(repos)
    logger.info(s"Loaded ${diplomacy.size} historical diplomatic relations...")
    val wars = WarHistoryParser(repos)
    logger.info(s"Loaded ${wars.size} historical wars...")
    val warGoalTypes = WarGoalTypeParser(repos)
    logger.info(s"Loaded ${warGoalTypes.size} war goal types...")
    val cbTypes = CasusBelliParser(repos)
    logger.info(s"Loaded ${cbTypes.size} casus belli configs...")
    val ideas = IdeaParser(repos)
    logger.info(s"Loaded ${ideas.size} idea groups...")
    val provinces = ProvinceParser(repos)
    logger.info(s"Loaded ${provinces.size} provinces...")

    logger.info(s"Configs loaded")
    repos
  }

  private def loadMap(): Seq[Tile] = {
    logger.info("Loading provinces...")
    val rootDir = System.getProperty("user.dir")
    val relativeMapDir = "./clausewitz/resources/"
    val mapDir = Paths.get(rootDir, relativeMapDir)
    val map = MapLoader.loadMap(mapDir).get
    val tiles = map._1
    val routes = map._2
    logger.info("Loaded " + tiles.size + " tiles, :" + tiles)
    val l: List[Int] = Nil
    tiles
  }




}