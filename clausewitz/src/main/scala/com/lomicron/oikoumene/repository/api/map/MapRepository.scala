package com.lomicron.oikoumene.repository.api.map

import com.lomicron.oikoumene.model.Color
import com.lomicron.oikoumene.model.map.{TerrainMapColorConf, Tile}
import com.lomicron.oikoumene.parsers.map.{Point2D, Polygon, SphericalMap}
import com.lomicron.oikoumene.repository.api.AbstractRepository

import scala.util.Try

trait MapRepository extends AbstractRepository[Color, Tile] {

  private var terrainById: Map[String, TerrainMapColorConf] = Map.empty
  private var terrainByColor: Map[Color, TerrainMapColorConf] = Map.empty
  private var terrainColors: Array[Color] = Array.empty
  private var _mercator: Seq[Polygon] = Seq.empty
  private var _sphere: SphericalMap = SphericalMap(Point2D())

  def setTerrainMapColorConf(mapTerrain: Seq[TerrainMapColorConf]): MapRepository = {
    this.terrainById = mapTerrain.map(mt => (mt.id, mt)).toMap
    this
  }

  def setTerrainMapColors(terrainColors: Array[Color]): MapRepository = {
    this.terrainColors = terrainColors
    this
  }

  def rebuildTerrainColors(terrainColors: Array[Color] = this.terrainColors): MapRepository = {
    this.terrainColors = terrainColors
    this.terrainById = this.terrainById
      .mapValues(mt => Try(this.terrainColors(mt.colorIndex)).map(mt.withColor).getOrElse(mt))
    terrainByColor = this.terrainById.values.filter(_.color.isDefined).map(t => (t.color.get, t)).toMap
    this
  }

  def terrainMapType(argb: Int): Option[String] =
    terrainMapType(Color(argb))

  def terrainMapType(color: Color): Option[String] =
    this.terrainByColor.get(color).map(_.terrainType)

  def setMercator(mercator: Seq[Polygon]): MapRepository = {
    this._mercator = mercator
    this
  }

  def mercator: Seq[Polygon] =
    this._mercator

  def setSphericalMap(sphericalMap: SphericalMap): MapRepository = {
    this._sphere = sphericalMap
    this
  }

  def spherical: SphericalMap = this._sphere

}
