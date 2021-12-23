package com.lomicron.eu4.tools

import com.lomicron.eu4.repository.api.GameFilesSettings
import com.lomicron.eu4.service.map.MapBuilderSettings
import com.softwaremill.quicklens._

case class CLMapBuilderSettings
(
  input: String = "provinces.bmp",
  outputDir: String = "./target",
  meta: Option[String] = None,
  isHelp: Boolean = false,
  save: Option[String] = None,

  fileSettings: GameFilesSettings = GameFilesSettings(),
  mapSettings: MapBuilderSettings = MapBuilderSettings(),

) {

  def addMod(modName: String): CLMapBuilderSettings =
    this.modify(_.fileSettings.mods).setTo(fileSettings.mods :+ modName)

}
