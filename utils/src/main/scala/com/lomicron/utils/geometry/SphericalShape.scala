package com.lomicron.utils.geometry

case class SphericalShape
(
  borders: Seq[SphericalBorder] = Seq.empty,
  provColor: Option[Int] = None,
  provId: Option[Int] = None,
  clipShapes: Seq[SphericalShape] = Seq.empty,
) {

  def rotate(polarRot: Double, azimuthRot: Double): SphericalShape =
    copy(
      borders = borders.map(_.rotate(polarRot, azimuthRot)),
      clipShapes = clipShapes.map(_.rotate(polarRot, azimuthRot))
    )

}
