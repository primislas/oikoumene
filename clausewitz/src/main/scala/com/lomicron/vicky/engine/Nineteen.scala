package com.lomicron.vicky.engine

import com.lomicron.oikoumene.repository.api.GameFilesSettings
import com.lomicron.vicky.repository.inmemory.InMemoryRepositoryFactory
import com.typesafe.scalalogging.LazyLogging
import com.lomicron.utils.collection.CollectionUtils.toOption
import com.lomicron.vicky.parsers.{BuildingParser, InventionParser, LocalisationParser}
import com.lomicron.vicky.parsers.politics.TagParser
import com.lomicron.vicky.repository.api.RepositoryFactory

object Nineteen extends LazyLogging {

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

    val buildings = BuildingParser(repos, doEvalEntityFields)
    logger.info(s"Loaded ${buildings.size} buildings")

    val inventions = InventionParser(repos, doEvalEntityFields)
    logger.info(s"Loaded ${inventions.size} inventions")

    val tags = TagParser(repos, doEvalEntityFields)
    logger.info(s"Loaded ${tags.size} tags")


    repos
  }

}
