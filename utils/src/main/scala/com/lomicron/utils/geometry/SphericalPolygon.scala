package com.lomicron.utils.geometry

case class SphericalPolygon
(
  points: Seq[SphericalCoord],
  color: Int,
  provinceId: Option[Int] = None
) {

  def isEmpty: Boolean = points.size < 2
  def nonEmpty: Boolean = !isEmpty

  def rotate(rotation: SphericalCoord): SphericalPolygon =
    rotate(rotation.polar, rotation.azimuth)

  def rotate(polarRot: Double, azimuthRot: Double): SphericalPolygon = {
    val rps = points.map(_.rotate(polarRot, azimuthRot))
    copy(points = rps)
  }

  def project(center: Point2D): Polygon = {
    val ps = points.filterNot(_.isInvisible).map(_.project(center))
    Polygon(ps, color, provinceId)
  }

}
