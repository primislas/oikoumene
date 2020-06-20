package com.lomicron.oikoumene.repository.api.map

import com.lomicron.oikoumene.model.Color
import com.lomicron.oikoumene.model.map._
import com.lomicron.oikoumene.parsers.map.{MercatorMap, Polygon, River, SphericalMap}
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

  def updateMercator(mercator: MercatorMap): MapRepository
  def mercator: MercatorMap
  def createRivers(rivers: Seq[River]): MapRepository
  def rivers: Seq[River]

  def updatePositions(positions: Seq[ProvincePositions]): MapRepository
  def provincePositions: Seq[ProvincePositions]
  def positionsOf(ids: Seq[Int]): Seq[ProvincePositions] =
    ids.flatMap(positionsOf(_))
  def positionsOf(id: Int): Option[ProvincePositions]

}
