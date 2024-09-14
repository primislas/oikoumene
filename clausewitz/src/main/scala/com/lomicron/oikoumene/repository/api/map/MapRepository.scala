package com.lomicron.oikoumene.repository.api.map

import com.lomicron.oikoumene.model.Color
import com.lomicron.oikoumene.model.map._
import com.lomicron.oikoumene.repository.api.AbstractRepository

trait MapRepository extends AbstractRepository[Color, Tile] {

  def setTerrainMapColorConf(mapTerrain: Seq[TerrainMapColorConf]): MapRepository
  def setTreeTerrainMapColorConf(mapTerrain: Seq[TerrainMapColorConf]): MapRepository
  def setTerrainMapColors(terrainColors: Array[Color]): MapRepository
  /**
    * @param terrainProvColors terrain color mapped to province color
    * @return updated repository
    */
  def setProvinceTerrainTypes(terrainProvColors: Map[Color, String]): MapRepository
  def rebuildTerrainColors(terrainColors: Array[Color]): MapRepository
  def rebuildTreeTerrainColors(treeTerrainColors: Array[Color]): MapRepository
  def terrainTypeOfTerrainColor(argb: Int): Option[String]
  def terrainTypeOfTerrainColor(color: Color): Option[String]
  def treeTerrainTypeOfTreeColor(argb: Int): Option[String]
  def treeTerrainTypeOfTreeColor(color: Color): Option[String]
  /**
    * @param argb province color
    * @return terrain id
    */
  def terrainMapTypeOfProvince(argb: Int): Option[String]
  /**
    * @param color province color
    * @return terrain id
    */
  def terrainMapTypeOfProvince(color: Color): Option[String]

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
  def createLakes(lakes: Seq[ElevatedLake]): MapRepository
  def lakes: Seq[ElevatedLake]

  def updatePositions(positions: Seq[ProvincePositions]): MapRepository
  def provincePositions: Seq[ProvincePositions]
  def positionsOf(ids: Seq[Int]): Seq[ProvincePositions] =
    ids.flatMap(positionsOf(_))
  def positionsOf(id: Int): Option[ProvincePositions]

}
