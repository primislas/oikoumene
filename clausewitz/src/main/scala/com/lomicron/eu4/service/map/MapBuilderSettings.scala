package com.lomicron.eu4.service.map

import com.lomicron.eu4.model.map.MapModes
import com.lomicron.eu4.service.map.projections.ProjectionSettings

case class MapBuilderSettings
(
  svg: Option[String] = None,
  mapMode: String = MapModes.POLITICAL,
  outputFile: Option[String] = None,
  includeBorders: Option[Boolean] = None,
  includeNames: Option[Boolean] = None,
  includeRivers: Option[Boolean] = None,
  ownWastelands: Option[Boolean] = None,
  svgBackground: Option[String] = None,
  sourceProjection: Option[ProjectionSettings] = None,
  targetProjection: Option[ProjectionSettings] = None,
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
