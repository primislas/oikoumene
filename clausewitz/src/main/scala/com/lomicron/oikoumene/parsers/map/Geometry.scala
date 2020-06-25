package com.lomicron.oikoumene.parsers.map

import java.lang.Math._

import org.apache.commons.math3.fitting.{PolynomialCurveFitter, WeightedObservedPoint, WeightedObservedPoints}

object Geometry {
  val halfPI: Double = PI / 2
  val twoPI: Double = PI * 2

  implicit def toDouble(bd: BigDecimal): Double = bd.toDouble

  def projectMercatorAsSphere
  (
    polygons: Seq[Polygon],
    center: Point2D,
    radius: Double,
    offset: Point2D = Point2D(0, 0),
    rotation: Option[SphericalCoord] = None
  ): Seq[Polygon] = {
    val sphere = toSpherical(polygons, center, radius, offset)
    val rotated = rotation.map(sphere.rotate).getOrElse(sphere)
    rotated.project
  }

  def toSpherical
  (
    polygons: Seq[Polygon],
    center: Point2D,
    radius: Double,
    offset: Point2D = Point2D(0, 0),
  ): SphericalMap = {
    val sps = polygons
      .map(_.offset(offset))
      .map(_.toSpherical(center, radius))
    SphericalMap(center, sps)
  }

  def project(polygons: Seq[SphericalPolygon], center: Point2D): Seq[Polygon] =
    polygons
      .filter(_.nonEmpty)
      .map(_.project(center))

  def project(p: SphericalCoord, center: Point2D): Point2D = {
    val polarRadius = p.r * sin(p.polar.abs)
    val projectedX = center.x - polarRadius * cos(p.azimuth)
    val projectedY = center.y - p.r * cos(p.polar)
    Point2D(projectedX, projectedY)
  }

  def fromMercator(p: Point2D, center: Point2D, radius: Double): SphericalCoord = {
    val polar = halfPI - (center.y - p.y) / radius
    val azimuth = (p.x - center.x) / radius
    SphericalCoord(radius, polar, azimuth)
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

  def fitQuadratic(ps: Seq[Point2D]): QuadPolynomial =
    fitQuadratic(Map(1.0 -> ps))

  def fitQuadratic(psByWeight: Map[Double, Seq[Point2D]]): QuadPolynomial = {
    val coeffs = fit(psByWeight, 2)
    QuadPolynomial(coeffs)
  }

  def fit(psByWeight: Map[Double, Seq[Point2D]], polynomialDegree: Int): Array[Double] = {
    val obs = new WeightedObservedPoints()
    psByWeight.foreach { case (weight, ps) => ps.foreach(p => obs.add(weight, p.x, p.y))}
    val fitter = PolynomialCurveFitter.create(polynomialDegree)
    fitter.fit(obs.toList)
  }

  def weightedFit(ps: Seq[WeightedObservedPoint], polynomialDegree: Int = 2): QuadPolynomial = {
    val obs = new WeightedObservedPoints()
    ps.foreach(p => obs.add(p.getWeight, p.getX, p.getY))
    val fitter = PolynomialCurveFitter.create(polynomialDegree)
    val coeffs = fitter.fit(obs.toList)
    QuadPolynomial(coeffs)
  }

  def centroid(ps: Seq[Point2D]): Point2D = {
    val x = ps.map(_.x).sum / ps.size
    val y = ps.map(_.y).sum / ps.size
    Point2D(x, y)
  }


}


