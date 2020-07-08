package com.lomicron.oikoumene.writers.map

import java.lang.Math.PI

import com.lomicron.oikoumene.model.Color
import com.lomicron.oikoumene.model.map._
import com.lomicron.oikoumene.model.provinces.{Province, ProvinceTypes}
import com.lomicron.oikoumene.repository.api.RepositoryFactory
import com.lomicron.oikoumene.writers.map.SvgMapStyles._
import com.lomicron.oikoumene.writers.svg.SvgElements._
import com.lomicron.oikoumene.writers.svg._
import com.lomicron.utils.collection.CollectionUtils.{MapEx, toOption}
import com.lomicron.utils.geometry.Geometry.halfPI
import com.lomicron.utils.geometry._
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

    val terrainStyles = s"/*\n$buildTerrainStyles\n*/\n"
    val styleWithTags = style
      .addContent(terrainStyles)
      .addContent(buildTagStyles)

    Svg.svgHeader
      .copy(width = worldMap.mercator.width, height = worldMap.mercator.height)
      .add(styleWithTags)
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
        val children = ps.map(p => p.copy(classes = p.classes.drop(1)))
        group.copy(id = t, classes = ListSet(t), children = children)
      })
      .toSeq

    provinceGroup.add(groupsByClass)
  }

  def provinceToSvg(shape: Shape, mapMode: String): SvgElement = {
    val polygon = shape.withPolygon.polygon.get
    polygon
      .provinceId
      .flatMap(repos.provinces.find(_).toOption)
      .map(SvgMapClasses.ofProvince)
      .map(defaultProvincePolygon(polygon).addClasses(_))
      .getOrElse(defaultProvincePolygon(polygon))
  }

  def defaultProvincePolygon(polygon: Polygon): SvgElement = {
    val isPathClosed = true
    val elem = SvgElement(
      tag = SvgTags.PATH,
      id = polygon.provinceId.map(_.toString),
    )
    if (polygon.clip.isEmpty) elem.copy(path = Svg.pointsToSvgLinearPath(polygon.points, isPathClosed))
    else {
      val outer = Svg.pointsToSvgLinearPath(polygon.points)
      val inners = polygon.clip.map(_.points).map(Svg.pointsToSvgLinearPath(_, isPathClosed))
      val path = (outer +: inners).mkString(" ")
      elem.copy(path = path, fillRule = "evenodd")
    }
  }

  def buildTagStyles: String =
    repos
      .provinces
      .findAll
      .flatMap(_.state.owner)
      .distinct
      .flatMap(repos.tags.find(_).toOption)
      .map(tag => s".${tag.id} { fill:${Svg.colorToSvg(tag.color)} }")
      .mkString("\n")

  def buildTerrainStyles: String =
    repos
    .geography
    .terrain
    .findAll
    .flatMap(t => t.color.map(c => s".${t.id} { fill:${Svg.colorToSvg(c)} }"))
    .mkString("\n")

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
      .flatMap(gi => provinceGroupName(worldMap, gi._1, s"n${gi._2}"))
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

  def provinceGroupName(worldMap: WorldMap, group: Seq[Province], groupId: String): Seq[SvgElement] = {
    val height = worldMap.mercator.height
    val name = mapName(group)

    val polygons = provinceShapes(worldMap, group).map(_.reflectY(height))
    val ps = Geometry.approximateBorder(polygons)
    val c = Geometry.centroid(ps)
    val o = Geometry.findOrientation(ps, c)

    // rotating shapes so that they are 'parallel' to x axis
    // (according to identified orientation)
    // for further analysis
    val rotation = if (o == 0.0 || o == PI) 0.0 else if (o > halfPI) PI - o else -o
    val rotatedShapes = rotate(polygons, c, rotation)
    val rotatedSegments = rotatedShapes.flatMap(_.segments())

    val namePolyline = toWeightedCentroidPolyline(rotatedSegments)
    val curve = Geometry.weightedFit(namePolyline)
    val orderedBezier = quadCurveToBezier(namePolyline, curve, c, rotation, height)
    val curveLength = orderedBezier.head.distance(orderedBezier.last)
    val fontSizeLimit = maxFontSize(rotatedSegments)

    val oddNames = Set("ENGLAND", "PEGU", "BALUCHISTAN", "MUSCOVY", "WALLACHIA", "DENMARK", "OTOMI", "PIMA", "TUNIS")
    if (oddNames.contains(name))
      printFittingMeta(c, o, rotation, height, ps, rotatedSegments, orderedBezier)

    Svg.textPath(groupId, orderedBezier, name, curveLength, fontSizeLimit)
  }

  def mapName(provinces: Seq[Province]): String =
    provinces.head.state.owner
      .flatMap(repos.tags.find(_).toOption)
      .flatMap(_.localisation.name)
      .map(_.toUpperCase)
      .getOrElse("UNDEFINED")

  def quadCurveToBezier
  (
    namePolyline: Seq[WeightedObservedPoint],
    curve: QuadPolynomial,
    c: Point2D,
    rotation: Double,
    height: Int,
  ): Seq[Point2D] = {
    val left = namePolyline.head.getX
    val right = namePolyline.last.getX
    val quadBezier = curve.toBezier(left, right)
    val bezierPoints = quadBezier.map(rotate(_, c, -rotation)).map(_.reflectY(height))
    if (bezierPoints.head.x > bezierPoints.last.x) bezierPoints.reverse
    else bezierPoints
  }

  def maxFontSize(segments: Seq[PointSegment]): Double = {
    val sizes = segments
      .groupBy(_.x).values.toList
      .map(s => s.maxBy(_.max).max - s.minBy(_.min).min)
      .filter(_ > 5.0)
      .sorted
    val p40 = (0.6 * sizes.length).floor.toInt
    if (sizes.nonEmpty) sizes.drop(p40).head
    else 0.0
  }

  def rotate[T <: Rotatable[T]](ps: Seq[T], center: Point2D, angle: Double): Seq[T] =
    ps.map(rotate(_, center, angle))

  def rotate[T <: Rotatable[T]](p: T, center: Point2D, angle: Double): T =
    if (angle == 0.0) p
    else if (angle == halfPI || angle == -halfPI) p.flipXY
    else p.rotate(center, angle)

  def provinceShapes(worldMap: WorldMap, group: Seq[Province]): Seq[Polygon] = {
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

    val countryBorders = borders.filter(b => b.left.flatMap(ownersByColor.get) != b.right.flatMap(ownersByColor.get))
    Polygon.groupBordersIntoShapes(countryBorders)
  }

  def toWeightedCentroidPolyline(segments: Seq[PointSegment]): Seq[WeightedObservedPoint] = {
    val segmented = Geometry.groupSegments(segments).sortBy(_.x)
    val trimmed = Geometry.trimTinyBorderSegments(segmented)
    val unweighted = cutoffBorderSegments(trimmed)
    segmentsToWeightedPoints(unweighted)
  }

  def cutoffBorderSegments
  (
    segments: Seq[PointSegment],
    cutoffSidePercentage: Double = MapSettings.FITTING_SIDE_CUTOFF_PERCENTAGE,
    minSegmentSizeLimit: Int = MapSettings.MIN_SEGMENT_COUNT,
  ): Seq[PointSegment] = {
    val left = segments.head.x
    val right = segments.last.x

    val length = right - left
    val cutoffLength = length * cutoffSidePercentage
    val minCutoff = left + cutoffLength
    val maxCutoff = right - cutoffLength
    val cutoffSegments = segments.filter(s => s.x >= minCutoff && s.x <= maxCutoff)

    if (cutoffSegments.size < minSegmentSizeLimit) segments else cutoffSegments
  }

  def segmentsToWeightedPoints
  (
    segments: Seq[PointSegment],
    weightDiff: Double = MapSettings.WEIGHT_DIFF,
    minDistLimit: Double = MapSettings.MIN_SEGMENT_RANGE,
  ): Seq[WeightedObservedPoint] = {
    val ranges = segments.map(_.range)
    var maxDist = ranges.max
    if (maxDist < minDistLimit) maxDist = minDistLimit
    val minDist = ranges.min
    val k =
      if (maxDist == minDist) 0.0
      else -(weightDiff - 1.0) / (maxDist - minDist)
    val b = weightDiff - k * minDist

    segments.map(s => new WeightedObservedPoint(k * s.range + b, s.x, s.avg))
  }

  //noinspection RedundantDefaultArgument
  def printFittingMeta
  (
    c: Point2D,
    o: Double,
    rotation: Double,
    height: Int,
    ps: Seq[Point2D],
    rotatedSegments: Seq[PointSegment],
    orderedBezier: Seq[Point2D],
  ): Unit = {
    val borderPoints = ps.map(p => Svg.circle(p.x, p.reflectY(height).y, 1.0).copy(fill = SvgFill(Color(0, 150, 200))))
    val border = SvgElements.group.add(borderPoints)
    val centroid = Svg.circle(c.x, c.reflectY(height).y, 3.0).copy(fill = SvgFill(Color(0, 150, 200)))

    val xs = ps.map(_.x)
    val left = xs.min
    val right = xs.max
    val l = Line.ofAngle(o, c)
    val svgOrientation = Seq(Point2D(left, l.at(left)), Point2D(right, l.at(right)))
      .map(_.reflectY(height))
    val oLine = SvgElements.path
      .copy(path = Svg.pointsToQuadraticPath(svgOrientation), strokeColor = Color(0, 200, 0), strokeWidth = 1, fill = SvgFill.none)

    val approximationTo = toWeightedCentroidPolyline(rotatedSegments)
      .map(p => Point2D(p.getX, p.getY))
      .map(rotate(_, c, -rotation))
      .map(_.reflectY(height))
    val polyline = SvgElements.polyline
      .copy(strokeColor = Color(200, 0, 0), strokeWidth = 1, points = approximationTo, fill = SvgFill.none)

    val svgBezier = Svg.pointsToQuadraticPath(orderedBezier)
    val result = SvgElements.path
      .copy(path = svgBezier, strokeColor = Color(0, 0, 200), strokeWidth = 1, fill = SvgFill.none)

    println(border.toSvg)
    println(centroid.toSvg)
    println(oLine.toSvg)
    println(polyline.toSvg)
    println(result.toSvg)
  }

}

