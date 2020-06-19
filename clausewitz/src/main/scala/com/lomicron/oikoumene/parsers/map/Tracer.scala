package com.lomicron.oikoumene.parsers.map

import java.awt.Point
import java.awt.image.BufferedImage

import com.lomicron.utils.collection.CollectionUtils.{OptionEx, toOption}

object Tracer {

  def trace(img: BufferedImage): Seq[Shape] = {
    var shapes = Seq.empty[Shape]
    val groups = BitmapGrouper.toGroups(img)
    var tracedGroups = Set.empty[Int]

    for (y <- 0 until img.getHeight; x <- 0 until img.getWidth) {
      val group = groups(x)(y)
      if (!tracedGroups.contains(group)) {
        val startingPoint = new Point(x, y)
        val tracer = Tracer(img, startingPoint, groups)
        val shape = tracer.trace().copy(groupId = group)

        shapes = shapes :+ shape
        tracedGroups = tracedGroups + group
      }
    }

    clipShapes(shapes)
  }

  def clipShapes(ss: Seq[Shape]): Seq[Shape] = {
    val bs = ss.flatMap(_.borders)
    val bsByStartPoint = bs.groupBy(_.points.head)

    def hasNoAntiBorder(b: Border): Boolean =
      !bsByStartPoint
        .getOrElse(b.points.last, Seq.empty)
        .exists(_.leftGroup == b.rightGroup)

    val enclosedByOuterGroup = bs
      .filter(_.leftGroup.isDefined)
      .filter(hasNoAntiBorder)
      .groupBy(_.leftGroup.get)

    ss.map(s => {
      if (enclosedByOuterGroup.keySet.contains(s.groupId.get)) {
        val innerBs = enclosedByOuterGroup.getOrElse(s.groupId.get, Seq.empty)
        val (singleBorderShapes, multiBorders) = innerBs.partition(_.isClosed)
        val clips = singleBorderShapes.flatMap(b => Shape(Seq(b)).withPolygon.polygon) ++ groupBordersIntoShapes(multiBorders)
        s.copy(clip = clips)
      } else s
    })
  }

  @scala.annotation.tailrec
  def groupBordersIntoShapes(bs: Seq[Border], ps: Seq[Polygon] = Seq.empty): Seq[Polygon] = {
    if (bs.isEmpty) ps
    else bs match {
      case h :: t =>
        val startPoint = h.points.head
        var currentPoint = h.points.last
        var remainingBorders: Seq[Border] = t
        var currentGroup = Seq(h)
        while (currentPoint != startPoint && remainingBorders.nonEmpty && currentGroup.nonEmpty) {
          val next = remainingBorders.find(_.points.head == currentPoint)
          if (next.isDefined) {
            val b = next.get
            remainingBorders = remainingBorders.filterNot(_.points.head == currentPoint)
            currentGroup = currentGroup :+ b
            currentPoint = b.points.last
          } else
            currentGroup = Seq.empty
        }

        val parsedPolygon = if (currentPoint == startPoint)
          Shape(currentGroup).withPolygon.polygon.toSeq
        else Seq.empty

        groupBordersIntoShapes(remainingBorders, ps ++ parsedPolygon)

      case _ :: Nil => Seq.empty
    }

  }

}

case class Tracer(img: BufferedImage, p: Point, groups: Array[Array[Int]], d: Direction = Right) extends BitmapWalker {

  val maxHeight: Int = img.getHeight - 1
  val maxWidth: Int = img.getWidth - 1
  private var currentPoint: Point = p
  private var currentDirection: Direction = d
  private var currentNeighbor: Option[Int] = neighborGroup(p, d.rBackward)
  private val group: Int = groupOf(p)

  def trace(): Shape = {
    val startingPoint = p
    val startingDirection = d
    var outline = Seq.empty[BorderPoint]
    var lastP = Option.empty[BorderPoint]
    do {
      val nps = next()
      val appended = if (nps.headOption.contentsEqual(lastP)) nps.drop(1) else nps
      outline = outline ++ appended
      lastP = appended.lastOption
    } while (!(startingPoint == currentPoint && startingDirection == currentDirection))

    val color = colorOf(p)
    val shifted = outline.last +: outline.dropRight(1)
    val cleaned = Geometry.clean(shifted)
    val bps = cleaned.map(_.withRight(color))
    val bs = toBorders(bps)
    val poly = Polygon(outline.map(_.p), color)
    Shape(borders = bs, provColor = color, polygon = poly)
  }

  def next(): Seq[BorderPoint] = {
    var cp = currentPoint
    val cd = currentDirection
    var cg = currentNeighbor
    val currentColor = neighborColor(currentPoint, currentDirection.rBackward)

    var pixelLine = 0
    var nextD = nextDirection(cp, cd, group)

    def sameDirection: Boolean = nextD == cd

    def sameNeighbor(cn: Option[Int]): Boolean = sameExistingNeighbor(cn) || (cn.isEmpty && currentNeighbor.isEmpty)

    def sameExistingNeighbor(n: Option[Int], cn: Option[Int] = currentNeighbor): Boolean =
      cn.exists(n.contains(_))

    def isLongBorderLine: Boolean = pixelLine == 10 && (cp.y == 0 || cp.y == maxHeight || cp.x == 0 || cp.x == maxWidth)

    def rotationNeighbor: Option[Int] = if (nextD == currentDirection.rBackward) currentNeighbor else neighborGroup(cp, nextD.rBackward)

    while (sameDirection && sameNeighbor(cg) && !isLongBorderLine) {
      cp = neighbor(cp, nextD).get
      cg = neighborGroup(cp, nextD.rBackward)
      nextD = nextDirection(cp, cd, group)
      pixelLine += 1
    }

    if (nextD == cd.rBackward)
      if (neighborGroup(cp, nextD).contains(group))
        cp = neighbor(cp, nextD).get
      else cp = diagNeighbor(cp, nextD.rForward).get

    var diffNeighbor = Seq.empty[Point2D]
    val ps = if (isLongBorderLine && sameDirection) {
      cd.turnIntPixelPoints(cp)
    } else if (!sameNeighbor(cg) && nextD != cd.rBackward) {
      diffNeighbor = cd.turnIntPixelPoints(cp)
      val rn = cg
      cg = rotationNeighbor
      val turnPoints = if (!sameDirection) {
        val turnSmoothing = if (sameExistingNeighbor(cg, rn)) 1 else 0
        nextD.turnIntPixelPoints(cp, turnSmoothing, cd)
      } else Seq.empty
      diffNeighbor ++ turnPoints
    } else {
      cg = rotationNeighbor
      val smoothing = if (nextD == cd.rForward) {
        val smoothing = for {
          curr <- currentNeighbor
          turn <- cg
        } yield if (curr == turn) 1 else 0
        smoothing.getOrElse(0)
      } else 1

      nextD.turnIntPixelPoints(cp, smoothing, cd)
    }

    val bps = if (diffNeighbor.nonEmpty) {
      val tColor = neighborColor(cp, currentDirection.rBackward)
      val tGroup = neighborGroup(cp, currentDirection.rBackward)
      BorderPoint(ps.head, currentColor, currentNeighbor) +: ps.drop(1).map(BorderPoint(_, tColor, tGroup))
    } else ps.map(BorderPoint(_, currentColor, currentNeighbor))

    currentDirection = nextD
    currentPoint = cp
    currentNeighbor = cg

    bps
  }

  def toBorders(outline: Seq[BorderPoint]): Seq[Border] = {
    var bs = Seq.empty[Border]
    var p = outline.last
    val h = outline.head
    var neighbor = h.lg
    var currNeighbor = neighbor
    var leftPs = outline
    var currBorder = Border(Seq.empty, h.l, h.r, neighbor, group)

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
        currBorder = Border(Seq.empty, p.l, p.r, p.lg, group) + lastP

        neighbor = p.lg
        leftPs = leftPs
      } else {
        if (neighbor != currNeighbor) {
          bs = bs :+ currBorder
          currBorder = Border(Seq.empty, p.l, p.r, p.lg, group) + lastP + p
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

    bs
  }

}
