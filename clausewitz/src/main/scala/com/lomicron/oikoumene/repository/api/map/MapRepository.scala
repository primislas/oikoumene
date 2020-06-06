package com.lomicron.oikoumene.repository.api.map

import com.lomicron.oikoumene.model.Color
import com.lomicron.oikoumene.model.map._
import com.lomicron.oikoumene.parsers.map.{Polygon, SphericalMap}
import com.lomicron.oikoumene.repository.api.AbstractRepository

trait MapRepository extends AbstractRepository[Color, Tile] {

  def setTerrainMapColorConf(mapTerrain: Seq[TerrainMapColorConf]): MapRepository
  def setTerrainMapColors(terrainColors: Array[Color]): MapRepository
  def rebuildTerrainColors(terrainColors: Array[Color]): MapRepository
  def terrainMapType(argb: Int): Option[String]
  def terrainMapType(color: Color): Option[String]

  def updateAdjacencies(as: Seq[Adjacency]): MapRepository
  def adjacencies: Seq[Adjacency]
  def updateTileRoutes(routes: Seq[TileRoute]): MapRepository
  def tileRoutes: Seq[TileRoute]
  def updateRoutes(routes: Seq[Route]): MapRepository
  def routes: Map[Int, Seq[Route]]
  def provinceRoutes(provId: Int): Seq[Route]
  def buildRoutes(provinces: ProvinceRepository): MapRepository

  def updateMercator(mercator: Seq[Polygon]): MapRepository
  def mercator: Seq[Polygon]
  def updateSphericalMap(sphericalMap: SphericalMap): MapRepository
  def spherical: SphericalMap

}
