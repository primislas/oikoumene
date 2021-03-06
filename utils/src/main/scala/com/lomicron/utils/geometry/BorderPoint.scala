package com.lomicron.utils.geometry

case class BorderPoint
(
  p: Point2D,
  l: Option[Int] = None,
  lg: Option[Int] = None,
  r: Option[Int] = None,
)
{

  def withRight(c: Int): BorderPoint = copy(r = Option(c))

}
