package com.lomicron.utils.geometry

import com.lomicron.utils.collection.Emptiable
import com.lomicron.utils.geometry
import com.lomicron.utils.geometry.TPath.Polypath

case class Polygon
(
  points: Seq[Point2D],
  color: Int,
  provinceId: Option[Int] = None,
  clip: Seq[Polygon] = Seq.empty,
  path: Polypath = Seq.empty,
) extends Rotatable[Polygon] with Emptiable
{

  def isEmpty: Boolean = points.isEmpty
  def withPath(path: Polypath): Polygon = copy(path = path)

  def rotate(center: Point2D, angle: Double): Polygon = {
    val rotated = points.map(_.rotate(center, angle))
    copy(points = rotated)
  }
  def reflectY(height: Double): Polygon = {
    val reflected = points.map(_.reflectY(height))
    copy(points = reflected)
  }
  def flipXY: Polygon = {
    val flipped = points.map(_.flipXY)
    copy(points = flipped)
  }
  def offset(offset: Point2D): Polygon = {
    val ops = points.map(_.offset(offset))
    copy(points = ops)
  }
  def filter(predicate: Point2D => Boolean): Polygon = {
    val filtered = points.filter(predicate)
    copy(points = filtered)
  }

  def scale(coef: Double): Polygon =
    copy(points = points.map(_ * coef))

  def segments(segmentSize: Int = 5): Seq[PointSegment] = {
    def idSegment(p: Point2D): Double = (p.x / segmentSize).floor

    var remainingPs = points
    var segments = Seq.empty[PointSegment]
    while (remainingPs.nonEmpty) {
      val segId = idSegment(remainingPs.head)
      val segPs = remainingPs.takeWhile(p => idSegment(p) == segId)
      val segX = segId * segmentSize + segmentSize.toDouble / 2
      segments = segments :+ PointSegment(segX, segPs)
      remainingPs = remainingPs.drop(segPs.size)
    }

    segments = segments match {
      case _ :: Nil => segments
      case h :: tail =>
        if (h.x == tail.last.x) (tail.last + h) +: tail.dropRight(1)
        else h :: tail
    }

    // repeating head to cover last -> head gaps
    segments = segments :+ segments.head

    // filling in segment gaps
    var gaps = Seq.empty[PointSegment]
    segments.sliding(2, 1)
      .foreach(ss => {
        val diff = ss.head.x - ss.last.x
        val absDiff = Math.abs(diff).toInt
        if (absDiff != segmentSize) {
          val missingSegments = absDiff / segmentSize
          val xDiff = ss.last.x - ss.head.x
          val minDiff = ss.last.min - ss.head.min
          val maxDiff = ss.last.max - ss.head.max
          val avgDiff = ss.last.avg - ss.head.avg
          (1 until missingSegments).foreach(i => {
            val k = i.toDouble / missingSegments
            val x = ss.head.x + xDiff * k
            val min = ss.head.min + minDiff * k
            val max = ss.head.max + maxDiff * k
            val avg = ss.head.avg + avgDiff * k
            gaps = gaps :+ geometry.PointSegment(x, min, max, avg, Seq.empty)
          })
        }
      })

    segments.dropRight(1) ++ gaps
  }

  def toSpherical(center: Point2D, radius: Double): SphericalPolygon = {
    val ps = points.map(Geometry.fromMercator(_, center, radius))
    SphericalPolygon(ps, color, provinceId)
  }

}

object Polygon {

  def ofBorders(bs: Seq[Border]): Seq[Polygon] = groupBordersIntoShapes(bs)

  @scala.annotation.tailrec
  def groupBordersIntoShapes(bs: Seq[Border], ps: Seq[Polygon] = Seq.empty): Seq[Polygon] = {
    if (bs.isEmpty) ps
    else bs match {
      case Seq(h) => ps ++ Shape(Seq(h)).withPolygon.polygon.toSeq
      case h +: t =>
        val startPoint = h.points.head
        var currentPoint = h.points.last
        var remainingBorders: Seq[Border] = t
        var currentGroup = Seq(h)
        while (currentPoint != startPoint && remainingBorders.nonEmpty && currentGroup.nonEmpty) {
          val next = remainingBorders
            .find(_.points.head == currentPoint)
            .orElse(remainingBorders.find(_.points.last == currentPoint).map(_.reverse))
          if (next.isDefined) {
            val b = next.get
            remainingBorders = remainingBorders
              .filterNot(rb => (rb.points.head == currentPoint || rb.points.last == currentPoint) && rb.leftGroup == b.leftGroup && rb.rightGroup == b.rightGroup)
            currentGroup = currentGroup :+ b
            currentPoint = b.points.last
          } else
            currentGroup = Seq.empty
        }

        val parsedPolygon = if (currentPoint == startPoint)
          Shape(currentGroup).withPolygon.polygon.toSeq
        else Seq.empty

        Polygon.groupBordersIntoShapes(remainingBorders, ps ++ parsedPolygon)
    }
  }

}
