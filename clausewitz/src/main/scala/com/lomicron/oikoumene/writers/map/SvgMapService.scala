package com.lomicron.oikoumene.writers.map

import com.lomicron.oikoumene.model.Color
import com.lomicron.oikoumene.model.map.ElevatedLake
import com.lomicron.oikoumene.model.provinces.{Province, ProvinceTypes}
import com.lomicron.oikoumene.parsers.map._
import com.lomicron.oikoumene.repository.api.RepositoryFactory
import com.lomicron.oikoumene.writers.map.SvgMapStyles._
import com.lomicron.oikoumene.writers.svg.SvgElements._
import com.lomicron.oikoumene.writers.svg.{Svg, SvgElement, SvgFill, SvgTags}
import com.lomicron.utils.collection.CollectionUtils.{MapEx, toOption}
import org.apache.commons.math3.fitting.WeightedObservedPoint

import scala.collection.immutable.ListSet

case class SvgMapService(repos: RepositoryFactory) {

  val provinceGroup: SvgElement = group.copy(
    id = SvgMapClasses.PROVINCE_GROUP,
    classes = ListSet(SvgMapClasses.PROVINCE)
  )
  val riverGroup: SvgElement = group.copy(
    id = SvgMapClasses.RIVER_GROUP,
    classes = ListSet(SvgMapClasses.RIVER),
  )
  val borderGroup: SvgElement = group.copy(
    id = SvgMapClasses.BORDER_GROUP,
    classes = ListSet(SvgMapClasses.BORDER)
  )

  def worldSvg(worldMap: WorldMap, mapMode: Option[String] = None): String = {

    val style = if (mapMode.contains(MapModes.TERRAIN)) physicalMapStyle else defaultMapStyle
    val names = nameSvg(worldMap)

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
      .add(names)
      .toSvg
  }

  def riverSvg(rs: Seq[River]): SvgElement = {
    val rsByClass = rs
      .flatMap(riverToSvg)
      .groupBy(_.classes.head)
      .mapKVtoValue((t, rs) =>
        group
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
        group.copy(id = t, classes = ListSet(t), children = ps.map(_.copy(classes = ListSet.empty)))
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
      group.copy(id = t, classes = ListSet(t), children = bs)
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

    val land = group.copy(
      id = BorderTypes.LAND,
      classes = ListSet(BorderTypes.LAND),
      children = Seq(countries, landAreas, landBorders, seaShores, lakeShores)
    )
    val seas = group.copy(
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
    group
      .copy(id = "elevated-lakes")
      .addClass(ProvinceTypes.elevatedLake)
      .addClass(BorderTypes.LAKE_SHORE)
      .add(svgLakes)
  }

  def nameSvg(worldMap: WorldMap): SvgElement = {
    val names = worldMap
      .ownerGroups
      .zipWithIndex.toList
      .flatMap(gi => countryNames(worldMap, gi._1, s"n${gi._2}"))
      .grouped(2).toList
      .sortBy(pp => textLength(pp.last))
      .flatten

    group.copy(id = "tag-names").add(names)
  }

  private def textLength(e: SvgElement): Double =
    e.flatMap(_.children.headOption)
      .flatMap(se => se.fontSize)
      .map(fs => fs.take(fs.length - 2))
      .map(_.toDouble)
      .getOrElse(0.0)

  def countryNames(worldMap: WorldMap, group: Seq[Province], groupId: String): Seq[SvgElement] = {
    val name = group.head.state.owner
      .flatMap(repos.tags.find(_).toOption)
      .flatMap(_.localisation.name)
      .map(_.toUpperCase)
      .getOrElse("UNDEFINED")
    val height = worldMap.mercator.height
    val bps = borderPoints(worldMap, group)

    var left: Point2D = Point2D(worldMap.mercator.width, 0)
    var right: Point2D = Point2D.ZERO
    var top: Point2D = Point2D.ZERO
    var bottom: Point2D = Point2D(0, worldMap.mercator.height)
    bps.foreach(p => {
      if (p.x < left.x) left = p
      if (p.y < bottom.y) bottom = p
      if (p.x > right.x) right = p
      if (p.y > top.y) top = p
    })

    val isVertical = top.y - bottom.y > right.x - left.x
    val ps = bps.map(_.reflectY(height))
    val data =
      if (isVertical) ps.map(p => Point2D(p.y, p.x))
      else ps

    val curve = Geometry.weightedFit(simpleSegmentBorders(data))
    val quadBezier =
      if (isVertical) curve
        .toBezier(top.reflectY(height).y, bottom.reflectY(height).y)
        .map(p => Point2D(p.y, p.x))
        .map(_.reflectY(height))
      else curve.toBezier(left.x, right.x).map(_.reflectY(height))
    val curveLength =
      if (isVertical) curve.distance(top.reflectY(height).y, bottom.reflectY(height).y)
      else curve.distance(left.x, right.x)

    val lengthCoef =
      if (curveLength > 100) 0.6
      else 0.1 * (8 - Math.pow(1.2, curveLength - 100))
    val textLength = curveLength * lengthCoef

    val orderedBezier =
      if (quadBezier.head.x > quadBezier.last.x) quadBezier.reverse
      else quadBezier

    Svg.textPath(groupId, orderedBezier, name, textLength.toInt)
  }

  def borderPoints(worldMap: WorldMap, group: Seq[Province]): Seq[Point2D] = {
    val ids = group.map(_.id).toSet
    val borders = worldMap.mercator.provinces
      .filter(_.provId.exists(ids.contains))
      .flatMap(_.borders)
    val provColors = borders.flatMap(b => Seq(b.left, b.right).flatten).toSet
    val ownersByColor = provColors
      .flatMap(repos.provinces.findByColor(_))
      .groupBy(_.color.toInt)
      .flatMapValues(_.headOption)
      .flatMapValues(_.state.owner)

    borders
      .filter(b => b.left.flatMap(ownersByColor.get) != b.right.flatMap(ownersByColor.get))
      .flatMap(_.points)
  }

  def trimPoints(ps: Seq[Point2D], distance: Int = 10): Seq[Point2D] = {
    if (ps.length < 1) Seq.empty
    else if (ps.length < 2) ps
    else {
      var currP = ps.head
      var nextP = currP
      var trimmed = Seq(currP)
      var remainingPoints = ps
      var d = 0.0
      while (remainingPoints.nonEmpty) {
        while (d < distance && remainingPoints.nonEmpty) {
          nextP = remainingPoints.head
          d = currP.distance(nextP)
          remainingPoints = remainingPoints.drop(1)
        }

        if (d >= distance) {
          trimmed = trimmed :+ nextP
          currP = nextP
          d = 0.0
        }
      }

      trimmed
    }
  }

  def segmentBorders(ps: Seq[Point2D], segment: Int = 5): Unit = {
    var segments = Seq.empty[Point2D]
    val startSegment = (ps.head.x / segment).floor

    var currSegment = startSegment
    var nextSegment = startSegment
    var nextP = ps.head
    var remaining = ps
    var currSegmentPs = Seq.empty[Point2D]

    while (remaining.nonEmpty) {

      while (remaining.nonEmpty && currSegment == nextSegment) {
        currSegmentPs = currSegmentPs :+ nextP
        nextP = remaining.head
        remaining = remaining.drop(1)
        nextSegment = (nextP.x / segment).floor
      }

      val x = 5 * currSegment + segment / 2
      val y =
        if (currSegmentPs.nonEmpty)
          currSegmentPs.map(_.x).sum / currSegmentPs.size
        else
          segments.lastOption.map(p => (p.y + nextP.y) / 2).getOrElse(nextP.y)
      segments = segments :+ Point2D(x, y)

    }


  }

  def simpleSegmentBorders(ps: Seq[Point2D], segmentSize: Int = 5): Seq[WeightedObservedPoint] = {
    def idSegment(p: Point2D): Double = (p.x / segmentSize).floor

    val segmented = ps
      .groupBy(idSegment)
      .filterValues(_.nonEmpty)
      .mapKVtoValue((segment, segPs) => {
        val ys = segPs.map(_.y)
        val max = ys.max
        val min = ys.min
        val y = (max + min) / 2
        val weight = max - min
        val x = segment * segmentSize + segmentSize.toDouble / 2
        new WeightedObservedPoint(weight, x, y)
      })
      .values.toList

    segmented
  }

}
