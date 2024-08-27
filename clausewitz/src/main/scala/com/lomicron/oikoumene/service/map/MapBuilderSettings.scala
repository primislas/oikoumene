package com.lomicron.oikoumene.service.map

import com.lomicron.oikoumene.model.map.MapModes

case class MapBuilderSettings
(
  svg: Option[String] = None,
  mapMode: String = MapModes.POLITICAL,
  includeTagBorders: Option[Boolean] = None,
  includeTagNames: Option[Boolean] = None,
  includeProvinceNames: Option[Boolean] = None,
  includeRivers: Option[Boolean] = None,
  ownWastelands: Option[Boolean] = None,
  groupByTag: Boolean = false,
  svgBackground: Option[String] = None,
  decimalPrecision: Int = 1,
) {
  def withNames: Boolean = includeTagNames.contains(true)
  def withProvinceNames: Boolean = includeProvinceNames.contains(true)
  def withTagBorders: Boolean = includeTagBorders.contains(true)
  def withRivers: Boolean = includeRivers.contains(true)
  def withWastelands: Boolean = ownWastelands.contains(true)
}

object MapBuilderSettings {
  val default: MapBuilderSettings = MapBuilderSettings()
}
