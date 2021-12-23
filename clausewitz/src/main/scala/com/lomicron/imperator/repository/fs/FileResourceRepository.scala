package com.lomicron.imperator.repository.fs

import com.lomicron.imperator.repository.api.ResourceRepository
import com.lomicron.eu4.repository.api.GameFilesSettings
import com.lomicron.eu4.repository.api.resources.GameFile
import com.lomicron.utils.collection.CollectionUtils._
import com.lomicron.utils.io.IO

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path, Paths}
import scala.collection.parallel.CollectionConverters._

case class FileResourceRepository(settings: GameFilesSettings)
  extends ResourceRepository {

  val localisationDir = "localisation"

  val provinceSetupDir = "game/setup/provinces"

  val buildingsDir = "game/common/buildings"
  val popTypesDir = "game/common/pop_types"

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

  override def getProvinceSetup: Seq[GameFile] = readDir(provinceSetupDir)

  override def getBuildings: Seq[GameFile] = readDir(buildingsDir)
  override def getPopTypes: Seq[GameFile] = readDir(popTypesDir)
}

object FileResourceRepository {
  val userHome: String = Option(System.getProperty("user.home")).getOrElse("~/")
  val imperatorSettingsDir: String = Paths.get(userHome, "/Documents/Paradox Interactive/Europa Universalis IV").toString
  val imperatorModsDir: String = Paths.get(imperatorSettingsDir, "/mod").toString
  val imperatorSaveDir: String = Paths.get(imperatorSettingsDir, "/save games").toString

  def apply(gameDir: String, modDir: String): ResourceRepository =
    FileResourceRepository(GameFilesSettings(gameDir, modDir))
}

