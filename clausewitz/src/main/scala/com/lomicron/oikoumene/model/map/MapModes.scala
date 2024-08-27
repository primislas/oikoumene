package com.lomicron.oikoumene.model.map

import com.lomicron.oikoumene.service.map.MapBuilderSettings
import com.lomicron.utils.collection.CollectionUtils.toOption

object MapModes {
  val TERRAIN: String = "terrain"
  val POLITICAL: String = "political"
  val PROVINCE_OUTLINE: String = "province_outline"
  val SIMPLE_TERRAIN: String = "simple_terrain"
  val TRADE_NODES: String = "trade_nodes"

  private val politicalSettings: MapBuilderSettings =
    MapBuilderSettings(
      mapMode = POLITICAL,
      includeRivers = true,
      includeTagBorders = true,
      includeTagNames = true,
      ownWastelands = true,
      svgBackground = Seasons.SUMMER,
    )

  private val terrainSettings: MapBuilderSettings = politicalSettings.copy(mapMode = TERRAIN)

  private val provOutlinesSettings: MapBuilderSettings = MapBuilderSettings(mapMode = PROVINCE_OUTLINE)

  private val simpleTerrainSettings: MapBuilderSettings =
    MapBuilderSettings(
      mapMode = SIMPLE_TERRAIN,
      includeRivers = true,
      includeProvinceNames = true,
    )

  private def defaultSettings(mode: String): Option[MapBuilderSettings] =
    mode match {
      case POLITICAL => politicalSettings
      case TERRAIN => terrainSettings
      case PROVINCE_OUTLINE => provOutlinesSettings
      case SIMPLE_TERRAIN => simpleTerrainSettings
      case _ => None
    }

  def settingsOf(overrides: MapBuilderSettings): MapBuilderSettings =
    settingsOf(overrides.mapMode, overrides)

  def settingsOf(mode: String, overrides: Option[MapBuilderSettings] = None): MapBuilderSettings = {
    val settings = defaultSettings(mode).getOrElse(provOutlinesSettings)
    overrides
      .map(os => {
        var overridden = settings.copy(groupByTag = os.groupByTag)
        os.includeTagBorders.foreach(f => overridden = overridden.copy(includeTagBorders = f))
        os.includeTagNames.foreach(f => overridden = overridden.copy(includeTagNames = f))
        os.includeProvinceNames.foreach(f => overridden = overridden.copy(includeProvinceNames = f))
        os.includeRivers.foreach(f => overridden = overridden.copy(includeRivers = f))
        os.ownWastelands.foreach(f => overridden = overridden.copy(ownWastelands = f))
        os.svgBackground.foreach(f => overridden = overridden.copy(svgBackground = f))
        overridden
      })
      .getOrElse(settings)
  }

}
