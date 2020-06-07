package com.lomicron.oikoumene.parsers.map

import java.awt.Point
import java.awt.image.BufferedImage

import com.lomicron.oikoumene.parsers.map.MapParser.getRGB
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

case class Tracer(i: BufferedImage, p: Point, d: Direction = Right) {

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

    val maxHeight: Int = i.getHeight - 1
    val maxWidth: Int = i.getWidth - 1
    var pixelLine = 0
    var nextD = nextDirection(cp, cd)

    def sameDirection: Boolean = nextD == cd
    def sameNeighbor(cn: Option[Int]): Boolean = sameExistingNeighbor(cn) || (cn.isEmpty && currentNeighbor.isEmpty)
    def sameExistingNeighbor(n: Option[Int], cn: Option[Int] = currentNeighbor): Boolean =
      cn.exists(n.contains(_))
    def isLongBorderLine: Boolean = pixelLine == 10 && (cp.y == 0 || cp.y == maxHeight || cp.x == 0|| cp.x == maxWidth)
    def rotationNeighbor: Option[Int] = if (nextD == currentDirection.rBackward) currentNeighbor else neighborColor(cp, nextD.rBackward)

    while (sameDirection && sameNeighbor(cc) && !isLongBorderLine) {
      cp = neighbor(cp, nextD).get
      cc = neighborColor(cp, nextD.rBackward)
      nextD = nextDirection(cp, cd)
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

  def nextDirection(p: Point, d: Direction): Direction =
    if (neighborColorMatches(p, d.rBackward)) d.rBackward
    else if (neighborColorMatches(p, d)) d
    else d.rForward

  def neighborColorMatches(p: Point, d: Direction): Boolean =
    neighbor(p, d).map(colorOf).contains(color)

  def neighborColor(p: Point, d: Direction): Option[Int] =
    neighbor(p, d).map(colorOf)

  def neighbor(p: Point, d: Direction): Option[Point] = d match {
    case Up => if (p.y == 0) None else Some(new Point(p.x, p.y - 1))
    case Right => if (p.x == i.getWidth - 1) None else Some(new Point(p.x + 1, p.y))
    case Down => if (p.y == i.getHeight - 1) None else Some(new Point(p.x, p.y + 1))
    case Left => if (p.x == 0) None else Some(new Point(p.x - 1, p.y))
    case _ => None
  }

  def colorOf(p: Point): Int = getRGB(i, p.x, p.y).get

}

sealed trait Direction {
  self =>

  def isReverse(d: Direction): Boolean = self match {
    case Right => d == Left
    case Left => d == Right
    case Up => d == Down
    case Down => d == Up
  }

  def svgPoint(p: Point): Point = self match {
    case Right => p
    case Down => new Point(p.x + 1, p.y)
    case Left => new Point(p.x + 1, p.y + 1)
    case Up => new Point(p.x, p.y + 1)
  }

  def turnPoints(p: Point, smoothing: Int = 0, from: Direction = rBackward): Seq[Point2D] = {
    val startPoint =
      if (smoothing == 0) Point2D(svgPoint(p))
      else {
        val offsetStart = smoothing - 0.5
        val offsetDir = if (from == rBackward) 1 else if (from == rForward) -1 else 0
        val offset = offsetDir * offsetStart
        self match {
          case Right => Point2D(p.x, p.y + offset)
          case Down => Point2D(p.x + 1 - offset, p.y)
          case Left => Point2D(p.x + 1, p.y + 1 - offset)
          case Up => Point2D(p.x + offset, p.y + 1)
        }
      }

    val endPoint = Option(smoothing)
      .filter(_ > 0)
      .map(_ => self match {
        case Right => Point2D(p.x + 0.5, p.y)
        case Down => Point2D(p.x + 1, p.y + 0.5)
        case Left => Point2D(p.x + 0.5, p.y + 1)
        case Up => Point2D(p.x, p.y + 0.5)
      })

    Seq(startPoint) ++ endPoint.toSeq
  }

  def rForward: Direction = self match {
    case Right => Down
    case Down => Left
    case Left => Up
    case Up => Right
  }

  def rBackward: Direction = self match {
    case Right => Up
    case Up => Left
    case Left => Down
    case Down => Right
  }

}

object Right extends Direction

object Down extends Direction

object Left extends Direction

object Up extends Direction
