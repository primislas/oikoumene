package com.lomicron.utils.geometry

import com.lomicron.utils.collection.Emptiable

trait TSphericalShape[S <: TSphericalShape[S]] extends Emptiable {
  val points: Seq[SphericalCoord]
  def setPoints(ps: Seq[SphericalCoord]): S

  def isEmpty: Boolean = points.size < 2

  def rotate(rotation: SphericalCoord): S =
    rotate(rotation.polar, rotation.azimuth)
  def rotate(polarRot: Double, azimuthRot: Double): S = {
    val rps = points.map(_.rotate(polarRot, azimuthRot))
    setPoints(rps)
  }

  def projectPoints(center: Point2D): Seq[Point2D] =
    points.filterNot(_.isInvisible).map(_.project(center))

}
