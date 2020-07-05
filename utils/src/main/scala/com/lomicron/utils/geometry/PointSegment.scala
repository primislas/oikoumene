package com.lomicron.utils.geometry

case class PointSegment(x: Double, min: Double, max: Double, avg: Double, ps: Seq[Point2D]) {

  def +(points: Seq[Point2D]): PointSegment = PointSegment(x, ps ++ points)

  def +(segment: PointSegment): PointSegment = PointSegment(x, ps ++ segment.ps)

  def minMaxPoints: Seq[Point2D] = Seq(Point2D(x, min), Point2D(x, max))

  def avgPoint: Point2D = Point2D(x, avg)

  def range: Double = max - min

}

object PointSegment {

  def apply(x: Double, ps: Seq[Point2D]): PointSegment = {
    var min = Double.MaxValue
    var max = 0.0
    var sum = 0.0
    ps.foreach(p => {
      if (p.y < min) min = p.y
      if (p.y > max) max = p.y
      sum = sum + p.y
    })
    val avg = if (ps.nonEmpty) sum / ps.length else sum

    PointSegment(x, min, max, avg, ps)
  }

}
