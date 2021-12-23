package com.lomicron.eu4.tools

import java.nio.file.{Path, Paths}

import com.fasterxml.jackson.databind.node.{ArrayNode, ObjectNode}
import com.lomicron.eu4.io.FileIO
import com.lomicron.eu4.parsers.save.SaveGameParser
import com.lomicron.utils.collection.CollectionUtils.toOption
import com.lomicron.utils.json.JsonMapper
import com.lomicron.utils.json.JsonMapper.{ArrayNodeEx, JsonNodeEx, ObjectNodeEx, objectNode}
import com.softwaremill.quicklens._
import com.typesafe.scalalogging.LazyLogging

object SaveGameToJson extends LazyLogging {

  val nonExportedObjects = Set("id", "fired_events", "flags", "gameplaysettings",
    "idea_dates", "saved_event_target", "tech_level_dates")
  val exportedArrays = Set("provinces", "countries", "active_war", "previous_war",
    "rebel_faction", "trade_league")

  def main(args: Array[String]): Unit = {
    val settings = parseArgs(args)
    if (args.isEmpty || settings.isHelp)
      printHelp()
    else {
      val saveExists = settings.save.map(Paths.get(_)).map(_.toFile).exists(_.exists())
      if (!saveExists) {
        logger.error("Specified save game file was not found.")
        logger.info("Exiting...")
      } else {
        parseSave(settings)
          .foreach(saveDir => logger.info(s"Ready: $saveDir"))
      }
    }
  }

  def printHelp(): Unit = {
    val help =
      """help:
        |eu4 save game parser v1.0
        |Convert .eu4 save file to json for easier navigation.
        |
        |usage: sbt "runMain com.lomicron.eu4.tools.SaveGameToJson
        |             [--help|-h]
        |             [--save-game|-save <save_file_path>]
        |             [--output-dir|-od <output_dir>]
        |
        |e.g.
        |sbt -J-Xmx3G -J-Xss3M "runMain com.lomicron.eu4.tools.SaveGameToJson -save \"C:/Users/username/Documents/Paradox Interactive/Europa Universalis IV/save games/autosave.eu4\" -od \"C:/Users/username/Documents/Paradox Interactive/Europa Universalis IV/mod/parsed_saves\""
        |
        |""".stripMargin
    logger.info(help)
  }

  def parseArgs(args: Array[String]): CLMapBuilderSettings = parseArgs(args.toList)

  @scala.annotation.tailrec
  def parseArgs(args: List[String] = List.empty, settings: CLMapBuilderSettings = CLMapBuilderSettings()): CLMapBuilderSettings =
    args match {
      case "-h" :: _ | "--help" :: _ => settings.copy(isHelp = true)
      case "-od" :: outputFile :: tail => parseArgs(tail, settings.copy(outputDir = outputFile))
      case "--output-dir" :: outputFile :: tail => parseArgs(tail, settings.copy(outputDir = outputFile))
      case "-save" :: save :: tail =>
        parseArgs(tail, settings.modify(_.save).setTo(save))
      case "--save-game" :: save :: tail =>
        parseArgs(tail, settings.modify(_.save).setTo(save))

      case Nil => settings
      case _ => parseArgs(args.drop(1), settings)
    }

  def parseSave(settings: CLMapBuilderSettings): Option[Path] = {
    val fpath = settings.save.get
    val fname = Paths.get(fpath).toFile.getName
      .replaceAll("\\.eu4", "")
      .replaceAll("\\.", "")
    FileIO
      .readSave(fpath)
      .map(SaveGameParser.toObjectNode)
      .map(state => {
        seqToMapById(state, "provinces")
        seqToMapById(state, "countries")

        val exported = state.entries()
          .filter(e =>
            (e._2.isObject && !nonExportedObjects.contains(e._1)) ||
              (e._2.isArray && exportedArrays.contains(e._1))
          )

        val od = settings.outputDir.getOrElse("./target")
        val saveDir = Paths.get(od).resolve(fname)
        FileIO.ensureDirsExist(saveDir)
        exported.foreach(e => {
          val (key, node) = e
          state.remove(key)
          FileIO.writeUTF(saveDir, s"$key.json", JsonMapper.prettyPrint(node))
          logger.info(s"--> $key.json")
        })

        FileIO.writeUTF(saveDir, "gamestate.json", JsonMapper.prettyPrint(state))
        logger.info("--> gamestate.json")
        saveDir
      })
  }

  def seqToMapById(state: ObjectNode, field: String): ObjectNode =
    state.getArray(field)
      .map(groupById)
      .map(state.setEx(field, _))
      .getOrElse(state)

  def groupById(a: ArrayNode): ObjectNode =
    a.toSeq
      .sortBy(_.getString("id"))
      .foldLeft(objectNode)((acc, n) =>
        n.asObject
          .flatMap(_.getNode("id"))
          .map(id => acc.setEx(id.asText(), n))
          .getOrElse(acc)
      )

}
