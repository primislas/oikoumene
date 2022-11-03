package com.lomicron.vicky.repository.inmemory

import com.lomicron.oikoumene.model.Color
import com.lomicron.oikoumene.model.map.{Adjacency, ElevatedLake, MercatorMap, ProvincePositions, River, Route, RouteTypes, TerrainMapColorConf, Tile, TileRoute}
import com.lomicron.oikoumene.model.provinces.ProvinceTypes
import com.lomicron.oikoumene.repository.inmemory.InMemoryCrudRepository
import com.lomicron.utils.collection.CollectionUtils.MapEx
import com.lomicron.vicky.model.province.Province
import com.lomicron.vicky.repository.api.{MapRepository, ProvinceRepository}

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
  private var terrainProvColors: Map[Color, Color] = Map.empty
  private var terrainColors: Array[Color] = Array.empty
  private var _adjacencies: Seq[Adjacency] = Seq.empty
  private var _tileRoutes: Seq[TileRoute] = Seq.empty
  private var routesByProvId: Map[Int, Seq[Route]] = Map.empty
  private var _mercator: MercatorMap = MercatorMap()
  private var _rivers: Seq[River] = Seq.empty
  private var _lakes: Seq[ElevatedLake] = Seq.empty
  private var _positions: Map[Int, ProvincePositions] = Map.empty

  override def setTerrainMapColorConf(mapTerrain: Seq[TerrainMapColorConf]): MapRepository = {
    this.terrainById = mapTerrain.map(mt => (mt.id, mt)).toMap
    this
  }

  override def setTerrainMapColors(terrainColors: Array[Color]): MapRepository = {
    this.terrainColors = terrainColors
    this
  }

  override def setTerrainProvinceColors(terrainProvColors: Map[Color, Color]): MapRepository = {
    this.terrainProvColors = terrainProvColors
    this
  }

  override def rebuildTerrainColors(terrainColors: Array[Color] = this.terrainColors): MapRepository = {
    this.terrainColors = terrainColors
    this.terrainById = this.terrainById
      .mapValuesEx(mt => Try(this.terrainColors(mt.colorIndex)).map(mt.withColor).getOrElse(mt))
    terrainByColor = this.terrainById.values.filter(_.color.isDefined).map(t => (t.color.get, t)).toMap
    this
  }

  override def terrainMapType(argb: Int): Option[String] =
    terrainMapType(Color(argb))

  override def terrainMapType(color: Color): Option[String] =
    this.terrainProvColors
      .get(color)
      .flatMap(terrainByColor.get)
      .map(_.terrainType)

  override def updateAdjacencies(as: Seq[Adjacency]): MapRepository = {
    this._adjacencies = as
    this
  }

  override def adjacencies: Seq[Adjacency] = this._adjacencies

  override def updateTileRoutes(routes: Seq[TileRoute]): MapRepository = {
    this._tileRoutes = routes
    this
  }

  override def tileRoutes: Seq[TileRoute] =
    this._tileRoutes

  override def updateRoutes(routes: Seq[Route]): MapRepository = {
    routesByProvId = routes.groupBy(_.from)
    this
  }


  override def routes: Map[Int, Seq[Route]] = routesByProvId

  override def provinceRoutes(provId: Int): Seq[Route] =
    routesByProvId.getOrElse(provId, Seq.empty)

  override def buildRoutes(provinces: ProvinceRepository): MapRepository = {
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
    } yield routeBetween(source, target)

  def routeBetween(source: Province, target: Province): Route = {
    val rType = (source.geography.provinceType, target.geography.provinceType) match {
      case (ProvinceTypes.province, ProvinceTypes.province) => RouteTypes.LAND
      case (ProvinceTypes.province, ProvinceTypes.sea) => RouteTypes.BOARDING
      case (ProvinceTypes.sea, ProvinceTypes.province) => RouteTypes.LANDING
      case (ProvinceTypes.sea, ProvinceTypes.sea) => RouteTypes.SEA
      case _ => RouteTypes.IMPASSABLE
    }

    Route(source.id, target.id, rType)
  }

  def adjacenciesToRoutes(provinces: ProvinceRepository): Seq[Route] = {
    val rType = RouteTypes.CROSSING
    this._adjacencies
      .flatMap(a => for {
        source <- provinces.find(a.from).toOption
        target <- provinces.find(a.to).toOption
      } yield Seq(Route(source.id, target.id, rType), Route(target.id, source.id, rType)))
      .flatten
      .toList
  }

  override def updateMercator(mercator: MercatorMap): MapRepository = {
    this._mercator = mercator
    this
  }

  override def mercator: MercatorMap =
    this._mercator

  override def createRivers(rivers: Seq[River]): MapRepository = {
    this._rivers = rivers
    this
  }

  override def rivers: Seq[River] = this._rivers

  override def createLakes(lakes: Seq[ElevatedLake]): MapRepository = {
    this._lakes = lakes
    this
  }

  override def lakes: Seq[ElevatedLake] = this._lakes

  override def updatePositions(positions: Seq[ProvincePositions]): MapRepository = {
    this._positions = positions.groupBy(_.id).mapValuesEx(_.head)
    this
  }

  override def provincePositions: Seq[ProvincePositions] =
    _positions.values.toSeq

  override def positionsOf(id: Int): Option[ProvincePositions] =
    _positions.get(id)

}
