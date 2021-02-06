package com.lomicron.utils.geometry

trait TPath {
  def points: Seq[Point2D]
  def reverse: TPath
}

object TPath {
  type Polypath = Seq[TPath]
}
