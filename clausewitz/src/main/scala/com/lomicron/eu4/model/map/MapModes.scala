package com.lomicron.eu4.model.map

import com.lomicron.eu4.service.map.MapBuilderSettings
import com.lomicron.utils.collection.CollectionUtils.toOption

object MapModes {
  val TERRAIN: String = "terrain"
  val POLITICAL: String = "political"
  val PROVINCE_OUTLINE: String = "province_outline"
  val PROVINCE_SHAPES: String = "province_shapes"
  val SIMPLE_TERRAIN: String = "simple_terrain"
  val TRADE_NODES: String = "trade_nodes"

  val politicalSettings: MapBuilderSettings =
    MapBuilderSettings(
      mapMode = POLITICAL,
      includeRivers = true,
      includeBorders = true,
      includeNames = true,
      ownWastelands = true,
      svgBackground = Seasons.SUMMER,
    )

  val terrainSettings: MapBuilderSettings = politicalSettings.copy(mapMode = TERRAIN)

  val provOutlinesSettings: MapBuilderSettings = MapBuilderSettings(mapMode = PROVINCE_OUTLINE)

  val provShapesSettings: MapBuilderSettings = MapBuilderSettings(mapMode = PROVINCE_SHAPES)

  def defaultSettings(mode: String): Option[MapBuilderSettings] =
    mode match {
      case POLITICAL => politicalSettings
      case TERRAIN => terrainSettings
      case PROVINCE_OUTLINE => provOutlinesSettings
      case PROVINCE_SHAPES => provShapesSettings
      case _ => None
    }

  def settingsOf(overrides: MapBuilderSettings): MapBuilderSettings =
    settingsOf(overrides.mapMode, overrides)

  def settingsOf(mode: String, overrides: Option[MapBuilderSettings] = None): MapBuilderSettings = {
    val settings = defaultSettings(mode).getOrElse(provOutlinesSettings)
    overrides
      .map(os => {
        var overridden = settings
        os.includeBorders.foreach(f => overridden = overridden.copy(includeBorders = f))
        os.includeNames.foreach(f => overridden = overridden.copy(includeNames = f))
        os.includeRivers.foreach(f => overridden = overridden.copy(includeRivers = f))
        os.ownWastelands.foreach(f => overridden = overridden.copy(ownWastelands = f))
        os.svgBackground.foreach(f => overridden = overridden.copy(svgBackground = f))
        overridden = overridden.copy(decimalPrecision = os.decimalPrecision)
        overridden
      })
      .getOrElse(settings)
  }

}
