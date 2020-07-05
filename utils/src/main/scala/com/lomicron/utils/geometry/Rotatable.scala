package com.lomicron.utils.geometry

trait Rotatable[T <: Rotatable[T]] {
  def rotate(center: Point2D, angle: Double): T
  def flipXY: T
}
