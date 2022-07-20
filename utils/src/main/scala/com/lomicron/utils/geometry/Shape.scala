package com.lomicron.utils.geometry

import com.lomicron.utils.geometry.TPath.Polypath

case class Shape
(
  borders: Seq[Border] = Seq.empty,
  provColor: Option[Int] = None,
  provId: Option[Int] = None,
  path: Polypath = Seq.empty,
  clipShapes: Seq[Shape] = Seq.empty,
) {

  def clipPaths: Seq[Polypath] = clipShapes.map(_.path)

  def withProvinceId(id: Int): Shape =
    copy(provId = Some(id))

  def withPath(path: Seq[TPath]): Shape =
    copy(path = path)

  def offset(diff: Point2D): Shape = {
    val obs = borders.map(_.offset(diff))
    val ocss = clipShapes.map(_.offset(diff))
    copy(borders = obs, clipShapes = ocss)
  }

  def rotate(angle: Double, rotationCenter: Point2D = Point2D.ZERO): Shape = {
    val rbs = borders.map(_.rotate(rotationCenter, angle))
    val rcss = clipShapes.map(_.rotate(angle, rotationCenter))
    copy(borders = rbs, clipShapes = rcss)
  }

  def scale(coef: Double): Shape = {
    val sbs = borders.map(_.scale(coef))
    val scss = clipShapes.map(_.scale(coef))
    val ps = path.map(_.scale(coef))
    copy(borders = sbs, clipShapes = scss, path = ps)
  }

  def isClipped: Boolean = clipShapes.nonEmpty

  def toPolygon: Polygon = {
    val ps = borders
      .map(_.points)
      .map(_.drop(1))
      .reduce(_ ++ _)
    val outline = ps.last +: ps.take(ps.size - 1)
    val clip = clipShapes.map(_.toPolygon)
    Polygon(outline, provColor.getOrElse(-1), provId, clip)
  }

}

object Shape {

  @scala.annotation.tailrec
  def groupBordersIntoShapes(bs: Seq[Border], ss: Seq[Shape] = Seq.empty): Seq[Shape] = {
    if (bs.isEmpty) ss
    else bs match {
      case Seq(h) => ss :+ Shape(Seq(h))
      case h +: t =>
        val startPoint = h.points.head
        var currentPoint = h.points.last
        var remainingBorders: Seq[Border] = t
        var currentGroup = Seq(h)
        while (currentPoint != startPoint && remainingBorders.nonEmpty && currentGroup.nonEmpty) {
          val next = remainingBorders
            .find(_.points.head == currentPoint)
            .orElse(remainingBorders.find(_.points.last == currentPoint).map(_.reverse))
          if (next.isDefined) {
            val b = next.get
            remainingBorders = remainingBorders
              .filterNot(rb => (rb.points.head == currentPoint || rb.points.last == currentPoint) && rb.leftGroup == b.leftGroup && rb.rightGroup == b.rightGroup)
            currentGroup = currentGroup :+ b
            currentPoint = b.points.last
          } else
            currentGroup = Seq.empty
        }

        val parsedShape = if (currentPoint == startPoint)
          Seq(Shape(currentGroup))
        else Seq.empty

        Shape.groupBordersIntoShapes(remainingBorders, ss ++ parsedShape)
    }
  }

}
