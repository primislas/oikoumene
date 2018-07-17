package com.lomicron.oikoumene.engine

import java.nio.file.Paths

import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.oikoumene.model.map.Tile
import com.lomicron.oikoumene.parsers.{ClausewitzParser, TagParser}
import com.lomicron.oikoumene.repository.fs.FileResourceRepository

import scala.collection.immutable.TreeMap
import scala.collection.mutable.ListBuffer

object Oikoumene {

  val gameDir = "D:\\Steam\\steamapps\\common\\Europa Universalis IV"
  val modDir = ""

  def main(args: Array[String]) {
    println("Starting the known world...")
    //println(System.getProperty("user.dir"))

    loadTags(gameDir, modDir)

    println("Bye")
  }

  def loadMap(): Seq[Tile] = {
    println("Loading provinces...")
    val rootDir = System.getProperty("user.dir")
    val relativeMapPath = "./clausewitz/resources/provinces.bmp"
    val mapPath = Paths.get(rootDir, relativeMapPath)
    val map = MapLoader.loadMap(mapPath).get
    val tiles = map._1
    val routes = map._2
    println("Loaded " + tiles.size + " tiles, :" + tiles)
    val l: List[Int] = Nil
    tiles
  }

  def loadTags(sourceDir: String, modDir: String): Seq[ObjectNode] = {
    val files = FileResourceRepository(sourceDir, modDir)
    val filesByTags = files
      .getCountryTags
      .map(contentsByFile => ClausewitzParser.parse(contentsByFile._2)._1)
      .flatMap(obj => {
        var fsByTags = new ListBuffer[(String, String)]()
        obj.fields().forEachRemaining(e => fsByTags.+=((e.getKey, e.getValue.asText())))
        fsByTags
      })
      .map(kv => (kv._1, s"common/${kv._2}"))
      .foldLeft(TreeMap[String, String]())(_ + _)

    def filenameToTag(kv: (String, String)) =
      (kv._1.take(3).mkString, kv._2)

    val countries = files
      .getCountries(filesByTags)

    val histories = files.getCountryHistory
    val names = files.getCountryNames

    TagParser(filesByTags, countries, histories, names)
  }


}