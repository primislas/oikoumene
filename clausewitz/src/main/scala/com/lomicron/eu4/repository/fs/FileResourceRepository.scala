package com.lomicron.eu4.repository.fs

import com.lomicron.eu4.parsers.politics.TagConf
import com.lomicron.eu4.repository.api.GameFilesSettings
import com.lomicron.eu4.repository.api.resources.ResourceRepository
import com.lomicron.oikoumene.model.localisation.LocalisationEntry
import com.lomicron.oikoumene.repository.api.resources.GameFile
import com.lomicron.utils.collection.CollectionUtils._
import com.lomicron.utils.io.IO

import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path, Paths}
import scala.collection.parallel.CollectionConverters._
import scala.util.matching.Regex

case class FileResourceRepository(settings: GameFilesSettings)
  extends ResourceRepository {

  val localisationDir = "localisation"

  val countryTagsDir = "common/country_tags"
  val countriesDir = "common/countries"
  val countryHistoryDir = "history/countries"
  val rulerPersonalitiesDir = "common/ruler_personalities"

  val diploHistoryDir = "history/diplomacy"
  val warHistoryDir = "history/wars"
  val warGoalTypesDir = "common/wargoal_types"
  val casusBelliTypesDir = "common/cb_types"

  val governmentsDir = "common/governments"
  val governmentRanksDir = "common/government_ranks"
  val governmentReformsDir = "common/government_reforms"
  val technologyDir = "common/technologies"
  val techGroupFile = "common/technology.txt"
  val ideasDir = "common/ideas"
  val policiesDir = "common/policies"
  val stateEdictsDir = "common/state_edicts"

  val provinceDefinitionsFile = "map/definition.csv"
  val adjacenciesFile = "map/adjacencies.csv"
  val provinceTypesFile = "map/default.map"
  val provincePositionsFile = "map/positions.txt"
  val areasFile = "map/area.txt"
  val regionsFile = "map/region.txt"
  val supperregionsFile = "map/superregion.txt"
  val continentsFile = "map/continent.txt"
  val colonialRegionsFile = "common/colonial_regions/00_colonial_regions.txt"
  val terrainFile = "map/terrain.txt"
  val climateFile = "map/climate.txt"
  val elevatedLakesDir = "map/lakes"
  val provinceHistoryDir = "history/provinces"
  val buildingsDir = "common/buildingIds"

  val provinceMap = "map/provinces.bmp"
  val terrainMap = "map/terrain.bmp"
  val heightMap = "map/heightmap.bmp"
  val riversMap = "map/rivers.bmp"
  def background(season: String): String = s"map/terrain/colormap_$season.dds"

  val culturesFile = "common/cultures/00_cultures.txt"
  val religionsDir = "common/religions"

  val tradeGoodsDir = "common/tradegoods"
  val pricesDir = "common/prices"
  val tradeNodesDir = "common/tradenodes"
  val centersOfTradeDir = "common/centers_of_trade"

  val eventModifiersDir = "common/event_modifiers"
  val staticModifiersDir = "common/static_modifiers"

  private def readFile(path: String): String =
    IO.readTextFile(path, StandardCharsets.ISO_8859_1)

  def readGameFile(gf: GameFile): GameFile = {
    val path = pathOf(gf)
    val content = readFile(path)
    gf.withContent(content)
  }

  def pathOf(gf: GameFile): String =
    gf
      .path
      .orElse(gf.relDir.map(Paths.get(_, gf.name).toString).map(filePath))
      .getOrElse(Paths.get(gf.name))
      .toString

  override def getResource(fileConf: GameFile): GameFile = {
    val content = readGameFile(fileConf).flatMap(_.content)
    fileConf.copy(content = content)
  }

  override def getCountryTags: Seq[GameFile] =
    readDir(countryTagsDir)

  override def getCountries(filesByTags: Map[String, GameFile]): Map[String, GameFile] =
    filesByTags
      .mapValuesEx(gf => {
        val fullPath = filePath(gf.relPath.toString).path
        gf.copy(path = fullPath)
      })
      .mapValuesEx(readGameFile)

  override def getCountryHistory: Map[String, GameFile] =
    readDir(countryHistoryDir)
      .map(gf => filenameToTag(gf.name) -> gf)
      .toMap

  def getCountryConfigs(filesByTag: Map[String, GameFile]): Seq[TagConf] = {
    val historyFiles = listDirFiles(countryHistoryDir)
      .map(f => filenameToTag(f.name) -> f)
      .toMap

    filesByTag
      .map(e => {
        val (tag, tagConfFile) = e
        val tagConfWithFullPath = filePath(tagConfFile.relPath.toString)
        val hist = historyFiles.get(tag)
        TagConf(tag, tagConfWithFullPath, hist)
      })
      .toSeq
  }

  override def getRulerPersonalities: Seq[GameFile] =
    readDir(rulerPersonalitiesDir)

  override def getDiplomaticRelations: Seq[GameFile] =
    readDir(diploHistoryDir)

  override def getWarHistory: Seq[GameFile] =
    readDir(warHistoryDir)

  override def getWarGoalTypes: Seq[GameFile] =
    readDir(warGoalTypesDir)

  override def getCasusBelliTypes: Seq[GameFile] =
    readDir(casusBelliTypesDir)

  private def filenameToTag(str: String) =
    str.take(3).mkString

  def getProvinceDefinitions: Option[String] =
    readGameFileContent(provinceDefinitionsFile)

  override def getAdjacencies: Option[String] =
    readGameFileContent(adjacenciesFile)

  private def baseGame(relPath: String): GameFile = {
    val path = Paths.get(settings.gameDir.getOrElse("."), relPath)
    GameFile.of(path)
  }

  private def fromMod(relPath: String): Option[GameFile] =
    settings.mods.flatMap(modPath(_, relPath)).headOption

  private def modPath(mod: String, relPath: String): Option[GameFile] = {
    val p = Paths.get(relPath)
    val relDirPath = if (p.toFile.isFile) p.getParent else p
    val relDir = relDirPath.toString
    settings.modDir.map(Paths.get(_, mod, relPath)).filter(Files.exists(_)).map(p => GameFile.of(p, relDir, mod))
  }

  private def filePath(relPath: String): GameFile =
    fromMod(relPath).getOrElse(baseGame(relPath))

  private def readGameFile(relPath: String): Option[GameFile] =
    readGameFile(filePath(relPath))

  private def dirFiles(relPath: String): Seq[GameFile] = {
    val (modFiles, modFileNames) = readModDir(relPath, settings.mods)
    val vanillaFiles = baseGame(relPath)
      .flatMap(_.path.map(readAllFilesFromDir(_, modFileNames)))
      .getOrElse(Seq.empty)
      .map(GameFile.of(_, relPath))
    vanillaFiles ++ modFiles
  }

  private def readDir(relPath: String): Seq[GameFile] = {
    val ps = listDirFiles(relPath)
    readFiles(ps)
  }

  private def listDirFiles(relPath: String, excludeDirs: Boolean = true): Seq[GameFile] =
    dirFiles(relPath)
      .filterNot(excludeDirs && _.path.toFile.isDirectory)

  @scala.annotation.tailrec
  private def readModDir
  (
    relPath: String,
    mods: Seq[String],
    files: Seq[GameFile] = Seq.empty,
    excludes: Set[Path] = Set.empty
  ): (Seq[GameFile], Set[Path]) = {
    if (mods.isEmpty) files -> excludes
    else {
      val mod = mods.headOption
      val modDir = mod.flatMap(modPath(_, relPath))
      val modFiles = modDir
        .flatMap(dir => dir.path.map(readAllFilesFromDir(_, excludes)))
        .getOrElse(Seq.empty)
      val confs = modFiles.map(path => GameFile.of(path, relPath, mod))
      val modFileNames = confs.flatMap(_.path.map(_.getFileName)).toSet
      readModDir(relPath, mods.drop(1), files ++ confs, excludes ++ modFileNames)
    }
  }

  private def readAllFilesFromDir(p: Path, excludes: Set[Path] = Set.empty): Seq[Path] =
    IO.listFiles(p)
        .map(Paths.get(_))
        .filterNot(p => excludes.contains(p.getFileName))

  def readFiles(files: Seq[GameFile]): Seq[GameFile] =
    if (files.size < 16) files.map(readGameFile)
    else files.par.map(readGameFile).seq

  def readSourceFiles(files: Seq[String]): Seq[GameFile] =
    files.flatMap(readGameFile)

  override def getLocalisation(language: String)
  : Seq[LocalisationEntry] = {
    (settings.mods.map(Option(_)) :+ None)
      .flatMap(readLocalisation(_, language))
  }

  def readLocalisation(mod: Option[String], language: String): Seq[LocalisationEntry] = {
    mod
      .flatMap(modPath(_, localisationDir))
      .orElse(baseGame(localisationDir))
      .map(readFilesAndSubdirFilesFromDir)
      .getOrElse(Seq.empty)
      .par
      .map(_.toString)
      .filter(_.matches(s".*_l_$language\\.yml$$"))
      .map(IO.readTextFile(_, StandardCharsets.UTF_8))
      .flatMap(_.linesIterator)
      .flatMap(LocalisationEntry.fromString)
      .seq
  }

  def readFilesAndSubdirFilesFromDir(gf: GameFile): Seq[File] =
    gf.path.map(readFilesAndSubdirFilesFromDir).getOrElse(Seq.empty)

  def readFilesAndSubdirFilesFromDir(d: Path): Seq[File] = {
    val fs = readAllFilesFromDir(d)
    val isSubDir = fs.map(_.toFile).groupBy(_.isDirectory)
    val subDirFiles = isSubDir.getOrElse(true, Seq.empty)
      .map(_.toPath)
      .flatMap(readAllFilesFromDir(_))
      .map(_.toFile)
      .filterNot(_.isDirectory)
    val rootDirFiles = isSubDir.getOrElse(false, Seq.empty)
    rootDirFiles ++ subDirFiles
  }

  override def getProvinceTypes: Option[GameFile] =
    readGameFile(provinceTypesFile)

  override def getAreas: Option[String] =
    readGameFileContent(areasFile)

  override def getProvincePositions: Option[String] =
    readGameFileContent(provincePositionsFile)

  override def getRegions: Option[String] =
    readGameFileContent(regionsFile)

  override def getSuperregions: Option[String] =
    readGameFileContent(supperregionsFile)

  override def getContinents: Option[String] =
    readGameFileContent(continentsFile)

  override def getColonialRegions: Option[String] =
    readGameFileContent(colonialRegionsFile)

  override def getTerrain: Option[GameFile] =
    readGameFile(terrainFile)

  override def getClimate: Option[String] =
    readGameFileContent(climateFile)

  override def getElevatedLakes: Seq[GameFile] =
    readDir(elevatedLakesDir)

  override def getProvinceMap: Option[GameFile] = Option(filePath(provinceMap))

  override def getTerrainMap: Option[GameFile] = Option(filePath(terrainMap))

  override def getHeightMap: Option[GameFile] = Option(filePath(heightMap))

  def getRiversMap: Option[GameFile] = Option(filePath(riversMap))

  override def getBackground(season: String): Option[GameFile] =
    Option(filePath(background(season)))

  override def isMapModded: Boolean = fromMod(provinceMap).isDefined

  val provNamePat: String = """^(?<id>\d+).*\.txt$"""
  val provNameRegex: Regex = provNamePat.r

  def getProvinceHistoryResources: Map[Int, GameFile] =
    dirFiles(provinceHistoryDir)
      .reverse
      .flatMap(gf => idFromProvHistFileName(gf.name).map(id => id -> gf))
      .distinctBy(_._1)
      .toMap

  override def getProvinceHistory: Map[Int, GameFile] =
    getProvinceHistoryResources.mapValuesEx(readGameFile)

  override def getBuildings: Seq[GameFile] =
    readDir(buildingsDir)

  private def idFromProvHistFileName(filename: String): Option[Int] =
    filename match {
      case provNameRegex(id) => Some(id.toInt)
      case _ => None
    }

  private def readGameFileContent(relPath: String): Option[String] =
    readGameFile(relPath).flatMap(_.content)

  override def getCultures: Option[String] =
    readGameFileContent(culturesFile)

  override def getReligions: Seq[GameFile] =
    readDir(religionsDir)

  override def getGovernments: Seq[GameFile] =
    readDir(governmentsDir)

  override def getGovernmentRanks: Seq[GameFile] =
    readDir(governmentRanksDir)

  override def getGovernmentReforms: Seq[GameFile] =
    readDir(governmentReformsDir)

  override def getTechnologies: Seq[GameFile] =
    readDir(technologyDir)

  override def getTechGroupConfig: Option[String] =
    readGameFileContent(techGroupFile)

  override def getIdeas: Seq[GameFile] =
    readDir(ideasDir)

  override def getPolicies: Seq[GameFile] =
    readDir(policiesDir)

  override def getStateEdicts: Seq[GameFile] =
    readDir(stateEdictsDir)

  override def getTradeGoods: Seq[GameFile] =
    readDir(tradeGoodsDir)

  override def getPrices: Seq[GameFile] =
    readDir(pricesDir)

  override def getTradeNodes: Seq[GameFile] =
    readDir(tradeNodesDir)


  override def getCentersOfTrade: Seq[GameFile] =
    readDir(centersOfTradeDir)

  override def getEventModifiers: Seq[GameFile] =
    readDir(eventModifiersDir)

  override def getStaticModifiers: Seq[GameFile] =
    readDir(staticModifiersDir)
}

object FileResourceRepository {
  val userHome: String = Option(System.getProperty("user.home")).getOrElse("~/")
  val eu4SettingsDir: String = Paths.get(userHome, "/Documents/Paradox Interactive/Europa Universalis IV").toString
  val defaultModsDir: String = Paths.get(eu4SettingsDir, "/mod").toString
  val defaultSaveDir: String = Paths.get(eu4SettingsDir, "/save games").toString

  def apply(gameDir: String, modDir: String): ResourceRepository =
    FileResourceRepository(GameFilesSettings(gameDir, modDir))
}

