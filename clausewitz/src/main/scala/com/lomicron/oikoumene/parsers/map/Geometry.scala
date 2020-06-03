package com.lomicron.oikoumene.parsers.map

import java.lang.Math._

object Geometry {
  val halfPI: Double = PI / 2
  val twoPI: Double = PI * 2
  implicit def toDouble(bd: BigDecimal): Double = bd.toDouble

  def mercatorToSphere
  (
    polygons: Seq[Polygon],
    center: Point2D,
    radius: Double,
    offset: Point2D = Point2D(0, 0),
    rotation: Option[SphericalCoord] = None
  ): Seq[Polygon] =
    project(toSpherical(polygons, center, radius, offset, rotation), center)


  def toSpherical
  (
    polygons: Seq[Polygon],
    center: Point2D,
    radius: Double,
    offset: Point2D = Point2D(0, 0),
    rotation: Option[SphericalCoord] = None
  ): Seq[SphericalPolygon] =
    polygons
      .map(_.offset(offset))
      .map(_.toSpherical(center, radius))
      .map(p => rotation.map(p.rotate).getOrElse(p))

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
    val polar = halfPI  - (center.y - p.y) / radius
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

}


