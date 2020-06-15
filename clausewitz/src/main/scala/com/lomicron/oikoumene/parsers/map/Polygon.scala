package com.lomicron.oikoumene.parsers.map

case class Polygon
(
  points: Seq[Point2D],
  color: Int,
  provinceId: Option[Int] = None,
  clip: Seq[Polygon] = Seq.empty,
) {

  def isEmpty: Boolean = points.isEmpty
  def nonEmpty: Boolean = points.nonEmpty

  def toSpherical(center: Point2D, radius: Double): SphericalPolygon = {
    val ps = points.map(Geometry.fromMercator(_, center, radius))
    SphericalPolygon(ps, color, provinceId)
  }

  def offset(offset: Point2D): Polygon = {
    val ops = points.map(_.offset(offset))
    copy(points = ops)
  }

}
