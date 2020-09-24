package com.lomicron.oikoumene.repository.fs

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path, Paths}

import com.lomicron.oikoumene.io.FileNameAndContent
import com.lomicron.oikoumene.model.localisation.LocalisationEntry
import com.lomicron.oikoumene.repository.api.GameFilesSettings
import com.lomicron.oikoumene.repository.api.resources.ResourceRepository
import com.lomicron.utils.collection.CollectionUtils._
import com.lomicron.utils.io.IO

import scala.collection.immutable.ListMap
import scala.util.matching.Regex

case class FileResourceRepository(settings: GameFilesSettings)
  extends ResourceRepository {

  val localisationDir = "localisation"

  val countryTagsDir = "common/country_tags"
  val countriesDir = "common/countries"
  val countryHistoryDir = "history/countries"

  val diploHistoryDir = "history/diplomacy"
  val warHistoryDir = "history/wars"
  val warGoalTypesDir = "common/wargoal_types"
  val casusBelliTypesDir = "common/cb_types"

  val ideasDir = "common/ideas"

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
  val buildingsDir = "common/buildings"

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

  private def readFile(path: String): String =
    IO.readTextFile(path, StandardCharsets.ISO_8859_1)

  override def getCountryTags: Map[String, String] =
    readDir(countryTagsDir)

  override def getCountries(filesByTags: Map[String, String]): Map[String, String] =
    filesByTags
      .mapValuesEx(filePath)
      .mapValuesEx(_.toString)
      .mapValuesEx(readFile)

  override def getCountryHistory: Map[String, FileNameAndContent] =
    readDir(countryHistoryDir)
      .mapKVtoValue(FileNameAndContent)
      .mapKeys(filenameToTag)

  override def getDiplomaticRelations: Map[String, String] =
    readDir(diploHistoryDir)

  override def getWarHistory: Map[String, String] =
    readDir(warHistoryDir)

  override def getWarGoalTypes: Map[String, String] =
    readDir(warGoalTypesDir)

  override def getCasusBelliTypes: Map[String, String] =
    readDir(casusBelliTypesDir)

  private def filenameToTag(str: String) =
    str.take(3).mkString

  def getProvinceDefinitions: Option[String] =
    readGameFileContent(provinceDefinitionsFile)

  override def getAdjacencies: Option[String] =
    readGameFileContent(adjacenciesFile)

  private def baseGame(relPath: String): Path =
    Paths.get(settings.gameDir.getOrElse("."), relPath)

  private def fromMod(relPath: String): Option[Path] =
    settings.mods.flatMap(modPath(_, relPath)).headOption

  private def modPath(mod: String, relPath: String): Option[Path] =
    settings.modDir.map(Paths.get(_, mod, relPath)).filter(Files.exists(_))

  private def filePath(relPath: String): Path =
    fromMod(relPath).getOrElse(baseGame(relPath))

  private def readGameFile(relPath: String): Option[(String, String)] =
    readFileWithName(filePath(relPath))

  private def readFileWithName(p: Path): Option[(String, String)] =
    Option(p)
      .map(_.toFile)
      .filter(_.exists)
      .map(_.getAbsolutePath)
      .map(readFileKeepFilename)

  private def dirFiles(relPath: String): Seq[Path] = {
    val (modFiles, modFileNames) = readModDir(relPath, settings.mods)
    val vanillaFiles = baseGame(relPath).map(readAllFilesFromDir(_, modFileNames)).getOrElse(Seq.empty)
    modFiles ++ vanillaFiles
  }

  private def readDir(relPath: String): Map[String, String] = {
    val ps = dirFiles(relPath).map(_.toString)
    readFiles(ps).toMap
  }

  @scala.annotation.tailrec
  private def readModDir
  (
    relPath: String,
    mods: Seq[String],
    files: Seq[Path] = Seq.empty,
    excludes: Set[Path] = Set.empty
  ): (Seq[Path], Set[Path]) = {
    if (mods.isEmpty) files -> excludes
    else {
      val modDir = mods.headOption.flatMap(modPath(_, relPath))
      val modFiles = modDir.map(readAllFilesFromDir(_, excludes)).getOrElse(Seq.empty)
      val modFileNames = modFiles.map(_.getFileName)
      readModDir(relPath, mods.drop(1), files ++ modFiles, excludes ++ modFileNames)
    }
  }

  private def readAllFilesFromDir(p: Path, excludes: Set[Path] = Set.empty): Seq[Path] =
    IO.listFiles(p)
        .map(Paths.get(_))
        .filterNot(p => excludes.contains(p.getFileName))

  def readFiles(files: Seq[String]): Seq[(String, String)] =
    files.map(readFileKeepFilename)

  def readSourceFiles(files: Seq[String]): Map[String, String] =
    files.map(readGameFile).flatMap(_.toSeq).toMap

  private def readFileKeepFilename(path: String): (String, String) =
    (Paths.get(path).getFileName.toString, readFile(path))

  override def getLocalisation(language: String)
  : Seq[LocalisationEntry] =
    dirFiles(localisationDir)
      .map(_.toString)
      .filter(_.matches(s"^.*_l_$language.yml"))
      .par
      .map(IO.readTextFile(_, StandardCharsets.UTF_8))
      .flatMap(_.lines)
      .flatMap(LocalisationEntry.fromString)
      .seq

  override def getProvinceTypes: Map[String, String] =
    readSourceFileMapToName(provinceTypesFile)

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

  override def getTerrain: Map[String, String] =
    readSourceFileMapToName(terrainFile)

  override def getClimate: Option[String] =
    readGameFileContent(climateFile)

  override def getElevatedLakes: Map[String, String] =
    readDir(elevatedLakesDir)

  override def getProvinceMap: Option[Path] = Option(filePath(provinceMap))

  override def getTerrainMap: Option[Path] = Option(filePath(terrainMap))

  override def getHeightMap: Option[Path] = Option(filePath(heightMap))

  def getRiversMap: Option[Path] = Option(filePath(riversMap))

  override def getBackground(season: String): Option[Path] =
    Option(filePath(background(season)))

  override def isMapModded: Boolean = fromMod(provinceMap).isDefined

  val provNamePat: String = """^(?<id>\d+).*\.txt$"""
  val provNameRegex: Regex = provNamePat.r

  override def getProvinceHistory: Map[Int, FileNameAndContent] = {
    readDir(provinceHistoryDir)
      .mapKVtoValue(FileNameAndContent)
      .mapKeys(idFromProvHistFileName)
      .filterKeys(_.isDefined)
      .mapKeys(_.get)
  }

  override def getBuildings: Seq[String] =
    readDir(buildingsDir).values.toSeq

  private def idFromProvHistFileName(filename: String): Option[Int] =
    filename match {
      case provNameRegex(id) => Some(id.toInt)
      case _ => None
    }

  private def readGameFileContent(relPath: String): Option[String] =
    readGameFile(relPath).map(_._2)

  private def readSourceFileMapToName(relPath: String): Map[String, String] =
    readGameFile(relPath)
      .map(nc => ListMap(nc._1 -> nc._2))
      .getOrElse(Map.empty)

  override def getCultures: Option[String] =
    readGameFileContent(culturesFile)

  override def getReligions: Map[String, String] =
    readDir(religionsDir)

  override def getIdeas: Map[String, String] =
    readDir(ideasDir)

  override def getTradeGoods: Map[String, String] =
    readDir(tradeGoodsDir)

  override def getPrices: Map[String, String] =
    readDir(pricesDir)

  override def getTradeNodes: Map[String, String] =
    readDir(tradeNodesDir)

}

object FileResourceRepository {
  val defaultModDir = s"${System.getProperty("user.home")}/Paradox Interactive/Europa Universalis IV/mod"

  def apply(gameDir: String, modDir: String): ResourceRepository =
    FileResourceRepository(GameFilesSettings(gameDir, modDir))
}

