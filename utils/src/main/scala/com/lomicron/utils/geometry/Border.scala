package com.lomicron.utils.geometry

import com.lomicron.utils.collection.Emptiable

case class Border
(
  points: Seq[Point2D] = Seq.empty,
  // left neighbor: color or province id
  left: Option[Int] = None,
  // right neighbor: color or province id
  right: Option[Int] = None,
  leftGroup: Option[Int] = None,
  rightGroup: Option[Int] = None,
  path: Seq[TPath] = Seq.empty,
  `type`: Option[String] = None,
) extends Emptiable
{

  override def isEmpty: Boolean = points.size < 2

  def withPath(path: Seq[TPath]): Border = copy(path = path)

  def +(p: BorderPoint): Border = this.+(p.p)

  def +(p: Point2D): Border = copy(points = points :+ p)

  def +(ps: Seq[Point2D]): Border = copy(points = points ++ ps)

  def +(b: Border): Border = copy(points = points ++ b.points)

  def withType(t: String): Border = copy(`type` = Some(t))

  def identicalNeighbors(b: Border): Boolean =
    left == b.left && right == b.right

  def size: Int = points.size

  def isClosed: Boolean = points.headOption.exists(points.lastOption.contains)

  def reverse: Border = copy(points = points.reverse)

  def offset(diff: Point2D): Border = copy(points = points.map(_.offset(diff)))

  override def hashCode(): Int = {
    val leftIsSmaller =
      if (left.isEmpty) true
      else if (right.isEmpty) false
      else left.exists(l => right.exists(r => l < r))
    None.hashCode()
    if (leftIsSmaller) hashCodeOf(left, right, points.size)
    else hashCodeOf(right, left, points.size)
  }

  def hashCodeOf(a: Option[Int], b: Option[Int], size: Int): Int = {
    var res = 1
    val aInt = a.getOrElse(0)
    val bInt = b.getOrElse(0)
    res = 31 * res + aInt
    res = 31 * res + bInt
    res = 31 * res + size
    res
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

object Border {

  def ofBorderPoints(outline: Seq[BorderPoint], group: Int): Seq[Border] = {
    var bs = Seq.empty[Border]
    var p = outline.last
    val h = outline.head
    var neighbor = h.lg
    var currNeighbor = neighbor
    var leftPs = outline
    var currBorder = Border(Seq.empty, h.l, h.r, neighbor, Some(group))

    while (leftPs.nonEmpty) {

      while (currNeighbor == neighbor && leftPs.nonEmpty) {
        currBorder = currBorder + p
        p = leftPs.head
        currNeighbor = p.lg
        leftPs = leftPs.drop(1)
      }

      val lastP = currBorder.points.lastOption.map(BorderPoint(_)).get
      currNeighbor = p.lg
      if (leftPs.nonEmpty) {
        bs = bs :+ currBorder
        currBorder = Border(Seq.empty, p.l, p.r, p.lg, Some(group)) + lastP

        neighbor = p.lg
        leftPs = leftPs
      } else {
        if (neighbor != currNeighbor) {
          bs = bs :+ currBorder
          currBorder = Border(Seq.empty, p.l, p.r, p.lg, Some(group)) + lastP + p
        } else
          currBorder = currBorder + p
        bs = bs :+ currBorder
      }

    }

    if (bs.length > 1 && bs.head.identicalNeighbors(bs.last)
      && bs.head.points.headOption.exists(bs.last.points.lastOption.contains(_))) {

      val union = bs.last + bs.head.points.drop(1)
      bs = union +: bs.drop(1).dropRight(1)
    }

    val isClosedPolyline = false
    bs.map(b => b.copy(points = Geometry.cleanSameLinePoints(b.points, isClosedPolyline)))
  }

}
