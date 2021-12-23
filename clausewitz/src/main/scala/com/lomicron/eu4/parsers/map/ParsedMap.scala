package com.lomicron.eu4.parsers.map

import com.lomicron.eu4.model.map.{TileRoute, Tile}

case class ParsedMap
(
  tiles: Seq[Tile] = Seq.empty,
  routes: Seq[TileRoute] = Seq.empty,
  terrainColors: Array[Int] = Array.empty,
)
