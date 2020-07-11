package com.lomicron.oikoumene.model.map.spherical

import com.lomicron.oikoumene.model.map.RiverSegment
import com.lomicron.utils.geometry.{Geometry, Point2D, SphericalCoord, TSphericalShape}

case class SphericalRiverSegment(source: Int, width: Int, points: Seq[SphericalCoord])
extends TSphericalShape[SphericalRiverSegment]
{

  override def setPoints(ps: Seq[SphericalCoord]): SphericalRiverSegment =
    copy(points = ps)

  def project(center: Point2D): RiverSegment =
    RiverSegment(source, width, projectPoints(center))

}

object SphericalRiverSegment {
  def apply(riverSegment: RiverSegment, center: Point2D, radius: Double): SphericalRiverSegment =
    SphericalRiverSegment(
      riverSegment.source,
      riverSegment.width,
      Geometry.fromMercator(riverSegment.points, center, radius)
    )
}
