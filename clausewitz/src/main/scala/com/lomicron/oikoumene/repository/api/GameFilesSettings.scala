package com.lomicron.oikoumene.repository.api

import com.lomicron.utils.collection.CollectionUtils.toOption

case class GameFilesSettings
(
  gameDir: Option[String] = None,
  modDir: Option[String] = None,
  mods: Seq[String] = Seq.empty,
  cacheDir: Option[String] = None,
  rebuildCache: Boolean = false,
) {

  def withModDir(modDir: String): GameFilesSettings =
    copy(modDir = modDir)

  def withMods(mods: Seq[String]): GameFilesSettings =
    copy(mods = mods)

  def withCacheDir(cacheDir: String): GameFilesSettings =
    copy(cacheDir = cacheDir)

  def addMod(mod: String): GameFilesSettings =
    copy(mods = mods :+ mod)

  def addMods(ms: Seq[String]): GameFilesSettings =
    copy(mods = (mods ++ ms).distinct)

}
