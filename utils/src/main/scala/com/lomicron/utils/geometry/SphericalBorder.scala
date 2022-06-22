package com.lomicron.utils.geometry

case class SphericalBorder
(
  points: Seq[SphericalCoord] = Seq.empty,
  // left neighbor: color or province id
  left: Option[Int] = None,
  // right neighbor: color or province id
  right: Option[Int] = None,
  leftGroup: Option[Int] = None,
  rightGroup: Option[Int] = None,
)
extends TSphericalShape[SphericalBorder]
{

  override def setPoints(ps: Seq[SphericalCoord]): SphericalBorder =
    copy(points = ps)

}
