package com.lomicron.oikoumene.model.map

import com.fasterxml.jackson.annotation.{JsonCreator, JsonProperty}
import com.lomicron.oikoumene.model.{Color, Entity}
import com.lomicron.utils.json.FromJson

@JsonCreator
case class TerrainMapColorConf
(
  id: String,
  @JsonProperty("type") terrainType: String,
  @JsonProperty("color") colorIndex: Seq[Int] = Seq.empty,
  @JsonProperty("rgbColor") color: Seq[Color] = Seq.empty,
) {

  def withColor(c: Color): TerrainMapColorConf = copy(color = Seq(c))
  def withColor(cs: Seq[Color]): TerrainMapColorConf = copy(color = cs)

}

object TerrainMapColorConf extends FromJson[TerrainMapColorConf]
