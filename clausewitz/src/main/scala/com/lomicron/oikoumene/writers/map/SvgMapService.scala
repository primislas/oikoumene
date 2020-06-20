package com.lomicron.oikoumene.writers.map

import com.lomicron.oikoumene.model.Color
import com.lomicron.oikoumene.model.map.ElevatedLake
import com.lomicron.oikoumene.model.provinces.{Province, ProvinceTypes}
import com.lomicron.oikoumene.parsers.map._
import com.lomicron.oikoumene.repository.api.RepositoryFactory
import com.lomicron.oikoumene.writers.map.SvgMapStyles._
import com.lomicron.oikoumene.writers.svg.{Svg, SvgElement, SvgFill, SvgTags}
import com.lomicron.utils.collection.CollectionUtils.toOption

import scala.collection.immutable.ListSet
import com.lomicron.utils.collection.CollectionUtils.MapEx

case class SvgMapService(repos: RepositoryFactory) {

  val emptyGroup: SvgElement = SvgElement(tag = SvgTags.GROUP)
  val provinceGroup: SvgElement = emptyGroup.copy(
    id = SvgMapClasses.PROVINCE_GROUP,
    classes = ListSet(SvgMapClasses.PROVINCE)
  )
  val riverGroup: SvgElement = emptyGroup.copy(
    id = SvgMapClasses.RIVER_GROUP,
    classes = ListSet(SvgMapClasses.RIVER),
  )
  val borderGroup: SvgElement = emptyGroup.copy(
    id = SvgMapClasses.BORDER_GROUP,
    classes = ListSet(SvgMapClasses.BORDER)
  )
  val polygon: SvgElement = SvgElement(tag = SvgTags.POLYGON)
  val polyline: SvgElement = SvgElement(tag = SvgTags.POLYLINE)
  val path: SvgElement = SvgElement(tag = SvgTags.PATH)

  def worldSvg(worldMap: WorldMap, mapMode: Option[String] = None): String = {

    val style = if (mapMode.contains(MapModes.TERRAIN)) physicalMapStyle else defaultMapStyle

    val withMode = for {
      mercator <- worldMap.mercator
      mode <- mapMode
    } yield ofMode(mercator, mode)

    val rivers = riverSvg(worldMap.rivers)
    val borders = borderSvg(worldMap.mercator)
    //    val lakes = lakeSvg(worldMap.lakes, worldMap.mercator.height)

    Svg.svgHeader
      .copy(width = worldMap.mercator.width, height = worldMap.mercator.height)
      .add(style)
      .add(withMode.toSeq)
      .add(borders)
      .add(rivers)
      //      .add(lakes)
      .toSvg
  }

  def riverSvg(rs: Seq[River]): SvgElement = {
    val rsByClass = rs
      .flatMap(riverToSvg)
      .groupBy(_.classes.head)
      .mapKVtoValue((t, rs) =>
        emptyGroup
          .copy(id = t)
          .add(rs.map(_.clearClasses))
          .addClass(t)
      )
      .values.toSeq

    riverGroup.add(rsByClass)
  }

  def riverToSvg(r: River): Seq[SvgElement] =
    r.path.map(riverSegmentToSvg)

  def riverSegmentToSvg(rs: RiverSegment): SvgElement =
    path
      .copy(path = Svg.pointsToSvgLinearPath(rs.points))
      .addClass(SvgMapClasses.ofRiver(rs))

  def ofMode(map: MercatorMap, mapMode: String): SvgElement = {
    val psByClass = map.provinces
      .map(provinceToSvg(_, mapMode))
      .groupBy(_.classes.head)

    val groupsByClass = ProvinceTypes.list
      .map(t => {
        val ps = psByClass.getOrElse(t, Seq.empty)
        emptyGroup.copy(id = t, classes = ListSet(t), children = ps.map(_.copy(classes = ListSet.empty)))
      })
      .toSeq

    provinceGroup.add(groupsByClass)
  }

  def provinceToSvg(shape: Shape, mapMode: String): SvgElement = {
    val polygon = shape.withPolygon.polygon.get
    polygon
      .provinceId
      .flatMap(repos.provinces.find(_).toOption)
      .map(prov =>
        defaultProvincePolygon(polygon).copy(
          classes = ListSet(SvgMapClasses.ofProvince(prov)),
          fill = mapModeColor(prov, mapMode).map(SvgFill(_)),
        )
      )
      .getOrElse(defaultProvincePolygon(polygon))
  }

  def defaultProvincePolygon(polygon: Polygon): SvgElement = {
    val isPathClosed = true
    val elem = SvgElement(
      tag = SvgTags.PATH,
      id = polygon.provinceId.map(_.toString),
      classes = ListSet(SvgMapClasses.PROVINCE),
    )
    if (polygon.clip.isEmpty) elem.copy(path = Svg.pointsToSvgLinearPath(polygon.points, isPathClosed))
    else {
      val outer = Svg.pointsToSvgLinearPath(polygon.points)
      val inners = polygon.clip.map(_.points).map(Svg.pointsToSvgLinearPath(_, isPathClosed))
      val path = (outer +: inners).mkString(" ")
      elem.copy(path = path, fillRule = "evenodd")
    }
  }

  def mapModeColor(p: Province, mapMode: String): Option[Color] =
    mapMode match {
      case MapModes.POLITICAL => ownerColor(p)
      case MapModes.TRADE_NODES => tradeNodeColor(p)
      case MapModes.SIMPLE_TERRAIN => simpleTerrainColor(p)
      case _ => None
    }

  def ownerColor(p: Province): Option[Color] =
    p.state.owner.flatMap(repos.tags.find(_).toOption).map(_.color)

  def tradeNodeColor(p: Province): Option[Color] =
    p.geography.tradeNode
      .flatMap(repos.tradeNodes.find(_).toOption)
      .flatMap(_.color)

  def simpleTerrainColor(p: Province): Option[Color] =
    p.geography.terrain
      .flatMap(repos.geography.terrain.find(_).toOption)
      .flatMap(_.color)

  def defaultColor(p: Province): Color =
    p.geography.`type`.map {
      case ProvinceTypes.sea => seaColor
      case ProvinceTypes.lake => lakeColor
      case _ => if (p.geography.isImpassable) wastelandColor else uncolonizedColor
    }.getOrElse(uncolonizedColor)

  def borderSvg(mercatorMap: MercatorMap): SvgElement = {
    val bs = mercatorMap.provinces.flatMap(_.borders)
    borderSvg(bs)
  }

  def borderSvg(borders: Seq[Border]): SvgElement = {
    val bordsByClass = borders.distinct
      .map(borderSvg)
      .groupBy(_.classes.head)

    def bordsOfType(t: String): SvgElement = {
      val bs = bordsByClass.getOrElse(t, Seq.empty).map(_.copy(classes = ListSet.empty))
      emptyGroup.copy(id = t, classes = ListSet(t), children = bs)
    }

    val mapBorders = bordsOfType(BorderTypes.MAP_BORDER)

    val countries = bordsOfType(BorderTypes.COUNTRY)
    val landAreas = bordsOfType(BorderTypes.LAND_AREA)
    val landBorders = bordsOfType(BorderTypes.LAND)
      .copy(id = "border-land-default", classes = ListSet.empty)
    val seaShores = bordsOfType(BorderTypes.SEA_SHORE)
    val lakeShores = bordsOfType(BorderTypes.LAKE_SHORE)

    val seaAreas = bordsOfType(BorderTypes.SEA_AREA)
    val seaBorders = bordsOfType(BorderTypes.SEA)
      .copy(id = "border-sea-default", classes = ListSet.empty)
    val lakes = bordsOfType(BorderTypes.LAKE)

    val land = emptyGroup.copy(
      id = BorderTypes.LAND,
      classes = ListSet(BorderTypes.LAND),
      children = Seq(countries, landAreas, landBorders, seaShores, lakeShores)
    )
    val seas = emptyGroup.copy(
      id = BorderTypes.SEA,
      classes = ListSet(BorderTypes.SEA),
      children = Seq(seaAreas, seaBorders, lakes)
    )

    borderGroup.add(Seq(mapBorders, land, seas))
  }

  def borderSvg(b: Border): SvgElement = {
    val lp = b.left.flatMap(repos.provinces.findByColor)
    val rp = b.right.flatMap(repos.provinces.findByColor)
    val borderTypeOpt = for {
      lProv <- lp
      rProv <- rp
    } yield borderBetween(lProv, rProv)

    val borderType = borderTypeOpt.getOrElse(BorderTypes.MAP_BORDER)
    path.copy(classes = ListSet(borderType), path = Svg.pointsToSvgLinearPath(b.points))
  }

  def borderBetween(a: Province, b: Province): String = {
    if (isCountryBorder(a, b)) BorderTypes.COUNTRY
    else if (isLandBorder(a, b))
      if (isAreaBorder(a, b)) BorderTypes.LAND_AREA
      else BorderTypes.LAND
    else if (isSeaBorder(a, b))
      if (isAreaBorder(a, b)) BorderTypes.SEA_AREA
      else BorderTypes.SEA
    else if (isSeaShoreBorder(a, b)) BorderTypes.SEA_SHORE
    else if (isLakeShoreBorder(a, b)) BorderTypes.LAKE_SHORE
    else if (isLakeBorder(a, b)) BorderTypes.LAKE
    else BorderTypes.UNDEFINED
  }

  def isCountryBorder(a: Province, b: Province): Boolean =
    isLandBorder(a, b) && (a.state.owner.isDefined || b.state.owner.isDefined) &&
      (a.state.owner != b.state.owner)

  def isLandBorder(a: Province, b: Province): Boolean =
    a.isLand && b.isLand

  def isLandAreaBorder(a: Province, b: Province): Boolean =
    isLandBorder(a, b) && isAreaBorder(a, b)

  def isSeaBorder(a: Province, b: Province): Boolean =
    a.`type` == ProvinceTypes.sea && b.`type` == ProvinceTypes.sea

  def isSeaAreaBorder(a: Province, b: Province): Boolean =
    isSeaBorder(a, b) && isAreaBorder(a, b)

  def isSeaShoreBorder(a: Province, b: Province): Boolean =
    (a.isLand && b.`type` == ProvinceTypes.sea) || (b.isLand && a.`type` == ProvinceTypes.sea)

  def isLakeBorder(a: Province, b: Province): Boolean =
    a.`type` == ProvinceTypes.lake && b.`type` == ProvinceTypes.lake

  def isLakeShoreBorder(a: Province, b: Province): Boolean =
    (a.isLand && b.`type` == ProvinceTypes.lake) || (b.isLand && a.`type` == ProvinceTypes.lake)

  def isAreaBorder(a: Province, b: Province): Boolean =
    (a.geography.area.isDefined || b.geography.area.isDefined) &&
      a.geography.area != b.geography.area

  def lakeSvg(lakes: Seq[ElevatedLake], mapHeight: Option[Int] = None): SvgElement = {
    val svgLakes = lakes
      .filter(_.triangleStrip.nonEmpty)
      .map(l => polygon.copy(points = l.asPolygon(mapHeight)))
    emptyGroup
      .copy(id = "elevated-lakes")
      .addClass(ProvinceTypes.elevatedLake)
      .addClass(BorderTypes.LAKE_SHORE)
      .add(svgLakes)
  }

}
