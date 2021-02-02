package com.lomicron.utils.geometry

import com.lomicron.utils.geometry.Geometry.{halfPI, threeHalfPI, twoPI}
import com.lomicron.utils.geometry.Point2D.ZERO
import org.apache.commons.math3.fitting.WeightedObservedPoint

import java.awt.Point
import java.lang.Math.{PI, abs, atan}

case class Point2D(x: Double = 0, y: Double = 0) extends Rotatable[Point2D] { self =>

  def offset(p: Point2D): Point2D = this + p

  def -(p: Point2D): Point2D = Point2D(x - p.x, y - p.y)

  def +(p: Point2D): Point2D = Point2D(x + p.x, y + p.y)

  def *(m: Double): Point2D = Point2D(x * m, y * m)

  def *(p: Point2D): Double = x * p.x + y * p.y

  def toInt: Point = new Point(x.toInt, y.toInt)

  def dx(p: Point2D): Double = x - p.x
  def dy(p: Point2D): Double = y - p.y

  def reflectY(height: Double): Point2D = Point2D(x, height - y)
  def flipXY: Point2D = Point2D(y, x)

  def sqrDistance(p: Point2D = ZERO): Double = Math.pow(x - p.x, 2) + Math.pow(y - p.y, 2)
  def distance(p: Point2D): Double = Math.sqrt(sqrDistance(p))
  def angleTo(p: Point2D): Double = {
    if (x == p.x) { if (p.y >= y) halfPI else threeHalfPI }
    else {
      val pdx = p.dx(self)
      val pdy = p.dy(self)
      val a = abs(atan(pdy / pdx))
      if (pdy >= 0) { if (pdx >= 0) a else PI - a }
      else if (pdx < 0) PI + a else twoPI - a
    }
  }

  override def rotate(c: Point2D, a: Double): Point2D = {
    val originalAngle = c.angleTo(self)
    val dist = distance(c)
    val nextAngle = originalAngle + a
    val rx = dist * Math.cos(nextAngle) + c.x
    val ry = dist * Math.sin(nextAngle) + c.y
    Point2D(rx, ry)
  }

  def normalize: Point2D = {
    val length = distance(ZERO)
    Point2D(x / length, y / length)
  }

}

object Point2D {

  val ZERO: Point2D = Point2D()

  def apply(x: Int, y: Int): Point2D = Point2D(x.toDouble, y.toDouble)

  def apply(p: Point): Point2D = apply(p.x, p.y)

  def apply(wp: WeightedObservedPoint): Point2D = new Point2D(wp.getX, wp.getY)

  implicit def toPoint3D(p: Point): Point2D = apply(p)

}
