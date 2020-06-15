package com.lomicron.oikoumene.writers.map

import com.lomicron.oikoumene.model.Color
import com.lomicron.oikoumene.model.provinces.{Province, ProvinceTypes}
import com.lomicron.oikoumene.parsers.map._
import com.lomicron.oikoumene.repository.api.RepositoryFactory
import com.lomicron.oikoumene.writers.map.SvgMapStyles._
import com.lomicron.oikoumene.writers.svg.{Svg, SvgElement, SvgFill, SvgTags}
import com.lomicron.utils.collection.CollectionUtils.toOption

import scala.collection.immutable.ListSet

case class SvgMapService(repos: RepositoryFactory) {

  val emptyGroup: SvgElement = SvgElement(tag = SvgTags.GROUP)
  val provinceGroup: SvgElement = emptyGroup.copy(id = SvgMapClasses.PROVINCE_GROUP)
  val riverGroup: SvgElement = emptyGroup.copy(id = SvgMapClasses.RIVER_GROUP)
  val borderGroup: SvgElement = emptyGroup.copy(id = SvgMapClasses.BORDER_GROUP)
  val polyline: SvgElement = SvgElement(tag = SvgTags.POLYLINE)

  def worldSvg(worldMap: WorldMap, mapMode: Option[String] = None): String = {

    val style = if (mapMode.contains(MapModes.TERRAIN)) physicalMapStyle else defaultMapStyle

    val withMode = for {
      mercator <- worldMap.mercator
      mode <- mapMode
    } yield ofMode(mercator, mode)

    val rivers = riverSvg(worldMap.rivers)

    val borders = borderSvg(worldMap.mercator)

    Svg.svgHeader
      .add(style)
      .add(withMode.toSeq)
      .add(borders)
      .add(rivers)
      .toSvg
  }

  def riverSvg(rs: Seq[River]): SvgElement = {
    val riverPaths = rs.flatMap(riverToSvg)
    riverGroup.add(riverPaths)
  }

  def riverToSvg(r: River): Seq[SvgElement] =
    r.path.map(riverSegmentToSvg)

  def riverSegmentToSvg(rs: RiverSegment): SvgElement =
    polyline.copy(points = rs.points, classes = SvgMapClasses.ofRiver(rs))

  def ofMode(map: MercatorMap, mapMode: String): SvgElement = {
    val ps = map.provinces.map(provinceToSvg(_, mapMode))
    provinceGroup.add(ps)
  }

  def provinceToSvg(shape: Shape, mapMode: String): SvgElement = {
    val polygon = shape.withPolygon.polygon.get
    polygon
      .provinceId
      .flatMap(repos.provinces.find(_).toOption)
      .map(prov =>
        defaultProvincePolygon(polygon).copy(
          classes = SvgMapClasses.ofProvince(prov),
          fill = mapModeColor(prov, mapMode).map(SvgFill(_)),
        )
      )
      .getOrElse(defaultProvincePolygon(polygon))
  }

  def defaultProvincePolygon(polygon: Polygon): SvgElement =
    SvgElement(
      tag = SvgTags.POLYGON,
      id = polygon.provinceId.map(_.toString),
      classes = ListSet(SvgMapClasses.PROVINCE),
      points = polygon.points,
    )

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
    val svgBorders = borders.distinct.map(borderSvg)
    borderGroup.add(svgBorders)
  }

  def borderSvg(b: Border): SvgElement = {
    val lp = b.left.flatMap(repos.provinces.findByColor)
    val rp = b.right.flatMap(repos.provinces.findByColor)
    val borderTypeOpt = for {
      lProv <- lp
      rProv <- rp
    } yield borderBetween(lProv, rProv)

    val borderType = borderTypeOpt.getOrElse(BorderTypes.MAP_BORDER)
    polyline.copy(classes = ListSet(BorderTypes.BORDER, borderType), points = b.points)
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
    a.geography.area.exists(!b.geography.area.contains(_))

}
