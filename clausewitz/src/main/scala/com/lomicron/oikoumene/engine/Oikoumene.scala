package com.lomicron.oikoumene.engine

import java.nio.file.Paths

import com.lomicron.oikoumene.model.map.Tile
import com.lomicron.oikoumene.parsers.diplomacy.{CasusBelliParser, DiplomacyParser, WarGoalTypeParser, WarHistoryParser}
import com.lomicron.oikoumene.parsers.government.IdeaParser
import com.lomicron.oikoumene.parsers.politics._
import com.lomicron.oikoumene.parsers.provinces.{BuildingParser, GeographyParser, ProvinceParser}
import com.lomicron.oikoumene.repository.inmemory.InMemoryRepositoryFactory
import com.typesafe.scalalogging.LazyLogging

object Oikoumene extends LazyLogging {

  def main(args: Array[String]) {
    logger.info("Starting the known world...")

    val gameDir = "D:\\Steam\\steamapps\\common\\Europa Universalis IV"
    val modDir = ""

    val repos = InMemoryRepositoryFactory(gameDir, modDir)

    val tags = TagParser(repos)
    val buildings = BuildingParser(repos)
    val provinces = ProvinceParser(repos)
    val geography = GeographyParser(repos)
    val religions = ReligionParser(repos)
    val cultures = CultureParser(repos)
    val diplomacy = DiplomacyParser(repos)
    val wars = WarHistoryParser(repos)
    val warGoalTypes = WarGoalTypeParser(repos)
    val cbTypes = CasusBelliParser(repos)
    val ideas = IdeaParser(repos)

    logger.info("Bye")
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