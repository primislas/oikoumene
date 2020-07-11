package com.lomicron.utils.geometry

case class SphericalBorder
(
  points: Seq[SphericalCoord] = Seq.empty,
  border: Border
)
extends TSphericalShape[SphericalBorder]
{

  override def setPoints(ps: Seq[SphericalCoord]): SphericalBorder =
    copy(points = ps)

  def project(center: Point2D): Border =
    border.copy(points = projectPoints(center))

}

object SphericalBorder {

  def apply(border: Border, center: Point2D, radius: Double): SphericalBorder =
    SphericalBorder(Geometry.fromMercator(border.points, center, radius), border)

}
