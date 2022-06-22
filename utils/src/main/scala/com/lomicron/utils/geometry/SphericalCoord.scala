package com.lomicron.utils.geometry

/**
  * Spherical coordinate.
  *
  * @param r original radius
  * @param polar polar coordinate, latitude φ = π/2 - azimuth
  * @param azimuth azimuth angle, longitude λ
  */
case class SphericalCoord
(
  r: Double,
  polar: Double,
  azimuth: Double,
) {

  def isInvisible: Boolean = isInvisible(polar) || isInvisible(azimuth)

  def isInvisible(angle: BigDecimal): Boolean = angle < 0 || angle > Math.PI

  def project(center: Point2D): Point2D =
    Geometry.project(this, center)

  def rotate(polarRotation: Double = 0, azimuthRotation: Double = 0): SphericalCoord =
    Geometry.rotate(this, polarRotation, azimuthRotation)

}
