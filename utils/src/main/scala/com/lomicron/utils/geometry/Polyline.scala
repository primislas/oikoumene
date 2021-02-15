package com.lomicron.utils.geometry

case class Polyline(points: Seq[Point2D] = Seq.empty) extends TPath {
  override def reverse: Polyline = copy(points.reverse)
}
