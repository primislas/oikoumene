package com.lomicron.utils.projections

import com.lomicron.utils.geometry.Geometry.halfPI
import com.lomicron.utils.geometry._

import java.lang.Math.atan

object MercatorProjection extends CylindricalProjection {

  override def toSpherical
  (
    p: Point2D,
    equator: Double,
    greenwich: Double,
    radius: Double
  ): SphericalCoord = {
    val polar = halfPI - 2 * atan(Math.exp((equator - p.y) / radius))
    val azimuth = (p.x - greenwich) / radius
    SphericalCoord(radius, polar, azimuth)
  }

}
