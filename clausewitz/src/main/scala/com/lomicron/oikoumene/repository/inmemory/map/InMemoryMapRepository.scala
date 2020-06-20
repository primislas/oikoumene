package com.lomicron.oikoumene.repository.inmemory.map

import com.lomicron.oikoumene.model.Color
import com.lomicron.oikoumene.model.map._
import com.lomicron.oikoumene.parsers.map.{MercatorMap, River}
import com.lomicron.oikoumene.repository.api.map.{MapRepository, ProvinceRepository}
import com.lomicron.oikoumene.repository.inmemory.InMemoryCrudRepository

import scala.collection.immutable.SortedMap
import scala.util.Try

case class InMemoryMapRepository()
  extends InMemoryCrudRepository[Color, Tile](t => Option(t.color))
    with MapRepository
{
  override def setId(entity: Tile, id: Color): Tile = entity.copy(color = id)

  override def findNames(keys: Seq[Color]): SortedMap[Color, String] = SortedMap.empty

  private var terrainById: Map[String, TerrainMapColorConf] = Map.empty
  private var terrainByColor: Map[Color, TerrainMapColorConf] = Map.empty
  private var terrainColors: Array[Color] = Array.empty
  private var _adjacencies: Seq[Adjacency] = Seq.empty
  private var _tileRoutes: Seq[TileRoute] = Seq.empty
  private var routesByProvId: Map[Int, Seq[Route]] = Map.empty
  private var _mercator: MercatorMap = MercatorMap()
  private var _rivers: Seq[River] = Seq.empty
  private var _positions: Map[Int, ProvincePositions] = Map.empty

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

  def updateAdjacencies(as: Seq[Adjacency]): MapRepository = {
    this._adjacencies = as
    this
  }

  def adjacencies: Seq[Adjacency] = this._adjacencies

  def updateTileRoutes(routes: Seq[TileRoute]): MapRepository = {
    this._tileRoutes = routes
    this
  }

  def tileRoutes: Seq[TileRoute] =
    this._tileRoutes

  def updateRoutes(routes: Seq[Route]): MapRepository = {
    routesByProvId = routes.groupBy(_.from)
    this
  }


  override def routes: Map[Int, Seq[Route]] = routesByProvId

  def provinceRoutes(provId: Int): Seq[Route] =
    routesByProvId.getOrElse(provId, Seq.empty)

  def buildRoutes(provinces: ProvinceRepository): MapRepository = {
    val trs = buildTileRoutes(provinces)
    val ars = adjacenciesToRoutes(provinces)
    updateRoutes(trs ++ ars)

    this
  }

  def buildTileRoutes(provinces: ProvinceRepository): Seq[Route] =
    this._tileRoutes
      .flatMap(r => Seq(r, r.inverse))
      .flatMap(buildRoute(_, provinces))

  def buildRoute(r: TileRoute, provinces: ProvinceRepository): Option[Route] =
    for {
      source <- provinces.findByColor(r.source)
      target <- provinces.findByColor(r.target)
    } yield Route(source, target)

  def adjacenciesToRoutes(provinces: ProvinceRepository): Seq[Route] = {
    val rType = RouteTypes.CROSSING
    this._adjacencies
      .flatMap(a => for {
        source <- provinces.find(a.from).toOption
        target <- provinces.find(a.to).toOption
      } yield Seq(Route(source.id, target.id, rType), Route(target.id, source.id, rType)))
      .flatten
  }

  def updateMercator(mercator: MercatorMap): MapRepository = {
    this._mercator = mercator
    this
  }

  def mercator: MercatorMap =
    this._mercator

  def createRivers(rivers: Seq[River]): MapRepository = {
    this._rivers = rivers
    this
  }

  def rivers: Seq[River] = this._rivers

  override def updatePositions(positions: Seq[ProvincePositions]): MapRepository = {
    this._positions = positions.groupBy(_.id).mapValues(_.head)
    this
  }

  override def provincePositions: Seq[ProvincePositions] =
    _positions.values.toSeq

  override def positionsOf(id: Int): Option[ProvincePositions] =
    _positions.get(id)
}
