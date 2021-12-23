package com.lomicron.eu4.writers

import java.nio.file.{Path, Paths}

import com.lomicron.eu4.io.{FileIO, FileNameAndContent}
import com.lomicron.eu4.repository.fs.FileResourceRepository

trait FileModWriter[T] extends ModWriter[T, FileNameAndContent] { self =>

  def repo: FileResourceRepository
  def relativeTargetDir: String

  def modPath: Option[Path] = for {
    modsDir <- settings.eu4ModDir
    modDir <- settings.modDir
  } yield Paths.get(modsDir, modDir)

  def targetPath: Option[Path] =
    modPath.map(p => Paths.get(p.toString, relativeTargetDir))


  override def storeSerialized(e: FileNameAndContent): ModWriter[T, FileNameAndContent] = {
    targetPath.foreach(FileIO.writeLatin(_, e))
    self
  }

  override def clear: FileModWriter[T] = {
    targetPath.foreach(FileIO.clearDir)
    self
  }
}
