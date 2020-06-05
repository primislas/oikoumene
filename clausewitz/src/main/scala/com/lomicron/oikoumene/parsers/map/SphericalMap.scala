package com.lomicron.oikoumene.parsers.map

/**
  * Raw geometric map without any metadata.
  */
case class SphericalMap(center: Point2D, polygons: Seq[SphericalPolygon] = Seq.empty) {

  def rotate(c: SphericalCoord): SphericalMap =
    rotate(c.polar, c.azimuth)

  def rotate(polarRot: Double, azimuthRot: Double): SphericalMap = {
    val rotated = polygons.map(_.rotate(polarRot, azimuthRot))
    SphericalMap(center, rotated)
  }

  def project: Seq[Polygon] = Geometry.project(polygons, center)

}
