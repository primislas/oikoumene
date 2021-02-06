package com.lomicron.oikoumene.service.map

import com.lomicron.oikoumene.model.Color
import com.lomicron.oikoumene.model.map._
import com.lomicron.oikoumene.model.provinces.{Province, ProvinceTypes}
import com.lomicron.oikoumene.repository.api.RepositoryFactory
import com.lomicron.oikoumene.service.map.SvgMapStyles._
import com.lomicron.oikoumene.service.svg.SvgElements._
import com.lomicron.oikoumene.service.svg._
import com.lomicron.utils.collection.CollectionUtils.{MapEx, toOption}
import com.lomicron.utils.geometry.Geometry.halfPI
import com.lomicron.utils.geometry._
import com.typesafe.scalalogging.LazyLogging
import org.apache.commons.math3.fitting.WeightedObservedPoint

import java.lang.Math.PI
import scala.collection.immutable.ListSet

case class SvgMapService(repos: RepositoryFactory, settings: SvgMapSettings = SvgMapSettings.default) extends LazyLogging {
  private val defaultPrecision: Int = settings.pointDecimalPrecision

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

  def worldSvg
  (
    worldMap: WorldMap,
    settingOverrides: MapBuilderSettings = MapBuilderSettings.default,
  ): String = {

    val settings = MapModes.settingsOf(settingOverrides.mapMode, settingOverrides)
    if (settings.ownWastelands.contains(true))
      worldMap.recalculateWastelandOwners

    val map = worldMap.mercator.recalcCurves
    val precision: Int = settings.decimalPrecision
    val background = settings.svgBackground.map(SvgMapStyles.background(_, repos)).getOrElse(Seq.empty)
    val style = SvgMapStyles.styleOf(settings, repos)
    val provinces = provinceSvg(map, settings.mapMode, precision)
    val rivers = settings.includeRivers.filter(identity).map(_ => riverSvg(map.rivers, precision)).toSeq
    val names = settings.includeNames.filter(_.booleanValue()).toSeq.map(_ => nameSvg(worldMap))
    val borders = settings.includeBorders.filter(_.booleanValue()).toSeq.map(_ => borderSvg(map, precision))

    val worldSvg = Svg
      .svgHeader
      .copy(width = map.width, height = map.height)
      .add(background)
      .add(style)
      .add(provinces)
      .add(borders)
      .add(rivers)
      .add(names)

    worldSvg.toSvg
  }

  def riverSvg(rs: Seq[River], precision: Int = defaultPrecision): SvgElement = {
    val rsByClass = rs
      .flatMap(riverToSvg(_, precision))
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

  def riverToSvg(r: River, precision: Int = defaultPrecision): Seq[SvgElement] =
    r.path.map(riverSegmentToSvg(_, precision))

  def riverSegmentToSvg(rs: RiverSegment, precision: Int = defaultPrecision): SvgElement =
    path
      .copy(path = Svg.fromPolypath(rs.path, precision))
      .addClass(SvgMapClasses.ofRiver(rs))

  def provinceSvg(map: MercatorMap, mapMode: String, precision: Int = defaultPrecision): SvgElement = {
    val psByClass = map.provinces
      .map(provinceToSvg(_, mapMode, precision))
      .groupBy(_.classes.head)

    val groupsByClass = ProvinceTypes.list
      .map(t => {
        val ps = psByClass.getOrElse(t, Seq.empty)
        val children = ps.map(p => p.copy(classes = p.classes.drop(1)))
        group.copy(id = t, classes = ListSet(t), children = children)
      })
      .toSeq
      .filter(_.children.nonEmpty)

    provinceGroup.add(groupsByClass)
  }

  def provinceToSvg(shape: Shape, mapMode: String, precision: Int = defaultPrecision): SvgElement = {
    val polygon = shape.polygon.get
    val elem = polygon
      .provinceId
      .map(classesOfProvince(_, mapMode))
      .map(defaultProvincePolygon(shape, precision).addClasses(_))
      .getOrElse(defaultProvincePolygon(shape, precision))
    polygon
      .provinceId
      .map(buildProvTooltip)
      .map(elem.addTitle)
      .getOrElse(elem)
  }

  def classesOfProvince(pId: Int, mapMode: String): Seq[String] =
    repos.provinces.find(pId).toOption.map(SvgMapClasses.ofProvince(_, mapMode)).getOrElse(Seq.empty)

  def defaultProvincePolygon(shape: Shape, precision: Int = defaultPrecision): SvgElement = {
    val elem = SvgElement(
      tag = SvgTags.PATH,
      id = shape.provId.map(_.toString),
    )
    if (shape.clip.isEmpty) elem.copy(path = Svg.fromPolypath(shape.path, precision))
    else {
      val outer = Svg.fromPolypath(shape.path)
      val inners = shape.clip.map(_.path).map(Svg.fromPolypath(_, precision))
      val path = (outer +: inners).mkString(" ")
      elem.copy(path = path, fillRule = "evenodd")
    }
  }

  def buildProvTooltip(pId: Int): String =
    repos.provinces.find(pId).toOption
      .map(p => p.localisation.name.map(n => s"$n (#${p.id})").getOrElse(s"#${p.id}"))
      .map(_.replaceAll("&", "&amp;"))
      .getOrElse("UNDEFINED PROV")

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
      case ProvinceTypes.sea => oceanColor
      case ProvinceTypes.lake => lakeColor
      case _ => if (p.geography.isImpassable) wastelandColor else uncolonizedColor
    }.getOrElse(uncolonizedColor)

  def borderSvg(mercatorMap: MercatorMap, precision: Int = defaultPrecision): SvgElement =
    borderSvg(mercatorMap.borders, precision)

  def borderSvg(borders: Seq[Border], precision: Int): SvgElement = {
    val bordsByClass = borders.map(borderSvg(_, precision)).groupBy(_.classes.head)

    def bordsOfType(t: String): SvgElement = {
      val bs = bordsByClass.getOrElse(t, Seq.empty).map(_.copy(classes = ListSet.empty))
      group.copy(id = t, classes = ListSet(t), children = bs)
    }

    val mapBorders = bordsOfType(BorderTypes.MAP_BORDER)

    val countries = bordsOfType(BorderTypes.COUNTRY)
    val landAreas = bordsOfType(BorderTypes.LAND_AREA)
    val landBorders = bordsOfType(BorderTypes.LAND)
      .copy(id = "border-land-default", classes = ListSet.empty)
    val countryShores = bordsOfType(BorderTypes.COUNTRY_SHORE)
    val seaShores = bordsOfType(BorderTypes.SEA_SHORE)
    val lakeShores = bordsOfType(BorderTypes.LAKE_SHORE)

    val seaAreas = bordsOfType(BorderTypes.SEA_AREA)
    val seaBorders = bordsOfType(BorderTypes.SEA)
      .copy(id = "border-sea-default", classes = ListSet.empty)
    val lakes = bordsOfType(BorderTypes.LAKE)

    val land = group.copy(
      id = BorderTypes.LAND,
      classes = ListSet(BorderTypes.LAND),
      children = Seq(countries, countryShores, landAreas, landBorders, seaShores, lakeShores)
    )
    val seas = group.copy(
      id = BorderTypes.SEA,
      classes = ListSet(BorderTypes.SEA),
      children = Seq(seaAreas, seaBorders, lakes)
    )

    borderGroup.add(Seq(mapBorders, land, seas))
  }

  def borderSvg(b: Border, precision: Int): SvgElement = {
    val lp = b.left.flatMap(repos.provinces.findByColor)
    val rp = b.right.flatMap(repos.provinces.findByColor)
    val borderType = borderBetweenProvs(lp, rp)
    path
      .copy(path = Svg.fromPolypath(b.path, precision))
      .addClass(borderType)
  }

  def borderBetweenProvIds(p1: Option[Int], p2: Option[Int]): String =
    borderBetweenProvs(
      p1.flatMap(repos.provinces.find(_).toOption),
      p2.flatMap(repos.provinces.find(_).toOption)
    )

  def borderBetweenProvs(p1: Option[Province], p2: Option[Province]): String = {
    val borderTypeOpt = for {
      lProv <- p1
      rProv <- p2
    } yield borderBetween(lProv, rProv)

    if (p1.isDefined || p2.isDefined)
      borderTypeOpt.getOrElse(BorderTypes.MAP_BORDER)
    else
      BorderTypes.UNDEFINED
  }

  def borderBetween(a: Province, b: Province): String = {
    if (isCountryBorder(a, b)) BorderTypes.COUNTRY
    else if (isLandBorder(a, b))
      if (isAreaBorder(a, b)) BorderTypes.LAND_AREA
      else BorderTypes.LAND
    else if (isSeaBorder(a, b))
      if (isAreaBorder(a, b)) BorderTypes.SEA_AREA
      else BorderTypes.SEA
    else if (isSeaShoreBorder(a, b))
      if (anyHasOwner(a, b)) BorderTypes.COUNTRY_SHORE
      else BorderTypes.SEA_SHORE
    else if (isLakeShoreBorder(a, b))
      if (anyHasOwner(a, b)) BorderTypes.COUNTRY_SHORE
      else BorderTypes.LAKE_SHORE
    else if (isLakeBorder(a, b)) BorderTypes.LAKE
    else BorderTypes.UNDEFINED
  }

  def isCountryBorder(a: Province, b: Province): Boolean =
    isLandBorder(a, b) &&
      anyHasOwner(a, b) &&
      (a.state.owner != b.state.owner || (
        (a.geography.isImpassable || b.geography.isImpassable)
          && (!a.geography.isImpassable || !b.geography.isImpassable))
        )

  def isLandBorder(a: Province, b: Province): Boolean =
    a.isLand && b.isLand

  def isLandAreaBorder(a: Province, b: Province): Boolean =
    isLandBorder(a, b) && isAreaBorder(a, b)

  def anyHasOwner(a: Province, b: Province): Boolean =
    a.state.owner.isDefined || b.state.owner.isDefined

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
    if (polygons.isEmpty) {
      // possible in case of mods, where certain vanilla provinces do not exist on the map
      Seq.empty
    } else {

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

      //    val oddNames = Set("ENGLAND", "PEGU", "BALUCHISTAN", "MUSCOVY", "WALLACHIA", "DENMARK", "OTOMI", "PIMA", "TUNIS")
      //    if (oddNames.contains(name))
      //      printFittingMeta(c, o, rotation, height, ps, rotatedSegments, orderedBezier)

      Svg.textPath(groupId, orderedBezier, name, curveLength, fontSizeLimit)
    }
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
    cutoffSidePercentage: Double = MapBuilderConstants.FITTING_SIDE_CUTOFF_PERCENTAGE,
    minSegmentSizeLimit: Int = MapBuilderConstants.MIN_SEGMENT_COUNT,
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
    weightDiff: Double = MapBuilderConstants.WEIGHT_DIFF,
    minDistLimit: Double = MapBuilderConstants.MIN_SEGMENT_RANGE,
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

