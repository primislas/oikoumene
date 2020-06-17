package com.lomicron.oikoumene.parsers.map

trait TPoint {
  val x: Double
  val y: Double
  def asPoint: Point2D
}
