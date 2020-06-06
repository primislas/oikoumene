package com.lomicron.oikoumene.parsers.map

import com.lomicron.oikoumene.model.map.{TileRoute, Tile}

case class ParsedMap
(
  tiles: Seq[Tile] = Seq.empty,
  routes: Seq[TileRoute] = Seq.empty,
  terrainColors: Array[Int] = Array.empty,
)
