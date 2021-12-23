package com.lomicron.eu4.model.map.spherical

import com.lomicron.eu4.model.map.River
import com.lomicron.utils.collection.Emptiable
import com.lomicron.utils.geometry.Point2D

case class SphericalRiver(path: Seq[SphericalRiverSegment] = Seq.empty)
extends Emptiable
{

  override def isEmpty: Boolean = path.isEmpty

  def project(center: Point2D): River =
    River(path.map(_.project(center)).filter(_.nonEmpty))

  def rotate(polarRot: Double, azimuthRot: Double): SphericalRiver =
    copy(path = path.map(_.rotate(polarRot, azimuthRot)))

}

object SphericalRiver {
  def apply(river: River, center: Point2D, radius: Double): SphericalRiver =
    SphericalRiver(river.path.map(SphericalRiverSegment(_, center, radius)))
}
