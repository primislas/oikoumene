package com.lomicron.oikoumene.repository.fs

import java.nio.file.{Path, Paths}

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
  val countryNamesFile = "localisation/countries_l_english.yml"

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
    readSourceFile(countryNamesFile)

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
    IO
      .listFiles(p)
      .map(readFileKeepFilename)
      .toMap
  }

  private def readFileKeepFilename(path: String) =
    (Paths.get(path).getFileName.toString, IO.readTextFile(path))
}

object FileResourceRepository {
  def apply(gameDir: String, modDir: String): ResourceRepository =
    new FileResourceRepository(gameDir, modDir)
}

