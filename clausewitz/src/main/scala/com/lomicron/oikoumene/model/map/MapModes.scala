package com.lomicron.oikoumene.model.map

import com.lomicron.oikoumene.service.map.MapBuilderSettings
import com.lomicron.utils.collection.CollectionUtils.toOption

object MapModes {
  val TERRAIN: String = "terrain"
  val POLITICAL: String = "political"
  val PROVINCE_OUTLINE: String = "province_outline"
  val SIMPLE_TERRAIN: String = "simple_terrain"
  val TRADE_NODES: String = "trade_nodes"
  val WIKI: String = "wiki"

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
  val wikiSettings: MapBuilderSettings = MapBuilderSettings(mapMode = WIKI)

  def defaultSettings(mode: String): Option[MapBuilderSettings] =
    mode match {
      case POLITICAL => politicalSettings
      case TERRAIN => terrainSettings
      case PROVINCE_OUTLINE => provOutlinesSettings
      case WIKI => wikiSettings
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
        overridden
      })
      .getOrElse(settings)
  }

}
