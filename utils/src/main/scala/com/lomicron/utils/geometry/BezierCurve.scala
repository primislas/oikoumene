package com.lomicron.utils.geometry

case class BezierCurve
(
  p1: Point2D = Point2D.ZERO,
  cp1: Point2D = Point2D.ZERO,
  cp2: Point2D = Point2D.ZERO,
  p2: Point2D = Point2D.ZERO,
) extends TPath {
  override def points: Seq[Point2D] = Seq(p1, cp1, cp2, p2)
  override def reverse: BezierCurve = copy(p2, cp2, cp1, p1)
  def toArray: Array[Point2D] = points.toArray
}

object BezierCurve {

  def apply(array: Array[Point2D]): BezierCurve = {
    val p1 = array.headOption.getOrElse(Point2D.ZERO)
    val cp1 = array.lift(1).getOrElse(Point2D.ZERO)
    val cp2 = array.lift(2).getOrElse(Point2D.ZERO)
    val p2 = array.lastOption.getOrElse(Point2D.ZERO)
    BezierCurve(p1, cp1, cp2, p2)
  }

}
