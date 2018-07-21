package com.lomicron.oikoumene.engine

import java.nio.file.Paths

import com.lomicron.oikoumene.model.map.Tile
import com.lomicron.oikoumene.parsers.{ClausewitzParser, TagParser}
import com.lomicron.oikoumene.repository.api.{LocalisationRepository, ResourceRepository, TagRepository}
import com.lomicron.oikoumene.repository.fs.FileResourceRepository
import com.lomicron.oikoumene.repository.inmemory.{InMemoryLocalisationRepository, InMemoryTagRepository}
import com.lomicron.utils.collection.CollectionUtils._
import com.typesafe.scalalogging.LazyLogging

import scala.collection.immutable.TreeMap

object Oikoumene extends LazyLogging {

  val gameDir = "D:\\Steam\\steamapps\\common\\Europa Universalis IV"
  val modDir = ""

  def main(args: Array[String]) {
    logger.info("Starting the known world...")
    //println(System.getProperty("user.dir"))
    val files = FileResourceRepository(gameDir, modDir)
    val localisation = InMemoryLocalisationRepository(files)
    val tagRepo: TagRepository = new InMemoryTagRepository

    val tags = loadTags(files, tagRepo, localisation)

    logger.info("Bye")
  }

  def loadMap(): Seq[Tile] = {
    logger.info("Loading provinces...")
    val rootDir = System.getProperty("user.dir")
    val relativeMapPath = "./clausewitz/resources/provinces.bmp"
    val mapPath = Paths.get(rootDir, relativeMapPath)
    val map = MapLoader.loadMap(mapPath).get
    val tiles = map._1
    val routes = map._2
    logger.info("Loaded " + tiles.size + " tiles, :" + tiles)
    val l: List[Int] = Nil
    tiles
  }

  def loadTags
  (files: ResourceRepository,
   tags: TagRepository,
   localisation: LocalisationRepository
  ) : TagRepository = {
    val filesByTags = files
      .getCountryTags
      .map(contentsByFile => ClausewitzParser.parse(contentsByFile._2)._1)
      .flatMap(obj => obj.fields.toStream.map(e => (e.getKey, e.getValue.asText)))
      .map(kv => (kv._1, s"common/${kv._2}"))
      .foldLeft(TreeMap[String, String]())(_ + _)
    val countries = files.getCountries(filesByTags)
    val histories = files.getCountryHistory
    val names = localisation.fetchTags
    val parsedTags = TagParser(filesByTags, countries, histories, names)
    tags.create(parsedTags.values.toSeq)
    tags
  }


}