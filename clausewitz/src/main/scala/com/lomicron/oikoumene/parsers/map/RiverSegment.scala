package com.lomicron.oikoumene.parsers.map

import java.awt.Point

case class RiverSegment
(
  source: Int,
  width: Int,
  points: Seq[Point2D] = Seq.empty
) {

  def isEmpty: Boolean = points.isEmpty
  def nonEmpty: Boolean = points.nonEmpty
  def reverse: RiverSegment = RiverSegment(width, source, points.reverse)
  def withStartingPoint(p: Point): RiverSegment = copy(points = Point2D(p) +: points)

}

object RiverSegment {

  def ofIntPoints(source: Int, width: Int, points: Seq[Point] = Seq.empty): RiverSegment =
    new RiverSegment(source, width, points.map(Point2D(_)))

}
