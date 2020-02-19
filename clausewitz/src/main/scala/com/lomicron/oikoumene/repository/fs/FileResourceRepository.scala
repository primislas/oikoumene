package com.lomicron.oikoumene.repository.fs

import java.nio.charset.StandardCharsets
import java.nio.file.{Path, Paths}

import com.lomicron.oikoumene.model.localisation.LocalisationEntry
import com.lomicron.oikoumene.repository.api.{FileNameAndContent, ResourceRepository}
import com.lomicron.utils.collection.CollectionUtils._
import com.lomicron.utils.io.IO

import scala.collection.immutable.ListMap
import scala.util.matching.Regex

case class FileResourceRepository
(gameDir: String,
 modDir: String
) extends ResourceRepository {

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
  val provinceTypesFile = "map/default.map"
  val provincePositionsFile = "map/positions.txt"
  val areasFile = "map/area.txt"
  val regionsFile = "map/region.txt"
  val supperregionsFile = "map/superregion.txt"
  val continentsFile = "map/continent.txt"
  val colonialRegionsFile = "common/colonial_regions/00_colonial_regions.txt"
  val terrainFile = "map/terrain.txt"
  val climateFile = "map/climate.txt"
  val provinceHistoryDir = "history/provinces"
  val buildingsDir = "common/buildings"

  val provinceMap = "map/provinces.bmp"
  val terrainMap = "map/terrain.bmp"
  val heightMap = "map/heightmap.bmp"

  val culturesFile = "common/cultures/00_cultures.txt"
  val religionsDir = "common/religions"

  val tradeGoodsDir = "common/tradegoods"
  val pricesDir = "common/prices"
  val tradeNodesDir = "common/tradenodes"

  private def readFile(path: String): String =
    IO.readTextFile(path, StandardCharsets.ISO_8859_1)

  override def getCountryTags: Map[String, String] =
    readSourceDir(countryTagsDir)

  override def getCountries(filesByTags: Map[String, String]): Map[String, String] =
    filesByTags
      .mapValuesEx(fromSource)
      .mapValuesEx(_.toString)
      .mapValuesEx(readFile)

  override def getCountryHistory: Map[String, String] =
    readSourceDir(countryHistoryDir)
      .mapKeys(filenameToTag)

  override def getDiplomaticRelations: Map[String, String] =
    readSourceDir(diploHistoryDir)

  override def getWarHistory: Map[String, String] =
    readSourceDir(warHistoryDir)

  override def getWarGoalTypes: Map[String, String] =
    readSourceDir(warGoalTypesDir)

  override def getCasusBelliTypes: Map[String, String] =
    readSourceDir(casusBelliTypesDir)

  private def filenameToTag(str: String) =
    str.take(3).mkString

  def getProvinceDefinitions: Option[String] =
    readSourceFileContent(provinceDefinitionsFile)

  private def fromSource(relPath: String) = Paths.get(gameDir, relPath)

  private def readSourceFile(relPath: String): Option[(String, String)] =
    readFile(fromSource(relPath))

  private def readFile(p: Path): Option[(String, String)] =
    Option(p)
      .map(_.toFile)
      .filter(_.exists)
      .map(_.getAbsolutePath)
      .map(readFileKeepFilename)

  private def readSourceDir(relPath: String): Map[String, String] =
    readAllFilesFromDir(fromSource(relPath))

  private def readAllFilesFromDir(p: Path): Map[String, String] = {
    readFiles(IO.listFiles(p)).toMap
  }

  def readFiles(files: Seq[String]): Seq[(String, String)] =
    files.map(readFileKeepFilename)

  def readSourceFiles(files: Seq[String]): Map[String, String] =
    files.map(readSourceFile).flatMap(_.toSeq).toMap

  private def readFileKeepFilename(path: String) =
    (Paths.get(path).getFileName.toString, readFile(path))

  override def getLocalisation(language: String)
  : Seq[LocalisationEntry] =
    IO
      .listFiles(fromSource(localisationDir))
      .filter(_.matches(s"^.*_l_$language.yml"))
      .par
      .map(IO.readTextFile(_, StandardCharsets.UTF_8))
      .flatMap(_.lines)
      .flatMap(LocalisationEntry.fromString)
      .seq

  override def getProvinceTypes: Map[String, String] =
    readSourceFileMapToName(provinceTypesFile)

  override def getAreas: Option[String] =
    readSourceFileContent(areasFile)

  override def getProvincePositions: Option[String] =
    readSourceFileContent(provincePositionsFile)

  override def getRegions: Option[String] =
    readSourceFileContent(regionsFile)

  override def getSuperregions: Option[String] =
    readSourceFileContent(supperregionsFile)

  override def getContinents: Option[String] =
    readSourceFileContent(continentsFile)

  override def getColonialRegions: Option[String] =
    readSourceFileContent(colonialRegionsFile)

  override def getTerrain: Map[String, String] =
    readSourceFileMapToName(terrainFile)

  override def getClimate: Option[String] =
    readSourceFileContent(climateFile)

  override def getProvinceMap: Option[Path] = Option(fromSource(provinceMap))

  override def getTerrainMap: Option[Path] = Option(fromSource(terrainMap))

  override def getHeightMap: Option[Path] = Option(fromSource(heightMap))


  val provNamePat: String = """^(?<id>\d+).*\.txt$"""
  val provNameRegex: Regex = provNamePat.r

  override def getProvinceHistory: Map[Int, FileNameAndContent] = {
//    val histFiles = readAllFilesFromDir(fromSource(provinceHistoryDir))
      readAllFilesFromDir(fromSource(provinceHistoryDir))
        .mapKVtoValue(FileNameAndContent)
//    ClausewitzParser
//      .filesWithPrependedNames(histFiles)
      .mapKeys(idFromProvHistFileName)
      .filterKeys(_.isDefined)
      .mapKeys(_.get)
  }

  override def getBuildings: Seq[String] =
    readAllFilesFromDir(fromSource(buildingsDir)).values.toSeq

  private def idFromProvHistFileName(filename: String): Option[Int] =
    filename match {
      case provNameRegex(id) => Some(id.toInt)
      case _ => None
    }

  private def readSourceFileContent(relPath: String): Option[String] =
    readSourceFile(relPath).map(_._2)

  private def readSourceFileMapToName(relPath: String): Map[String, String] =
    readSourceFile(relPath)
      .map(nc => ListMap(nc._1 -> nc._2))
      .getOrElse(Map.empty)

  override def getCultures: Option[String] =
    readSourceFileContent(culturesFile)

  override def getReligions: Map[String, String] =
    readSourceDir(religionsDir)

  override def getIdeas: Map[String, String] =
    readSourceDir(ideasDir)

  override def getTradeGoods: Map[String, String] =
    readSourceDir(tradeGoodsDir)

  override def getPrices: Map[String, String] =
    readSourceDir(pricesDir)

  override def getTradeNodes: Map[String, String] =
    readSourceDir(tradeNodesDir)

}

object FileResourceRepository {
  def apply(gameDir: String, modDir: String): ResourceRepository =
    new FileResourceRepository(gameDir, modDir)
}

