package com.lomicron.eu4.parsers.map

import com.lomicron.eu4.parsers.map.Tracer.MAX_MAP_BORDER_LINE_LENGTH
import com.lomicron.utils.collection.CollectionUtils.{OptionEx, toOption}
import com.lomicron.utils.geometry._
import com.typesafe.scalalogging.LazyLogging

import java.awt.Point
import java.awt.image.BufferedImage
import scala.collection.parallel.CollectionConverters._

object Tracer extends LazyLogging {
  type Labels = Array[Array[Int]]

  val MAX_MAP_BORDER_LINE_LENGTH = 10

  def trace(img: BufferedImage): Seq[Shape] = {
    logger.info("Labeling map components...")
    val labeledImg = BitmapRegionScanner.labelRegions(img)
    logger.info(s"Identified ${labeledImg.size} map components")
    val shapes = traceShapes(labeledImg)
    logger.info(s"Traced ${shapes.size} shapes")

    clipShapes(shapes)
  }

  def traceShapes(labeledImage: LabeledImage): Seq[Shape] =
    labeledImage
      .regions
      .par
      .map(group => {
        val startingPoint = new Point(group.x, group.y)
        val tracer = Tracer(labeledImage.img, startingPoint, labeledImage.labels)
        tracer.trace().copy(groupId = group.id)
      })
      .seq

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
        val clipShapes = singleBorderShapes.map(b => Shape(Seq(b))) ++ Shape.groupBordersIntoShapes(multiBorders)
        val clips = singleBorderShapes.flatMap(b => Shape(Seq(b)).withPolygon.polygon) ++ clipShapes.flatMap(_.withPolygon.polygon)
        s.copy(clip = clips, clipShapes = clipShapes)
      } else s
    })
  }

}

case class Tracer(img: BufferedImage, p: Point, labels: Array[Array[Int]], d: Direction = Right) extends BitmapWalker {

  val maxHeight: Int = img.getHeight - 1
  val maxWidth: Int = img.getWidth - 1
  private var currentPoint: Point = p
  private var currentDirection: Direction = d
  private var currentNeighbor: Option[Int] = neighborRegion(p, d.rBackward)
  private val group: Int = regionOf(p)

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
    val outlinePs = outline.map(_.p)
    val cleanedOutlinePs = Geometry.cleanSameLinePoints(outlinePs)
    val poly = Polygon(cleanedOutlinePs, color)
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

    def rotationNeighbor: Option[Int] = if (nextD == currentDirection.rBackward) currentNeighbor else neighborRegion(cp, nextD.rBackward)

    while (sameDirection && sameNeighbor(cg) && !isLongBorderLine) {
      cp = neighbor(cp, nextD).get
      cg = neighborRegion(cp, nextD.rBackward)
      nextD = nextDirection(cp, cd, group)
      pixelLine += 1
    }

    if (nextD == cd.rBackward)
      if (neighborRegion(cp, nextD).contains(group))
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
      val tGroup = neighborRegion(cp, currentDirection.rBackward)
      BorderPoint(ps.head, currentColor, currentNeighbor) +: ps.drop(1).map(BorderPoint(_, tColor, tGroup))
    } else ps.map(BorderPoint(_, currentColor, currentNeighbor))

    currentDirection = nextD
    currentPoint = cp
    currentNeighbor = cg

    bps
  }

}
