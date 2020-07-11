package com.lomicron.utils.geometry

case class SphericalPolygon
(
  points: Seq[SphericalCoord],
  color: Int,
  provinceId: Option[Int] = None,
  clip: Seq[SphericalPolygon] = Seq.empty,
) extends TSphericalShape[SphericalPolygon]
{

  override def setPoints(ps: Seq[SphericalCoord]): SphericalPolygon =
    copy(points = ps)

  override def rotate(polarRot: Double, azimuthRot: Double): SphericalPolygon = {
    val rotated = super.rotate(polarRot, azimuthRot)
    val rotatedClips = clip.map(_.rotate(polarRot, azimuthRot))
    rotated.copy(clip = rotatedClips)
  }

  def project(center: Point2D): Polygon = {
    val ps = projectPoints(center)
    val clips = clip.map(_.project(center))
    Polygon(ps, color, provinceId, clips)
  }

}

object SphericalPolygon {
  def apply
  (
    polygon: Polygon,
    center: Point2D,
    radius: Double,
  ): SphericalPolygon =
    SphericalPolygon(
      Geometry.fromMercator(polygon.points, center, radius),
      polygon.color,
      polygon.provinceId,
      polygon.clip.map(SphericalPolygon(_, center, radius))
    )
}
