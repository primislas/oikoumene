package com.lomicron.eu4.tools

import com.lomicron.eu4.engine.Oikoumene
import com.lomicron.eu4.io.FileIO
import com.lomicron.eu4.parsers.map.MapParser
import com.lomicron.eu4.parsers.save.SaveGameParser
import com.lomicron.eu4.repository.api.RepositoryFactory
import com.lomicron.eu4.repository.api.resources.ResourceRepository
import com.lomicron.eu4.repository.fs.FileResourceRepository
import com.lomicron.eu4.repository.inmemory.InMemoryRepositoryFactory
import com.lomicron.eu4.service.map.SvgMapService
import com.lomicron.eu4.tools.map.MapBuilder
import com.lomicron.eu4.tools.model.SupportedGames
import com.lomicron.eu4.tools.model.map.{BorderSvgJson, PolygonSvgJson}
import com.lomicron.eu4.tools.model.metadata._
import com.lomicron.oikoumene.model.Color
import com.lomicron.oikoumene.parsers.map.MapConfigParser
import com.lomicron.utils.collection.CollectionUtils.{SeqEx, toOption}
import com.lomicron.utils.json.JsonMapper.prettyPrint
import com.softwaremill.quicklens._
import com.typesafe.scalalogging.LazyLogging

import java.nio.file.{Path, Paths}
import scala.util.Try

object ClausewitzMapBuilder extends LazyLogging {

  val mapPolygonsJson = "map-polygons.json"
  val bordersJson = "borders.json"
  val provsJson = "provinces.json"
  val tagsJson = "tags.json"
  val culturesJson = "cultures.json"
  val cultureGroupsJson = "culture-groups.json"
  val religionsJson = "religion.json"
  val religionGroupsJson = "religion-groups.json"

  def main(args: Array[String]): Unit = {
    val settings = parseArgs(args)
    if (args.isEmpty || settings.isHelp)
      printHelp()
    else {
      if (settings.fileSettings.gameDir.isEmpty) {
        logger.error("Game directory has not been configured. Rerun tracer with -gd or --game-dir parameter.")
        logger.info("Exiting...")
      } else {
        val repo = initMetadata(settings)
        generateMapOutlines(settings, repo)
        generateMetadata(settings, repo)
        generateMap(settings, repo)
      }
    }
  }

  def printHelp(): Unit = {
    val help =
      """help:
        |eu4 province tracer v1.0
        |Convert provinces.bmp to svg polygons and borders written to json.
        |
        |usage: sbt "runMain com.lomicron.eu4.tools.ClausewitzMapBuilder
        |             [--help|-h]
        |             [--game-dir|-gd <game_dir_path>]
        |             [--mod-dir|-md <mod_dir_path>]
        |             [--mod|-m <mod>]
        |             [--cache-dir|-cd <cache_dir_pat>]
        |             [--rebuild-cache|-rc]
        |             [--save-game|-save <save_file_path>]
        |             [--output-dir|-od <output_dir>]
        |             [--no-borders|-nb]
        |             [--no-rivers|-nr]
        |             [--no-names|-nn]
        |             [--no-wastelands|-nw]
        |             [--meta|-meta eu4]
        |             [--svg|-svg eu4]
        |             [--mode|-mode political|terrain|province_outline]
        |             [--background|-bg autumn|winter|spring|summer]
        |
        |e.g.
        |simply parse provinces.bmp into svg shape and border jsons (add "-meta eu4" to parse additional metadata to jsons)
        |sbt -J-Xmx3G -J-Xss3M "runMain com.lomicron.eu4.tools.ClausewitzMapBuilder -gd \"D:/Steam/steamapps/common/Europa Universalis IV\""
        |
        |generate a province outline map:
        |sbt -J-Xmx3G -J-Xss3M "runMain com.lomicron.eu4.tools.ClausewitzMapBuilder -gd \"D:/Steam/steamapps/common/Europa Universalis IV\" -cd \"C:/Users/username/Documents/Paradox Interactive/Europa Universalis IV/mod/map_rendering/cmb_cache_base_game\" -svg eu4 -mode province_outline"
        |
        |generate a political map from a save:
        |sbt -J-Xmx3G -J-Xss3M "runMain com.lomicron.eu4.tools.ClausewitzMapBuilder -gd \"D:/Steam/steamapps/common/Europa Universalis IV\" -md \"C:/Users/username/Documents/Paradox Interactive/Europa Universalis IV/mod\" -m rus -m balkans -m anatolia -m mashriq -cd \"C:/Users/username/Documents/Paradox Interactive/Europa Universalis IV/mod/map_rendering/cmb_cache_my_mods\" -svg eu4 -mode province_outline -save \"C:/Users/username/Documents/Paradox Interactive/Europa Universalis IV/save games/autosave.eu4\""
        |
        |""".stripMargin
    logger.info(help)
  }

  def parseArgs(args: Array[String]): CLMapBuilderSettings = parseArgs(args.toList)

  @scala.annotation.tailrec
  def parseArgs(args: List[String] = List.empty, settings: CLMapBuilderSettings = CLMapBuilderSettings()): CLMapBuilderSettings =
    args match {
      case "-h" :: _ | "--help" :: _ => settings.copy(isHelp = true)
      case "-i" :: inputFile :: tail =>
        parseArgs(tail, settings.copy(input = inputFile))
      case "--input" :: inputFile :: tail => parseArgs(tail, settings.copy(input = inputFile))
      case "-od" :: outputFile :: tail => parseArgs(tail, settings.copy(outputDir = outputFile))
      case "--output-dir" :: outputFile :: tail => parseArgs(tail, settings.copy(outputDir = outputFile))
      case "-meta" :: engine :: tail => parseArgs(tail, settings.copy(meta = engine))
      case "--metadata" :: engine :: tail => parseArgs(tail, settings.copy(meta = engine))


      case "-gd" :: gameDir :: tail =>
        parseArgs(tail, settings.modify(_.fileSettings.gameDir).setTo(gameDir))
      case "--game-dir" :: gameDir :: tail =>
        parseArgs(tail, settings.modify(_.fileSettings.gameDir).setTo(gameDir))
      case "-md" :: modDir :: tail =>
        parseArgs(tail, settings.modify(_.fileSettings.modDir).setTo(modDir))
      case "--mod-dir" :: modDir :: tail =>
        parseArgs(tail, settings.modify(_.fileSettings.modDir).setTo(modDir))
      case "-m" :: mod :: tail => parseArgs(tail, settings.addMod(mod))
      case "--mod" :: mod :: tail => parseArgs(tail, settings.addMod(mod))
      case "-cd" :: cacheDir :: tail =>
        parseArgs(tail, settings.modify(_.fileSettings.cacheDir).setTo(cacheDir))
      case "--cache-dir" :: cacheDir :: tail =>
        parseArgs(tail, settings.modify(_.fileSettings.cacheDir).setTo(cacheDir))
      case "-rc" :: tail =>
        parseArgs(tail, settings.modify(_.fileSettings.rebuildCache).setTo(true))
      case "--rebuild-cache" :: tail =>
        parseArgs(tail, settings.modify(_.fileSettings.rebuildCache).setTo(true))
      case "-save" :: save :: tail =>
        parseArgs(tail, settings.modify(_.save).setTo(save))
      case "--save-game" :: save :: tail =>
        parseArgs(tail, settings.modify(_.save).setTo(save))


      case "-mode" :: mapMode :: tail =>
        parseArgs(tail, settings.modify(_.mapSettings.mapMode).setTo(mapMode))
      case "--map-mode" :: mapMode :: tail =>
        parseArgs(tail, settings.modify(_.mapSettings.mapMode).setTo(mapMode))
      case "-nw" :: tail =>
        parseArgs(tail, settings.modify(_.mapSettings.ownWastelands).setTo(false))
      case "--no-wastelands" :: tail =>
        parseArgs(tail, settings.modify(_.mapSettings.ownWastelands).setTo(false))
      case "-nn" :: tail =>
        parseArgs(tail, settings.modify(_.mapSettings.includeNames).setTo(false))
      case "--no-names" :: tail =>
        parseArgs(tail, settings.modify(_.mapSettings.includeNames).setTo(false))
      case "-nb" :: tail =>
        parseArgs(tail, settings.modify(_.mapSettings.includeBorders).setTo(false))
      case "--no-borders" :: tail =>
        parseArgs(tail, settings.modify(_.mapSettings.includeBorders).setTo(false))
      case "-svg" :: engine :: tail =>
        parseArgs(tail, settings.modify(_.mapSettings.svg).setTo(engine))
      case "--svg" :: engine :: tail =>
        parseArgs(tail, settings.modify(_.mapSettings.svg).setTo(engine))
      case "-bg" :: season :: tail =>
        parseArgs(tail, settings.modify(_.mapSettings.svgBackground).setTo(season))
      case "--background" :: season :: tail =>
        parseArgs(tail, settings.modify(_.mapSettings.svgBackground).setTo(season))


      case Nil => settings
      case _ => parseArgs(args.drop(1), settings)
    }

  def generateMapOutlines(settings: CLMapBuilderSettings, repo: RepositoryFactory)
  : (Seq[PolygonSvgJson], Seq[BorderSvgJson]) = {
    if (settings.mapSettings.svg.isEmpty || settings.meta.contains(SupportedGames.eu4)) {
      val r = repo.resources
      val inputFile = getProvincesBmp(r)
      val mercator = repo.geography.map.mercator
      val (ps, bs) = if (mercator.borders.isEmpty)
        parseProvinceShapes(inputFile, settings.mapSettings.withBorders)
      else
        (
          mercator.provinces.flatMap(_.polygon).map(PolygonSvgJson(_)),
          mercator.borders.map(BorderSvgJson(_))
        )
      val (psid, bsid) = parseProvinceIds(r, ps, bs)
      val (psmeta, bsmeta) = addSvgClasses(settings, repo, psid, bsid)
      writeProvinces(settings, psmeta)
      writeBorders(settings, bsmeta)
      (psmeta, bsmeta)
    } else (Seq.empty, Seq.empty)
  }

  def initMetadata(settings: CLMapBuilderSettings): RepositoryFactory = {
    val emptyRepos = InMemoryRepositoryFactory(settings.fileSettings)
    val loadConfigs =
      settings.meta.contains(SupportedGames.eu4) ||
        settings.mapSettings.svg.contains(SupportedGames.eu4)

    if (loadConfigs) Oikoumene.loadConfigs(emptyRepos)
    else emptyRepos
  }

  def generateMetadata(settings: CLMapBuilderSettings, repos: RepositoryFactory): RepositoryFactory = {
    settings.meta match {
      case Some(SupportedGames.eu4) =>

        val provs = repos.provinces.findAll.map(ProvinceMetadata(_))
        val tags = repos.tags.findAll.map(TagMetadata(_))
        val religions = repos.religions.findAll.map(ReligionMetadata(_))
        val rGroups = repos.religions.findAllGroups.map(ReligionGroupMetadata(_))
        val cultures = repos.cultures.findAll.map(CultureMetadata(_))
        val cGroups = repos.cultures.findAllGroups.map(CultureGroupMetadata(_))

        writeMetadata(settings, provs, "provinces", provsJson)
        writeMetadata(settings, tags, "tags", tagsJson)
        writeMetadata(settings, religions, "religions", religionsJson)
        writeMetadata(settings, rGroups, "religion_groups", religionGroupsJson)
        writeMetadata(settings, cultures, "cultures", culturesJson)
        writeMetadata(settings, cGroups, "culture_groups", cultureGroupsJson)

        repos
      case _ => repos
    }
  }

  def generateMap(settings: CLMapBuilderSettings, repos: RepositoryFactory): RepositoryFactory = {
    settings.mapSettings.svg match {
      case Some(SupportedGames.eu4) =>
        val save = settings
          .save
          .flatMap(save => {
            val p = Paths.get(save)
            val f = p.toFile
            val fname = f.getName
            if (!f.exists()) {
              logger.info(s"Save file not found, omitting: $save")
              None
            } else {
              FileIO
                .readSave(save)
                .map(SaveGameParser(_))
                .map(state => (fname, state))
            }
          })
        val saveFileName = save.map(_._1)
        val saveGame = save.map(_._2)

        val svgMap = MapBuilder.buildMap(repos, settings.mapSettings, saveGame)
        val fname = mapName(settings.mapSettings.mapMode, saveFileName)
        val fp = writeUTF(settings, fname, svgMap)
        logger.info(s"Stored map to $fp")
      case _ =>
    }

    repos
  }

  def mapName(mode: String, saveFileName: Option[String] = None): String =
    saveFileName
      .map(_.replaceAll("\\.eu4", ""))
      .map(fname => s"eu4_${fname}_$mode.svg")
      .getOrElse(s"eu4_$mode.svg")

  def initRepo(settings: CLMapBuilderSettings): FileResourceRepository =
    FileResourceRepository(settings.fileSettings)

  def getProvincesBmp(repo: ResourceRepository): Path =
    repo.getProvinceMap.get.path

  def parseProvinceShapes(inputFile: Path, includeBorders: Boolean)
  : (Seq[PolygonSvgJson], Seq[BorderSvgJson]) = {

    logger.info(s"Loading $inputFile...")
    val img = MapParser.fetchMap(inputFile)
    logger.info("Parsing provinces...")
    val shapes = MapParser.parseProvinceShapes(img).map(_.withPolygon)
    val ps = shapes.flatMap(_.polygon).map(PolygonSvgJson(_))
    logger.info(s"Identified ${ps.length} provinces shapes")

    val bs =
      if (includeBorders)
        shapes.flatMap(_.borders).distinct.map(BorderSvgJson(_))
      else Seq.empty
    if (includeBorders)
      logger.info(s"Identified ${bs.length} borders")

    (ps, bs)
  }

  def parseProvinceIds(repo: ResourceRepository, polygons: Seq[PolygonSvgJson], borders: Seq[BorderSvgJson])
  : (Seq[PolygonSvgJson], Seq[BorderSvgJson]) = {
    val defsFile = repo.getProvinceDefinitions
    val defs = MapConfigParser
      .parseProvinceDefinitions(defsFile)
      .toMapEx(p => p.color -> p)

    def idOfColor(c: Color): Option[Int] = defs.get(c).map(_.id)

    val ps = polygons.map(poly => poly.color.flatMap(idOfColor).map(poly.setId).getOrElse(poly))
    val bs = borders.map(b => {
      val leftId = b.left.flatMap(idOfColor)
      val rightId = b.right.flatMap(idOfColor)
      var withIds = b
      withIds = leftId.map(withIds.setLeftProv).getOrElse(withIds)
      withIds = rightId.map(withIds.setRightProv).getOrElse(withIds)
      withIds
    })

    (ps, bs)
  }

  def addSvgClasses
  (
    settings: CLMapBuilderSettings,
    repos: RepositoryFactory,
    polygons: Seq[PolygonSvgJson],
    borders: Seq[BorderSvgJson]
  ): (Seq[PolygonSvgJson], Seq[BorderSvgJson]) =
    if (!settings.meta.contains("eu4")) (polygons, borders)
    else {
      val service = SvgMapService(repos)
      val ps = polygons
        .map(p => p.provId
          .map(service.classesOfProvince(_, settings.mapSettings.mapMode))
          .map(p.addClasses)
          .getOrElse(p)
        )
      val bs = borders.map(b => b.addClass(service.borderBetweenProvIds(b.lProv, b.rProv)))
      (ps, bs)
    }

  def writeProvinces(settings: CLMapBuilderSettings, polygons: Seq[PolygonSvgJson]): Unit =
    writeMetadata(settings, polygons, "polygons", mapPolygonsJson)

  def writeBorders(settings: CLMapBuilderSettings, borders: Seq[BorderSvgJson]): Unit =
    if (settings.mapSettings.withBorders)
      writeMetadata(settings, borders, "borders", bordersJson)

  def writeMetadata[T](settings: CLMapBuilderSettings, es: Seq[T], key: String, filename: String): Unit = {
    val fp = write(settings, filename, prettyPrint(Map(key -> es)))
    logger.info(s"Stored ${es.length} $key to $fp")
  }

  def write(settings: CLMapBuilderSettings, fname: String, content: String): Path =
    writeHOF(settings, fname, content, FileIO.writeLatin)

  def writeUTF(settings: CLMapBuilderSettings, fname: String, content: String): Path =
    writeHOF(settings, fname, content, FileIO.writeUTF)

  private def writeHOF
  (
    settings: CLMapBuilderSettings,
    fname: String,
    content: String,
    writeF: (Path, String, String) => Try[Unit]
  ): Path = {
    val od = Paths.get(settings.outputDir)
    writeF(od, fname, content)
    od.resolve(fname)
  }

}
