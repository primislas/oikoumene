package com.lomicron.eu4.repository.api.resources

import java.nio.file.{Path, Paths}

case class GameFile
(
  name: String,
  path: Path,
  relDir: Option[String] = None,
  mod: Option[String] = None,
  content: Option[String] = None,
) {
  def relPath: Path = relDir.map(Paths.get(_, name)).getOrElse(Paths.get(name))
  def withContent(content: String): GameFile = copy(content = Option(content))
}

object GameFile {
  def of(p: Path, relDir: Option[String] = None, mod: Option[String] = None): GameFile =
    GameFile(p.getFileName.toString, p, relDir, mod)
}
