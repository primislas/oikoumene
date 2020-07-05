package com.lomicron.utils.geometry

import java.lang.Math.{abs, sqrt, tan}

import com.lomicron.utils.geometry.Geometry.sqr

/**
  * ax + by + c = 0
  *
  * @param cx x coeff
  * @param cy y coeff
  * @param c free coeff
  */
case class Line(cx: Double, cy: Double, c: Double) {

  def at(x: Double): Double = - (cx * x + c) / cy

  def angle: Double = Math.atan(-cx / cy)

  def distance(p: Point2D): Double =
    abs(cx * p.x + cy * p.y + c) / sqrt(sqr(cx) + sqr(cy))

  def distance(ps: Seq[Point2D]): Double = {
    val denominator = sqrt(sqr(cx) + sqr(cy))
    ps.map(p => cx * p.x + cy * p.y + c)
      .map(abs)
      .map(_ / denominator)
      .sum
  }

}

object Line {

  /**
    * @param a angle
    * @param p a point belonging to the line
    * @return general line equation
    */
  def ofAngle(a: Double, p: Point2D): Line = {
    val cx = -tan(a)
    val c = p.y + cx * p.x
    Line(cx, 1.0, -c)
  }

  def ofTwoPoints(p1: Point2D, p2: Point2D): Line = {
    val dx = p2.dx(p1)
    val dy = p2.dy(p1)
    val angle =
      if (dx == 0) Geometry.halfPI
      else if (dy == 0) 0
      else Math.atan(dy / dx)
    ofAngle(angle, p1)
  }

}
