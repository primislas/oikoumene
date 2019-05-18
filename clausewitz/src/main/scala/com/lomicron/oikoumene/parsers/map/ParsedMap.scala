package com.lomicron.oikoumene.parsers.map

import com.lomicron.oikoumene.model.map.{Route, Tile}

case class ParsedMap
(
  tiles: Seq[Tile] = Seq.empty,
  routes: Seq[Route] = Seq.empty,
  terrainColors: Array[Int] = Array.empty,
)
