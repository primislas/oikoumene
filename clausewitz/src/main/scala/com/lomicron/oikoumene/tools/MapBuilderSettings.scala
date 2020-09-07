package com.lomicron.oikoumene.tools

case class MapBuilderSettings
(
  input: String = "provinces.bmp",
  outputDir: String = "./target",
  gameDir: Option[String] = None,
  modDir: Option[String] = None,
  mods: Seq[String] = Seq.empty,
  meta: Option[String] = None,
  svg: Option[String] = None,
  includeBorders: Boolean = true,
  isHelp: Boolean = false,
) {
  def addMod(modName: String): MapBuilderSettings = copy(mods = mods :+ modName)
}
