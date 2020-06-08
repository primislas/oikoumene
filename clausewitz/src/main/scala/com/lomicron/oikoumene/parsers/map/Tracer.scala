package com.lomicron.oikoumene.parsers.map

import java.awt.Point
import java.awt.image.BufferedImage

import com.lomicron.utils.collection.CollectionUtils.OptionEx

object Tracer {

  def trace(img: BufferedImage): Seq[Polygon] = {
    var polygons = Seq[Polygon]()
    val groups = BitmapGrouper.toGroups(img)
    var tracedGroups = Set[Int]()
    for (y <- 0 until img.getHeight; x <- 0 until img.getWidth) {
      val group = groups(x)(y)
      if (!tracedGroups.contains(group)) {
        val poly = Tracer(img, new Point(x, y)).trace()
        polygons = polygons :+ poly
        tracedGroups = tracedGroups + group
      }
    }

    polygons
  }

}

case class Tracer(img: BufferedImage, p: Point, d: Direction = Right) extends BitmapWalker {

  private var currentPoint: Point = p
  private var currentDirection: Direction = d
  private var currentNeighbor: Option[Int] = neighborColor(p, d.rBackward)
  private val color: Int = colorOf(p)

  def trace(): Polygon = {
    val startingPoint = p
    val startingDirection = d
    var outline = Seq.empty[Point2D]
    var lastP = Option.empty[Point2D]
    do {
      val nps = next()
      val appended = if (nps.headOption.contentsEqual(lastP)) nps.drop(1) else nps
      outline = outline ++ appended
      lastP = appended.lastOption
    } while (!(startingPoint == currentPoint && startingDirection == currentDirection))

    Polygon(outline, color)
  }

  def next(): Seq[Point2D] = {
    var cp = currentPoint
    val cd = currentDirection
    var cc = currentNeighbor

    val maxHeight: Int = img.getHeight - 1
    val maxWidth: Int = img.getWidth - 1
    var pixelLine = 0
    var nextD = nextDirection(cp, cd, color)

    def sameDirection: Boolean = nextD == cd
    def sameNeighbor(cn: Option[Int]): Boolean = sameExistingNeighbor(cn) || (cn.isEmpty && currentNeighbor.isEmpty)
    def sameExistingNeighbor(n: Option[Int], cn: Option[Int] = currentNeighbor): Boolean =
      cn.exists(n.contains(_))
    def isLongBorderLine: Boolean = pixelLine == 10 && (cp.y == 0 || cp.y == maxHeight || cp.x == 0|| cp.x == maxWidth)
    def rotationNeighbor: Option[Int] = if (nextD == currentDirection.rBackward) currentNeighbor else neighborColor(cp, nextD.rBackward)

    while (sameDirection && sameNeighbor(cc) && !isLongBorderLine) {
      cp = neighbor(cp, nextD).get
      cc = neighborColor(cp, nextD.rBackward)
      nextD = nextDirection(cp, cd, color)
      if (nextD == cd.rBackward)
        cp = neighbor(cp, nextD).get

      pixelLine += 1
    }

    val ps = if (isLongBorderLine) {
      cd.turnPoints(cp)
    } else if (!sameNeighbor(cc) && nextD != cd.rBackward) {
      val diffNeighbor = cd.turnPoints(cp)
      val rn = cc
      cc = rotationNeighbor
      val turnPoints = if (!sameDirection) {
        val turnSmoothing = if (sameExistingNeighbor(cc, rn)) 1 else 0
        nextD.turnPoints(cp, turnSmoothing, cd)
      } else Seq.empty
      diffNeighbor ++ turnPoints
    } else {
      // post-rotation neighbor color
      cc = rotationNeighbor
      val smoothing = if (nextD == cd.rForward) {
        val diagNeighbor = neighbor(cp, cd)
          .flatMap(neighbor(_, cd.rBackward))
          .map(colorOf)
        val isSameNeighbor = for {
          a <- currentNeighbor
          b <- diagNeighbor
          c <- cc
        } yield a == b && a == c
        isSameNeighbor.filter(identity).map(_ => 1).getOrElse(0)
      } else 1
      nextD.turnPoints(cp, smoothing, cd)
    }

    currentDirection = nextD
    currentPoint = cp
    currentNeighbor = cc

    ps
  }

}
