package com.lomicron.oikoumene.parsers.map

import java.awt.Point

case class Point2D(x: Double = 0, y: Double = 0) {

  def offset(p: Point2D): Point2D = this + p

  def -(p: Point2D): Point2D = Point2D(x - p.x, y - p.y)

  def +(p: Point2D): Point2D = Point2D(x + p.x, y + p.y)

  def toInt: Point = new Point(x.toInt, y.toInt)

  def dx(p: Point2D): Double = x - p.x
  def dy(p: Point2D): Double = y - p.y
  def reflectY(height: Int): Point2D = Point2D(x, height - y)

  def distance(p: Point2D): Double = Math.sqrt(Math.pow(x - p.x, 2) + Math.pow(y - p.y, 2))

}

object Point2D {

  val ZERO: Point2D = Point2D()

  def apply(x: Int, y: Int): Point2D = Point2D(x.toDouble, y.toDouble)

  def apply(p: Point): Point2D = apply(p.x, p.y)

  implicit def toPoint3D(p: Point): Point2D = apply(p)

}
