package com.lomicron.eu4.model.map.spherical

import com.lomicron.utils.geometry.{Point2D, Shape, SphericalPolygon}

case class SphericalShape
(
  polygon: SphericalPolygon,
  shape: Shape,
) {

  def project(center: Point2D): Shape =
    shape.copy(polygon = Some(polygon.project(center)))

  def rotate(polarRot: Double, azimuthRot: Double): SphericalShape =
    copy(polygon = polygon.rotate(polarRot, azimuthRot))

}

object SphericalShape {
  def apply(shape: Shape, center: Point2D, radius: Double): SphericalShape = {
    val p = shape.polygon.map(SphericalPolygon(_, center, radius)).getOrElse(SphericalPolygon(Seq.empty, 0))
    SphericalShape(p, shape)
  }
}
