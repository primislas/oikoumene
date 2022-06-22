package com.lomicron.utils.projections

import com.lomicron.utils.geometry.Geometry.halfPI
import com.lomicron.utils.geometry.{Point2D, SphericalCoord}

import java.lang.Math.atan

object BraunStereographicProjection extends CylindricalProjection {

  override def toSpherical
  (
    p: Point2D,
    equator: Double,
    greenwich: Double,
    radius: Double
  ): SphericalCoord = {
    val polar = halfPI - 2 * atan((equator - p.y) / (2 * radius))
    val azimuth = (p.x - greenwich) / radius
    SphericalCoord(radius, polar, azimuth)
  }

}
