package com.lomicron.oikoumene.model.map

import com.fasterxml.jackson.annotation.{JsonCreator, JsonProperty}
import com.lomicron.oikoumene.model.{Color, Entity}
import com.lomicron.utils.json.FromJson

case class TerrainMapColorConf
(
  id: String,
  @JsonProperty("type") terrainType: String,
  @JsonProperty("color") colorIndex: Int = 0,
  @JsonProperty("rgbColor") color: Option[Color] = None,
) {

  @JsonCreator def this() = this(Entity.UNDEFINED, Entity.UNDEFINED)

  def withColor(c: Color): TerrainMapColorConf = copy(color = Option(c))

}

object TerrainMapColorConf extends FromJson[TerrainMapColorConf]
