package com.lomicron.oikoumene.tools

import java.nio.file.{Path, Paths}

import com.lomicron.oikoumene.engine.Oikoumene
import com.lomicron.oikoumene.io.{FileIO, FileNameAndContent}
import com.lomicron.oikoumene.model.Color
import com.lomicron.oikoumene.parsers.map.MapParser
import com.lomicron.oikoumene.parsers.provinces.ProvinceParser
import com.lomicron.oikoumene.repository.api.{RepositoryFactory, ResourceRepository}
import com.lomicron.oikoumene.repository.fs.FileResourceRepository
import com.lomicron.oikoumene.repository.inmemory.InMemoryRepositoryFactory
import com.lomicron.oikoumene.tools.map.MapBuilder
import com.lomicron.oikoumene.tools.model.SupportedGames
import com.lomicron.oikoumene.tools.model.map.{BorderSvgJson, PolygonSvgJson}
import com.lomicron.oikoumene.tools.model.metadata._
import com.lomicron.oikoumene.writers.map.SvgMapService
import com.lomicron.utils.collection.CollectionUtils.{SeqEx, toOption}
import com.lomicron.utils.json.JsonMapper.prettyPrint
import com.typesafe.scalalogging.LazyLogging

object ClausewitzMapBuilder extends LazyLogging {

  val mapPolygonsJson = "map-polygons.json"
  val bordersJson = "borders.json"
  val provsJson = "provinces.json"
  val tagsJson = "tags.json"
  val culturesJson = "cultures.json"
  val cultureGroupsJson = "culture-groups.json"
  val religionsJson = "religion.json"
  val religionGroupsJson = "religion-groups.json"
  val politicalSvgMap = "world.svg"

  def main(args: Array[String]): Unit = {
    val settings = parseArgs(args)
    if (args.isEmpty || settings.isHelp)
      printHelp()
    else {
      if (settings.gameDir.isEmpty) {
        logger.error("Game directory has not been configured. Rerun tracer with -gd or --game-dir parameter.")
        logger.info("Exiting...")
      } else {
        val repo = initMetadata(settings)
        generatedMapOutlines(settings, repo)
        generateMetadata(settings, repo)
        generateMap(settings, repo)
      }
    }
  }

  def printHelp(): Unit = {
    val help =
      """help:
        |oikoumene province tracer v1.0
        |Convert provinces.bmp to svg polygons and borders written to json.
        |
        |usage: sbt "runMain com.lomicron.oikoumene.tools.ClausewitzMapBuilder
        |             [--help|-h]
        |             [--game-dir|-gd <game_dir_path>]
        |             [--mod-dir|-md <mod_dir_path>]
        |             [--mod|-m <mod>]
        |             [--output-dir|-od <output_dir>]
        |             [--no-borders|-nb]
        |             [--meta|-meta eu4]
        |             [--svg|-svg eu4]
        |
        |e.g.
        |sbt -J-Xmx3G -J-Xss3M "runMain com.lomicron.oikoumene.tools.ClausewitzMapBuilder -gd \"D:/Steam/steamapps/common/Europa Universalis IV\"""".stripMargin
    logger.info(help)
  }

  def parseArgs(args: Array[String]): MapBuilderSettings = parseArgs(args.toList)

  @scala.annotation.tailrec
  def parseArgs(args: List[String] = List.empty, settings: MapBuilderSettings = MapBuilderSettings()): MapBuilderSettings =
    args match {
      case "-h" :: _ | "--help" :: _ => settings.copy(isHelp = true)
      case "-i" :: inputFile :: tail =>
        parseArgs(tail, settings.copy(input = inputFile))
      case "--input" :: inputFile :: tail => parseArgs(tail, settings.copy(input = inputFile))
      case "-gd" :: gameDir :: tail => parseArgs(tail, settings.copy(gameDir = gameDir))
      case "--game-dir" :: gameDir :: tail => parseArgs(tail, settings.copy(gameDir = gameDir))
      case "-md" :: modDir :: tail => parseArgs(tail, settings.copy(modDir = modDir))
      case "--mod-dir" :: modDir :: tail => parseArgs(tail, settings.copy(modDir = modDir))
      case "-m" :: mod :: tail => parseArgs(tail, settings.addMod(mod))
      case "--mod" :: mod :: tail => parseArgs(tail, settings.addMod(mod))
      case "-od" :: outputFile :: tail => parseArgs(tail, settings.copy(outputDir = outputFile))
      case "--output-dir" :: outputFile :: tail => parseArgs(tail, settings.copy(outputDir = outputFile))
      case "-nb" :: tail => parseArgs(tail, settings.copy(includeBorders = false))
      case "--no-borders" :: tail => parseArgs(tail, settings.copy(includeBorders = false))
      case "-meta" :: engine :: tail => parseArgs(tail, settings.copy(meta = engine))
      case "--metadata" :: engine :: tail => parseArgs(tail, settings.copy(meta = engine))
      case "-svg" :: engine :: tail => parseArgs(tail, settings.copy(svg = engine))
      case "--svg" :: engine :: tail => parseArgs(tail, settings.copy(svg = engine))
      case Nil => settings
      case _ => parseArgs(args.drop(1), settings)
    }

  def generatedMapOutlines(settings: MapBuilderSettings, repo: RepositoryFactory)
  : (Seq[PolygonSvgJson], Seq[BorderSvgJson]) =
  {
    val r = repo.resources
    val inputFile = getProvincesBmp(r)
    val (ps, bs) = parseProvinceShapes(inputFile, settings.includeBorders)
    val (psid, bsid) = parseProvinceIds(r, ps, bs)
    val (psmeta, bsmeta) = addSvgClasses(settings, repo, psid, bsid)
    writeProvinces(settings, psmeta)
    writeBorders(settings, bsmeta)
    (psmeta, bsmeta)
  }

  def initMetadata(settings: MapBuilderSettings): RepositoryFactory = {
    val gameDir = settings.gameDir.get
    val modDir = settings.modDir.getOrElse("")
    val mods = settings.mods
    val emptyRepos = InMemoryRepositoryFactory(gameDir, modDir, mods)

    settings.meta match {
      case Some(SupportedGames.eu4) => Oikoumene.parseConfigs(emptyRepos)
      case _ => emptyRepos
    }
  }

  def generateMetadata(settings: MapBuilderSettings, repos: RepositoryFactory): RepositoryFactory = {
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

  def generateMap(settings: MapBuilderSettings, repos: RepositoryFactory): RepositoryFactory = {
    settings.svg match {
      case Some(SupportedGames.eu4) =>
        val svgMap = MapBuilder.buildMap(repos)
        val fc = FileNameAndContent(politicalSvgMap, svgMap)
        val fp = writeUTF(settings, fc)
        logger.info(s"Stored map to $fp")
    }

    repos
  }

  def initRepo(settings: MapBuilderSettings): FileResourceRepository = {
    val modDir = settings.modDir.getOrElse(FileResourceRepository.defaultModDir)
    val mods = if (settings.modDir.isDefined) settings.mods else Seq.empty

    settings
      .gameDir
      .map(FileResourceRepository(_, modDir, mods))
      .get
  }

  def getProvincesBmp(repo: ResourceRepository): Path =
    repo.getProvinceMap.get

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
    val defs = ProvinceParser
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
    settings: MapBuilderSettings,
    repos: RepositoryFactory,
    polygons: Seq[PolygonSvgJson],
    borders: Seq[BorderSvgJson]
  ): (Seq[PolygonSvgJson], Seq[BorderSvgJson]) =
    if (!settings.meta.contains("eu4")) (polygons, borders)
    else {
      val service = SvgMapService(repos)
      val ps = polygons.map(p => p.provId.map(service.classesOfProvince).map(p.addClasses).getOrElse(p))
      val bs = borders.map(b => b.addClass(service.borderBetweenProvIds(b.lProv, b.rProv)))
      (ps, bs)
    }

  def writeProvinces(settings: MapBuilderSettings, polygons: Seq[PolygonSvgJson]): Unit =
    writeMetadata(settings, polygons, "polygons", mapPolygonsJson)

  def writeBorders(settings: MapBuilderSettings, borders: Seq[BorderSvgJson]): Unit =
    if (settings.includeBorders)
      writeMetadata(settings, borders, "borders", bordersJson)

  def writeMetadata[T](settings: MapBuilderSettings, es: Seq[T], key: String, filename: String): Unit = {
    val fc = FileNameAndContent(filename, prettyPrint(Map(key -> es)))
    val fp = write(settings, fc)
    logger.info(s"Stored ${es.length} $key to $fp")
  }

  def write(settings: MapBuilderSettings, fc: FileNameAndContent): Path =
    writeHOF(settings, fc, FileIO.writeLatin)

  def writeUTF(settings: MapBuilderSettings, fc: FileNameAndContent): Path =
    writeHOF(settings, fc, FileIO.writeUTF)

  private def writeHOF
  (
    settings: MapBuilderSettings,
    fc: FileNameAndContent,
    writeF: (Path, FileNameAndContent) => Unit
  ): Path = {
    val od = Paths.get(settings.outputDir)
    writeF(od, fc)
    od.resolve(fc.name)
  }

}
