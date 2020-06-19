package com.lomicron.oikoumene.parsers.map

import java.util.Objects

case class Border
(
  points: Seq[Point2D] = Seq.empty,
  // left neighbor: color or province id
  left: Option[Int] = None,
  // right neighbor: color or province id
  right: Option[Int] = None,
  leftGroup: Option[Int] = None,
  rightGroup: Option[Int] = None,
) {

  def +(p: BorderPoint): Border = this.+(p.p)

  def +(p: Point2D): Border = copy(points = points :+ p)

  def +(ps: Seq[Point2D]): Border = copy(points = points ++ ps)

  def +(b: Border): Border = copy(points = points ++ b.points)

  def identicalNeighbors(b: Border): Boolean =
    left == b.left && right == b.right

  def size: Int = points.size

  def isClosed: Boolean = points.headOption.exists(points.lastOption.contains)

  override def hashCode(): Int = {
    val leftIsSmaller =
      if (left.isEmpty) true
      else if (right.isEmpty) false
      else if (left.exists(l => right.exists(r => l < r))) true
      else false
    if (leftIsSmaller) Objects.hash(left, right, points.size.underlying())
    else Objects.hash(right, left, points.size.underlying())
  }

  override def equals(obj: Any): Boolean = obj match {
    case b: Border => sameNeighbors(b) && samePoints(b)
    case _ => false
  }

  private def sameNeighbors(b: Border): Boolean =
    (left == b.left && right == b.right) || (left == b.right && right == b.left)

  private def samePoints(b: Border): Boolean =
    points.size == b.points.size &&
      ((points.nonEmpty && (points.head == b.points.head || points.head == b.points.last)) || points.isEmpty)

}
