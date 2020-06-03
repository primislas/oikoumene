package com.lomicron.oikoumene.parsers.map

import java.awt.Point

case class Point2D(x: Double = 0, y: Double = 0) {

  def offset(p: Point2D): Point2D = Point2D(x + p.x, y + p.y)

}

object Point2D {

  def apply(x: Int, y: Int): Point2D = Point2D(x.toDouble, y.toDouble)

  def apply(p: Point): Point2D = apply(p.x, p.y)

  implicit def toPoint3D(p: Point): Point2D = apply(p)

}
