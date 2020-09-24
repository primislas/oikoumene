package com.lomicron.oikoumene.tools

import com.lomicron.oikoumene.repository.api.GameFilesSettings
import com.lomicron.oikoumene.service.map.MapBuilderSettings
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
