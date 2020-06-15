package com.lomicron.oikoumene.parsers.map

import java.util.Objects

case class Border
(
  points: Seq[Point2D] = Seq.empty,
  // left neighbor: color or province id
  left: Option[Int] = None,
  // right neighbor: color or province id
  right: Option[Int] = None,
) {

  def +(p: BorderPoint): Border = this.+(p.p)

  def +(p: Point2D): Border = copy(points = points :+ p)

  override def hashCode(): Int = {
    val leftIsSmaller =
      if (left.isEmpty) true
      else if (right.isEmpty) false
      else if (left.exists(l => right.exists(r => l < r))) true
      else false
    if (leftIsSmaller) Objects.hash(left, right)
    else Objects.hash(right, left)
  }

  override def equals(obj: Any): Boolean = left == right

}
