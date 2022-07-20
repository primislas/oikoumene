package com.lomicron.utils.projections

import com.lomicron.utils.geometry._

trait CylindricalProjection {

  def toSphericalShapes
  (
    ps: Seq[Shape],
    equator: Double,
    primeMeridian: Double,
    radius: Double
  ): Seq[SphericalShape] =
    ps.map(toSpherical(_, equator, primeMeridian, radius))

  def toSpherical
  (
    s: Shape,
    equator: Double,
    primeMeridian: Double,
    radius: Double
  ): SphericalShape = {
    val bs = s.borders.map(toSpherical(_, equator, primeMeridian, radius))
    val clipShapes = s.clipShapes.map(toSpherical(_, equator, primeMeridian, radius))

    SphericalShape(bs, s.provColor, s.provId, s.groupId, clipShapes)
  }

  def toSpherical
  (
    p: Polygon,
    equator: Double,
    primeMeridian: Double,
    radius: Double
  ): SphericalPolygon = {
    val ps = toSphericalPoints(p.points, equator, primeMeridian, radius)
    val clip = p.clip.map(toSpherical(_, equator, primeMeridian, radius))
    SphericalPolygon(ps, p.color, p.provinceId, clip)
  }

  def toSpherical
  (
    b: Border,
    equator: Double,
    primeMeridian: Double,
    radius: Double
  ): SphericalBorder = {
    val ps = toSphericalPoints(b.points, equator, primeMeridian, radius)
    SphericalBorder(ps, b.left, b.right, b.leftGroup, b.rightGroup)
  }

  def toSphericalPoints
  (
    ps: Seq[Point2D],
    equator: Double,
    primeMeridian: Double,
    radius: Double
  ): Seq[SphericalCoord] =
    ps.map(toSpherical(_, equator, primeMeridian, radius))

  def toSpherical
  (
    p: Point2D,
    equator: Double,
    greenwich: Double,
    radius: Double
  ): SphericalCoord

}
