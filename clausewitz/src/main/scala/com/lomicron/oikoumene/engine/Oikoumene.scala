package com.lomicron.oikoumene.engine

import java.nio.file.Paths

import com.lomicron.utils.io.IO
import com.lomicron.utils.parsing._

object Oikoumene {
  def main(args: Array[String]) {
    println("Starting the known world...")
    //println(System.getProperty("user.dir"))
    println("Loading provinces...")
    val rootDir = System.getProperty("user.dir")
    val relativeMapPath = "./clausewitz/resources/provinces.bmp"
    val mapPath = Paths.get(rootDir, relativeMapPath)
    val map = MapLoader.loadMap(mapPath).get
    val tiles = map._1
    val routes = map._2
    println("Loaded " + tiles.size + " tiles, :" + tiles)
    val l: List[Int] = Nil
    
    val s = IO.readTextFile("./clausewitz/resources/151 - Constantinople.txt")
    val json = ClausewitzParser.parse(s)._1
    val endDate = Date(1844,1,1)
    val province = ClausewitzParser.rollUpEvents(json, endDate)

    val isIdentifier: Token => Boolean = _.isInstanceOf[Identifier]
    println("Bye")
  }

}