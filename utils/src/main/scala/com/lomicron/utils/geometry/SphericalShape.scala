package com.lomicron.utils.geometry

case class SphericalShape
(
  borders: Seq[SphericalBorder] = Seq.empty,
  provColor: Option[Int] = None,
  provId: Option[Int] = None,
  groupId: Option[Int] = None,
  polygon: Option[SphericalPolygon] = None,
  clip: Seq[SphericalPolygon] = Seq.empty,
  clipShapes: Seq[SphericalShape] = Seq.empty,
) {

  def rotate(polarRot: Double, azimuthRot: Double): SphericalShape =
    copy(polygon = polygon.map(_.rotate(polarRot, azimuthRot)))

}
