package com.lomicron.oikoumene.model.map

import java.awt.Point

import com.lomicron.oikoumene.parsers.map.{Direction, Down, Left, Right, Up}
import com.lomicron.utils.geometry.Point2D

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

  def smooth: RiverSegment = {
    if (points.size < 3) this
    else {
      val head = points.head
      val next = points.drop(1).head
      val d = direction(head, next)
      val smoothedPs = recSmooth(next, d, points.drop(2), Seq(head))
      copy(points = smoothedPs)
    }
  }

  @scala.annotation.tailrec
  private def recSmooth(p: Point2D, d: Direction, ps: Seq[Point2D], smoothed: Seq[Point2D]): Seq[Point2D] = {
    if (ps.isEmpty) smoothed
    else {
      var sameDirectionPs = Seq(p)

      var leftPs = ps
      var prevP = p
      var nextP = leftPs.head
      leftPs = leftPs.drop(1)
      var nextD = direction(prevP, nextP)
      while (d == nextD && leftPs.size > 1) {
        sameDirectionPs = sameDirectionPs :+ nextP
        prevP = nextP
        nextP = leftPs.head
        leftPs = leftPs.drop(1)
        nextD = direction(prevP, nextP)
      }

      if (nextD == d) smoothed ++ sameDirectionPs :+ nextP
      else {
        // dropping the point we're about to smooth
        val sameDir = sameDirectionPs.dropRight(1)
        val smPs = smoothed ++ sameDir ++ nextD.turnPoints(prevP, 1, d)
        recSmooth(nextP, nextD, leftPs, smPs)
      }
    }


  }

  private def direction(a: Point2D, b: Point2D): Direction = {
    if (b.x > a.x) Right
    else if (b.x < a.x) Left
    else if (b.y > a.y) Down
    else Up
  }

}

object RiverSegment {

  def ofIntPoints(source: Int, width: Int, points: Seq[Point] = Seq.empty): RiverSegment =
    new RiverSegment(source, width, points.map(Point2D(_)))

}
