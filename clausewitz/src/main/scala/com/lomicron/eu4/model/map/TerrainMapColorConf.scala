package com.lomicron.eu4.model.map

import com.fasterxml.jackson.annotation.{JsonCreator, JsonProperty}
import com.lomicron.oikoumene.model.Color
import com.lomicron.utils.json.FromJson

@JsonCreator
case class TerrainMapColorConf
(
  id: String,
  @JsonProperty("type") terrainType: String,
  @JsonProperty("color") colorIndex: Int = 0,
  @JsonProperty("rgbColor") color: Option[Color] = None,
) {

  def withColor(c: Color): TerrainMapColorConf = copy(color = Option(c))

}

object TerrainMapColorConf extends FromJson[TerrainMapColorConf]
