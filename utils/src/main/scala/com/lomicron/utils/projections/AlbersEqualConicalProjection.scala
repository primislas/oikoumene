package com.lomicron.utils.projections

import com.lomicron.utils.geometry.Geometry.halfPI
import com.lomicron.utils.geometry.{Border, Point2D, Shape, SphericalBorder, SphericalCoord, SphericalShape}

import java.lang.Math.{cos, sin, sqrt}

object AlbersEqualConicalProjection {

  /**
    * Converts spherical coordinate to Albers equal-area conic coordinate.
    *
    * @param shape spherical shape
    * @param longitudeOfCenter aka λ0
    * @param latitudeOfCenter aka φ0
    * @param standardParallel1 aka φ1
    * @param standardParallel2 aka φ2
    * @return normalized 2D point
    */
  def from
  (
    shape: SphericalShape,
    longitudeOfCenter: Double,
    latitudeOfCenter: Double,
    standardParallel1: Double,
    standardParallel2: Double,
  ): Shape = {
    val borders = shape.borders.map(from(_, longitudeOfCenter, latitudeOfCenter, standardParallel1, standardParallel2))
    val clips = shape.clipShapes.map(from(_, longitudeOfCenter, latitudeOfCenter, standardParallel1, standardParallel2))
    Shape(borders, shape.provColor, shape.provId, shape.groupId, clipShapes = clips)
  }

  /**
    * Converts spherical coordinate to Albers equal-area conic coordinate.
    *
    * @param border spherical border
    * @param longitudeOfCenter aka λ0
    * @param latitudeOfCenter aka φ0
    * @param standardParallel1 aka φ1
    * @param standardParallel2 aka φ2
    * @return normalized 2D point
    */
  def from
  (
    border: SphericalBorder,
    longitudeOfCenter: Double,
    latitudeOfCenter: Double,
    standardParallel1: Double,
    standardParallel2: Double,
  ): Border = {
    val ps = from(border.points, longitudeOfCenter, latitudeOfCenter, standardParallel1, standardParallel2)
    Border(ps, border.left, border.right, border.leftGroup, border.rightGroup)
  }

  /**
    * Converts spherical coordinate to Albers equal-area conic coordinate.
    *
    * @param ps points, spherical coordinates
    * @param longitudeOfCenter aka λ0
    * @param latitudeOfCenter aka φ0
    * @param standardParallel1 aka φ1
    * @param standardParallel2 aka φ2
    * @return normalized 2D point
    */
  def from
  (
    ps: Seq[SphericalCoord],
    longitudeOfCenter: Double,
    latitudeOfCenter: Double,
    standardParallel1: Double,
    standardParallel2: Double,
  ): Seq[Point2D] = {
    val radius = ps.headOption.map(_.r).getOrElse(1.0)
    val n = nCoeff(standardParallel1, standardParallel2)
    val C = CCoeff(standardParallel1, n)
    val rho0 = rho0Coeff(radius, latitudeOfCenter, n, C)
    ps.map(from(_, longitudeOfCenter, standardParallel1, standardParallel2, n, C, rho0))
  }

  /**
    * Converts spherical coordinate to Albers equal-area conic coordinate.
    *
    * @param p point, spherical coordinate
    * @param longitudeOfCenter aka λ0
    * @param latitudeOfCenter aka φ0
    * @param standardParallel1 aka φ1
    * @param standardParallel2 aka φ2
    * @return normalized 2D point
    */
  def from
  (
    p: SphericalCoord,
    longitudeOfCenter: Double,
    latitudeOfCenter: Double,
    standardParallel1: Double,
    standardParallel2: Double,
  ): Point2D = {
    val n = nCoeff(standardParallel1, standardParallel2)
    val C = CCoeff(standardParallel1, n)
    val rho0 = rho0Coeff(p.r, latitudeOfCenter, n, C)
    from(p, longitudeOfCenter, standardParallel1, standardParallel2, n, C, rho0)
  }

  /**
    * Converts spherical coordinate to Albers equal-area conic coordinate.
    *
    * @param p point, spherical coordinate
    * @param longitudeOfCenter aka λ0
    * @param standardParallel1 aka φ1
    * @param standardParallel2 aka φ2
    * @param n n coefficient can be precalculated when a lot of points are transformed
    * @param C C coefficient can be precalculated when a lot of points are transformed
    * @param rho0 ρ0 coefficient can be precalculated when a lot of points are transformed
    * @return normalized 2D point
    */
  def from
  (
    p: SphericalCoord,
    longitudeOfCenter: Double,
    standardParallel1: Double,
    standardParallel2: Double,
    n: Double,
    C: Double,
    rho0: Double,
  ): Point2D = {
    val radius = p.r
    val lambda = p.azimuth
    val theta = n * (lambda - longitudeOfCenter)
    val phi = halfPI - p.polar
    val rho = radius * sqrt(C - 2 * n * sin(phi)) / n

    val x = rho * sin(theta)
    val y = - (rho0 - rho * cos(theta))

    Point2D(x, y)
  }

  def nCoeff(phi1: Double, phi2: Double): Double =
    (sin(phi1) + sin(phi2)) / 2

  def CCoeff(phi1: Double, n: Double): Double =
    cos(phi1) * cos(phi1) + 2 * n * sin(phi1)

  def rhoCoeff(radius: Double, phi: Double, n: Double, C: Double): Double =
    radius * sqrt(C - 2 * n * sin(phi)) / n

  def rho0Coeff(radius: Double, phi0: Double, n: Double, C: Double): Double =
    radius * sqrt(C - 2 * n * sin(phi0)) / n

}
