package com.lomicron.oikoumene.parsers.map

import java.awt.Point
import java.awt.image.BufferedImage

import com.lomicron.oikoumene.parsers.map.Tracer.MAX_MAP_BORDER_LINE_LENGTH
import com.lomicron.utils.collection.CollectionUtils.{OptionEx, toOption}
import com.lomicron.utils.geometry.{Border, BorderPoint, Geometry, Point2D, Polygon, Shape}

object Tracer {

  val MAX_MAP_BORDER_LINE_LENGTH = 10

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
        val clips = singleBorderShapes.flatMap(b => Shape(Seq(b)).withPolygon.polygon) ++ Polygon.groupBordersIntoShapes(multiBorders)
        s.copy(clip = clips)
      } else s
    })
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
    val bs = Border.ofBorderPoints(bps, group)
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

    def isLongBorderLine: Boolean = pixelLine == MAX_MAP_BORDER_LINE_LENGTH && (cp.y == 0 || cp.y == maxHeight || cp.x == 0 || cp.x == maxWidth)

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

}
