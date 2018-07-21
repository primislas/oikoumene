package com.lomicron.oikoumene.repository.fs

import java.nio.file.{Path, Paths}

import com.lomicron.oikoumene.model.localisation.LocalisationEntry
import com.lomicron.utils.io.IO
import com.lomicron.oikoumene.repository.api.ResourceRepository
import com.lomicron.utils.collection.CollectionUtils._

case class FileResourceRepository(
                                   gameDir: String,
                                   modDir: String)
  extends ResourceRepository {

  val countryTagsDir = "common/country_tags"
  val countriesDir = "common/countries"
  val countryHistoryDir = "history/countries"
  val countryNamesFiles = Seq(
    "localisation/countries_l_english.yml",
    "localisation/text_l_english.yml",
    "localisation/eldorado_l_english.yml",
    "localisation/EU4_l_english.yml",
    "localisation/tags_phase4_l_english.yml"
  )

  val localisationDir = "localisation"

  override def getCountryTags: Map[String, String] =
    readSourceDir(countryTagsDir)

  override def getCountries(filesByTags: Map[String, String]): Map[String, String] =
    filesByTags
      .mapValuesEx(fromSource)
      .mapValuesEx(_.toString)
      .mapValuesEx(IO.readTextFile)

  override def getCountryHistory: Map[String, String] =
    readSourceDir(countryHistoryDir)
      .mapKeys(filenameToTag)

  private def filenameToTag(str: String) =
    str.take(3).mkString

  override def getCountryNames: Map[String, String] =
    readSourceFiles(countryNamesFiles)

  private def fromSource(relPath: String) = Paths.get(gameDir, relPath)

  private def readSourceFile(relPath: String): Map[String, String] =
    readFile(fromSource(relPath))

  private def readFile(p: Path): Map[String, String] =
    Option(p)
      .map(_.toFile)
      .filter(_.exists)
      .map(_.getAbsolutePath)
      .map(readFileKeepFilename)
      .map(Map(_))
      .getOrElse(Map.empty)

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
    (Paths.get(path).getFileName.toString, IO.readTextFile(path))

  override def getLocalisation(language: String)
  : Seq[LocalisationEntry] =
    IO
      .listFiles(fromSource(localisationDir))
      .filter(_.matches(s"^.*_l_$language.yml"))
      .par
      .map(IO.readTextFile)
      .flatMap(_.lines)
      .flatMap(LocalisationEntry.fromString)
      .seq

}

object FileResourceRepository {
  def apply(gameDir: String, modDir: String): ResourceRepository =
    new FileResourceRepository(gameDir, modDir)
}

