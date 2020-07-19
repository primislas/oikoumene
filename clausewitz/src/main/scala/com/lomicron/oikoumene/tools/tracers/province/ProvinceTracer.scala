package com.lomicron.oikoumene.tools.tracers.province

import java.nio.file.Paths

import com.lomicron.oikoumene.io.FileIO
import com.lomicron.oikoumene.parsers.map.MapParser
import com.lomicron.utils.json.JsonMapper

object ProvinceTracer {

  def main(args: Array[String]): Unit = {
    val settings = parseArgs(args)
    if (args.isEmpty || settings.isHelp)
      printHelp()
    else {
      println(s"Loading ${settings.input}...")
      val img = MapParser.fetchMap(settings.input)
      println("Parsing provinces...")
      val shapes = MapParser.parseProvinceShapes(img).map(_.withPolygon)
      val ps = shapes.flatMap(_.polygon).map(ExportedPolygon(_))
      println(s"Identified ${ps.length} provinces shapes")

      val bs =
        if (settings.includeBorders)
          shapes.flatMap(_.borders).distinct.map(ExportedBorder(_))
        else Seq.empty
      if (settings.includeBorders)
        println(s"Identified ${bs.length} borders")

      val outputFile = Paths.get(settings.output).toFile
      val content = JsonMapper.prettyPrint(ExportedProvs(ps, bs))
      FileIO.write(outputFile, content)
      println(s"Stored provinces to ${settings.output}")
    }
  }

  def printHelp(): Unit = {
    val help =
      """oikoumene province tracer v1.0
        |Convert provinces.bmp to svg polygons and borders written to json.
        |
        |usage: sbt -J-Xmx2G -J-Xss2M "runMain com.lomicron.oikoumene.tools.tracers.province.ProvinceTracer
        |                          [--help|-h] [--input|-i <input_file>]
        |                          [--output|-o <output_file] [--no-borders|-nb]
        |
        |e.g.
        |sbt -J-Xmx2G -J-Xss2M "runMain com.lomicron.oikoumene.tools.tracers.province.ProvinceTracer -i \"D:/Steam/steamapps/common/Europa Universalis IV/map/provinces.bmp\"""".stripMargin
    println(help)
  }

  def parseArgs(args: Array[String]): TracerSettings = parseArgs(args.toList)

  @scala.annotation.tailrec
  def parseArgs(args: List[String] = List.empty, settings: TracerSettings = TracerSettings()): TracerSettings =
    args match {
      case "-h" :: _ | "--help" :: _ => settings.copy(isHelp = true)
      case "-i" :: inputFile :: tail =>
        parseArgs(tail, settings.copy(input = inputFile))
      case "--input" :: inputFile :: tail => parseArgs(tail, settings.copy(input = inputFile))
      case "-o" :: outputFile :: tail => parseArgs(tail, settings.copy(output = outputFile))
      case "--output" :: outputFile :: tail => parseArgs(tail, settings.copy(output = outputFile))
      case "-nb" :: tail => parseArgs(tail, settings.copy(includeBorders = false))
      case "--no-borders" :: tail => parseArgs(tail, settings.copy(includeBorders = false))
      case Nil => settings
      case _ => parseArgs(args.drop(1), settings)
    }

}






