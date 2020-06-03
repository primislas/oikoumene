package com.lomicron.oikoumene.parsers.map

import java.awt.Point
import java.awt.image.BufferedImage
import com.lomicron.utils.collection.CollectionUtils.OptionEx

object Tracer {

  def trace(img: BufferedImage): Seq[Polygon] = {
    var traced = Set[Point]()
    var polygons = Seq[Polygon]()
    var skipColor = Option(0)
    for (y <- 0 until img.getHeight; x <- 0 until img.getWidth) {
      val p = new Point(x, y)
      val color = MapParser.getRGB(img, p.x, p.y)
      if (!skipColor.contentsEqual(color)) {
        if (!traced.contains(p)) {
          val poly = Tracer(img, p).trace()
          traced = traced ++ poly.borderPoints
          polygons = polygons :+ poly
        }
        skipColor = color
      }
    }

    polygons
  }

}

case class Tracer(i: BufferedImage, p: Point, d: Direction = Right) {

  private var currentPoint: Point = p
  private var currentDirection: Direction = d
  private val color: Int = colorOf(p)

  def trace(): Polygon = {
    val startingPoint = d.svgPoint(p)
    var outline = Seq(startingPoint)
    var borderPoints = Set(startingPoint)
    var (nextPoint, tracedBorder) = next()

    while (startingPoint != nextPoint) {
      outline = outline :+ nextPoint
      borderPoints = borderPoints ++ tracedBorder
      val pb = next()
      // TODO check length and add extra points to split long lines if necessary
      nextPoint = pb._1
      tracedBorder = pb._2
    }

    val outline2D = outline.map(Point2D(_))
    Polygon(outline2D, color, borderPoints = borderPoints)
  }

  def next(): (Point, Seq[Point]) = {
    var cp = currentPoint
    val cd = currentDirection
    var tracedBorder = Seq(cp)

    var nextD = nextDirection(cp, cd)
    // TODO also check for left neighbor color changes and mark those
    while (nextD == cd) {
      cp = neighbor(cp, nextD).get
      nextD = nextDirection(cp, cd)
      tracedBorder = tracedBorder :+ cp
    }

    currentDirection = nextD
    currentPoint = if (nextD == cd.rBackward) neighbor(cp, nextD).get else cp
    (nextD.svgPoint(currentPoint), tracedBorder)
  }

  def nextDirection(p: Point, d: Direction): Direction =
    if (neighborColorMatches(p, d.rBackward)) d.rBackward
    else if (neighborColorMatches(p, d)) d
    else d.rForward

  def neighborColorMatches(p: Point, d: Direction): Boolean =
    neighbor(p, d).map(colorOf).contains(color)

  def neighbor(p: Point, d: Direction): Option[Point] = d match {
    case Up => if (p.y == 0) None else Some(new Point(p.x, p.y - 1))
    case Right => if (p.x == i.getWidth - 1) None else Some(new Point(p.x + 1, p.y))
    case Down => if (p.y == i.getHeight - 1) None else Some(new Point(p.x, p.y + 1))
    case Left => if (p.x == 0) None else Some(new Point(p.x - 1, p.y))
    case _ => None
  }

  def colorOf(p: Point): Int = MapParser.getRGB(i, p.x, p.y).get

}

sealed trait Direction { self =>

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
