package com.lomicron.vicky.service.map

import com.lomicron.oikoumene.model.map.MapModes

case class MapBuilderSettings
(
  svg: Option[String] = None,
  mapMode: String = MapModes.POLITICAL,
  includeBorders: Option[Boolean] = None,
  includeNames: Option[Boolean] = None,
  includeRivers: Option[Boolean] = None,
  ownWastelands: Option[Boolean] = None,
  svgBackground: Option[String] = None,
  decimalPrecision: Int = 1,
) {
  def withNames: Boolean = includeNames.contains(true)
  def withBorders: Boolean = includeBorders.contains(true)
  def withRivers: Boolean = includeRivers.contains(true)
  def withWastelands: Boolean = ownWastelands.contains(true)
}

object MapBuilderSettings {
  val default: MapBuilderSettings = MapBuilderSettings()
}
