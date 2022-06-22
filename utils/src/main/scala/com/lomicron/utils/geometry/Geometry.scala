package com.lomicron.utils.geometry

import java.lang.Math._

import com.lomicron.utils.collection.CollectionUtils._
import org.apache.commons.math3.fitting.{PolynomialCurveFitter, WeightedObservedPoint, WeightedObservedPoints}

import scala.annotation.tailrec

object Geometry {
  val halfPI: Double = PI / 2
  val threeHalfPI: Double = 3 * halfPI
  val twoPI: Double = PI * 2

  implicit def toDouble(bd: BigDecimal): Double = bd.toDouble

  def projectPolygons(polygons: Seq[SphericalPolygon], center: Point2D): Seq[Polygon] =
    polygons
      .filter(_.nonEmpty)
      .map(_.project(center))

  def project(ps: Seq[SphericalCoord], center: Point2D): Seq[Point2D] =
    ps.map(project(_, center))

  def project(p: SphericalCoord, center: Point2D): Point2D = {
    val polarRadius = p.r * sin(p.polar.abs)
    val projectedX = center.x - polarRadius * cos(p.azimuth)
    val projectedY = center.y - p.r * cos(p.polar)
    Point2D(projectedX, projectedY)
  }

  def fromMercator(ps: Seq[Point2D], center: Point2D, radius: Double): Seq[SphericalCoord] =
    ps.map(fromMercator(_, center, radius))

  def fromMercator(p: Point2D, center: Point2D, radius: Double): SphericalCoord = {
    val polar = halfPI - (center.y - p.y) / radius
    val azimuth = (p.x - center.x) / radius
    SphericalCoord(radius, polar, azimuth)
  }

  def toAlbersEqualAreaConicProjection(ps: Seq[SphericalCoord], radius: Double): Seq[Point2D] = {
    val lambda0 = PI * 10 / 180
    val phi0 = PI * 30 / 180
    val phi1 = PI * 43  / 180
    val phi2 = PI * 62  / 180

    val n = (sin(phi1) + sin(phi2)) / 2
    val C = cos(phi1) * cos(phi1) + 2 * n * sin(phi1)
    val rho0 = radius * sqrt(C - 2 * n * sin(phi0)) / n

    ps.map(p => {
//      val lambda = halfPI - p.polar
      val lambda = p.azimuth
      val theta = n * (lambda - lambda0)
//      val phi = p.azimuth
      val phi = halfPI - p.polar
      val rho = radius * sqrt(C - 2 * n * sin(phi)) / n

      val x = rho * sin(theta)
      val y = - (rho0 - rho * cos(theta))

      Point2D(x, y)
    })
  }

  def rotate(p: SphericalCoord, polarRotation: Double = 0, azimuthRotation: Double = 0): SphericalCoord = {

    val azimuthRot = toSphericalRange(p.azimuth + azimuthRotation)
    if (polarRotation == 0) return SphericalCoord(p.r, p.polar, azimuthRot)

    val x = p.r * abs(sin(p.polar)) * cos(azimuthRot)
    val y = p.r * abs(sin(p.polar)) * sin(azimuthRot)
    val z = p.r * cos(p.polar)

    val rotX = x
    val rotY = y * cos(polarRotation) - z * sin(polarRotation)
    val rotZ = y * sin(polarRotation) + z * cos(polarRotation)

    val rotPolar = acos(rotZ / p.r)
    val rotAzimuth = if (rotX == 0) {
      if (rotY < 0) 3 * halfPI
      else halfPI
    } else {
      val a = atan(rotY / rotX)
      if (rotX < 0) a + PI else a
    }

    SphericalCoord(p.r, toSphericalRange(rotPolar), toSphericalRange(rotAzimuth))
  }

  def toSphericalRange(angle: Double): Double = {
    var oa = angle
    while (oa < 0) oa = oa + twoPI
    while (oa > twoPI) oa = oa - twoPI
    oa
  }

  /**
    * Removes instances of double points, e.g. 2 identical points following each other
    *
    * @param ps input sequence
    * @return clean sequence
    */
  def clean[T](ps: Seq[T] = Seq.empty): Seq[T] =
    cleanRec(ps)

  @scala.annotation.tailrec
  def cleanRec[T]
  (
    remaining: Seq[T] = Seq.empty,
    cleaned: Seq[T] = Seq.empty,
    prevPoint: Option[T] = None
  ): Seq[T] =
    if (remaining.isEmpty) cleaned
    else {
      val recCleaned =
        if (prevPoint.contains(remaining.head)) cleaned
        else cleaned :+ remaining.head
      cleanRec(remaining.drop(1), recCleaned, remaining.headOption)
    }

  def cleanSameLinePoints(ps: Seq[Point2D], isClosed: Boolean = true): Seq[Point2D] =
    if (ps.length < 3) ps
    else {
      val p1 = ps.head
      val p2 = ps.drop(1).head
      val angle = p2.angleTo(p1)
      var cleaned = cleanSameLinePointsRec(Seq(p1), ps.drop(2), p2, angle)

      if (isClosed) {
        var tailAngle = p1.angleTo(cleaned.last)
        if (tailAngle == angle) {
          cleaned = cleaned.drop(1)
          val withoutTail = cleaned.dropRight(1)
          tailAngle = cleaned.last.angleTo(withoutTail.last)
          if (tailAngle == angle) cleaned = withoutTail
        }
      }

      cleaned
    }

  @tailrec
  def cleanSameLinePointsRec
  (
    cleaned: Seq[Point2D],
    remaining: Seq[Point2D],
    prevPoint: Point2D,
    prevAngle: Double,
  ): Seq[Point2D] =
    if (remaining.isEmpty) cleaned :+ prevPoint
    else {
      val point = remaining.head
      val angle = point.angleTo(prevPoint)
      val cleanedPs = if (angle == prevAngle) cleaned else cleaned :+ prevPoint
      cleanSameLinePointsRec(cleanedPs, remaining.drop(1), point, angle)
    }


  def fitQuadratic(ps: Seq[Point2D]): QuadPolynomial =
    fitQuadratic(Map(1.0 -> ps))

  def fitQuadratic(psByWeight: Map[Double, Seq[Point2D]]): QuadPolynomial = {
    val coeffs = fit(psByWeight, 2)
    QuadPolynomial(coeffs)
  }

  def fit(psByWeight: Map[Double, Seq[Point2D]], polynomialDegree: Int): Array[Double] = {
    val obs = new WeightedObservedPoints()
    psByWeight.foreach { case (weight, ps) => ps.foreach(p => obs.add(weight, p.x, p.y)) }
    val fitter = PolynomialCurveFitter.create(polynomialDegree)
    fitter.fit(obs.toList)
  }

  def weightedFit(ps: Seq[WeightedObservedPoint], polynomialDegree: Int = 2): QuadPolynomial = {
    if (ps.size == 1) {
      val l = Line.ofAngle(0.0, Point2D(ps.head))
      QuadPolynomial(-l.c, -l.cx, 0)
    } else if (ps.size == 2) {
      val l = Line.ofTwoPoints(Point2D(ps.head), Point2D(ps.last))
      QuadPolynomial(-l.c, -l.cx, 0)
    } else {
      val obs = new WeightedObservedPoints()
      ps.foreach(p => obs.add(p.getWeight, p.getX, p.getY))
      val fitter = PolynomialCurveFitter.create(polynomialDegree)
      val coeffs = fitter.fit(obs.toList)
      QuadPolynomial(coeffs)
    }
  }

  def findOrientation(ps: Seq[Point2D], center: Option[Point2D] = None): Double = {
    val angles = 12
    val angleStep = PI / angles
    val c = center.getOrElse(centroid(ps))
    (0 until angles)
      .map(_ * angleStep)
      .map(Line.ofAngle(_, c))
      .minBy(_.distance(ps))
      .angle
  }

  def findSegmentedOrientation(ps: Seq[Polygon], center: Point2D): Double = {
    val points = approximateBorder(ps)
    findOrientation(points, center)
  }

  def approximateBorder(ps: Seq[Polygon], segmentSize: Int = 5): Seq[Point2D] = {
    val xsegs = ps.flatMap(_.segments(segmentSize))
    val xsGroup = groupSegments(xsegs)
    val trimmedXsGroup = trimTinyBorderSegments(xsGroup)
    val xs = trimmedXsGroup.flatMap(_.minMaxPoints)

    val yps = if (trimmedXsGroup.length < xsGroup.length) {
      val trxs = trimmedXsGroup.map(_.x)
      val minX = trxs.min
      val maxX = trxs.max
      val halfSegment = segmentSize.toDouble / 2
      ps.map(_.filter(p => p.x > minX - halfSegment && p.x < maxX + halfSegment)).filter(_.nonEmpty)
    } else ps

    val ysegs = yps.flatMap(_.flipXY.segments(segmentSize))
    val ysGroup = groupSegments(ysegs)
    val trimmedYsGroup = trimTinyBorderSegments(ysGroup)
    val ys = trimmedYsGroup.flatMap(_.minMaxPoints).map(_.flipXY)

    val adjustedXs = xs
    // TODO this actually still needs to be addressed
    //  i.e. trim xs based on trimmed ys

    //      if (trimmedYsGroup.length < ysGroup.length) {
    //      val minY = trimmedYsGroup.head.x
    //      val maxY = trimmedYsGroup.last.x
    //      xs.map(p =>
    //        if (p.y < minY) Point2D(p.x, minY)
    //        else if (p.y > maxY) Point2D(p.x, maxY)
    //        else p
    //      )
    //    } else xs

    adjustedXs ++ ys
  }

  def rotatePoints(ps: Seq[Point2D], c: Point2D, angle: Double): Seq[Point2D] =
    ps.map(_.rotate(c, angle))

  def polygonCentroid(ps: Seq[Polygon]): Point2D = {
    val segments = ps.flatMap(_.segments())
    val avgY = segmentCentroid(segments)
    val flipped = ps.map(_.flipXY).flatMap(_.segments())
    val avgX = segmentCentroid(flipped)
    Point2D(avgX, avgY)
  }

  def groupSegments(ss: Seq[PointSegment]): Seq[PointSegment] = {
    ss
      .groupBy(_.x)
      .values.toList
      .map(segs => {
        var minY = Double.MaxValue
        var maxY = Double.MinValue
        segs.foreach(s => {
          if (s.min < minY) minY = s.min
          if (s.max > maxY) maxY = s.max
        })
        val avg = (minY + maxY) / 2.0
        val ps = segs.flatMap(_.ps)
        PointSegment(segs.head.x, minY, maxY, avg, ps)
      })
      .sortBy(_.x)
  }

  def trimTinyBorderSegments
  (
    segments: Seq[PointSegment],
    tinySegmentSize: Double = 5.0,
    maxTinySegmentShareLimit: Double = 0.2,
  ): Seq[PointSegment] = {
    var trimmed = segments
    val tinySegments = trimmed.count(_.range < tinySegmentSize).toDouble
    if (tinySegments / trimmed.length <= maxTinySegmentShareLimit) {
      trimmed = trimmed.dropWhile(_.range < tinySegmentSize)
      var r = trimmed.last.range
      while (r < tinySegmentSize) {
        trimmed = trimmed.dropRight(1)
        r = trimmed.last.range
      }
    }
    trimmed
  }

  def segmentCentroid(ss: Seq[PointSegment]): Double = {
    val averages = groupSegments(ss).map(_.avg)
    averages.sum / averages.length
  }

  def centroid(ps: Seq[Point2D]): Point2D = {
    val avgX = ps.map(_.x).sum / ps.size
    val avgY = ps.map(_.y).sum / ps.size
    Point2D(avgX, avgY)
  }

  def segmentedAvgY(ps: Seq[Point2D], segmentSize: Int = 5): Double = {
    val segmentAverages = ps
      .groupBy(p => (p.x / segmentSize).floor)
      .filterValues(_.nonEmpty)
      .mapKVtoValue((_, segPs) => {
        val ys = segPs.map(_.y)
        val max = ys.max
        val min = ys.min
        (max + min) / 2
      })
      .values.toList

    segmentAverages.sum / segmentAverages.size
  }

  def segmentedAvgX(ps: Seq[Point2D], segmentSize: Int = 5): Double = {
    val segmentAverages = ps
      .groupBy(p => (p.y / segmentSize).floor)
      .filterValues(_.nonEmpty)
      .mapKVtoValue((_, segPs) => {
        val xs = segPs.map(_.x)
        val max = xs.max
        val min = xs.min
        (max + min) / 2
      })
      .values.toList

    segmentAverages.sum / segmentAverages.size
  }

  def trimPointsToDistance(ps: Seq[Point2D], distance: Int = 5): Seq[Point2D] = {
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

  def sqr(x: Double): Double = x * x

}
