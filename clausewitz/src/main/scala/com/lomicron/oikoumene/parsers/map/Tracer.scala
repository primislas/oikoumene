package com.lomicron.oikoumene.parsers.map

import java.awt.Point
import java.awt.image.BufferedImage

import com.lomicron.oikoumene.parsers.map.MapParser.getRGB
import com.lomicron.utils.collection.CollectionUtils.OptionEx


object Tracer {

  def trace(img: BufferedImage): Seq[Polygon] = {
    var polygons = Seq[Polygon]()
    val groups = toGroups(img)
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

  def toGroups(img: BufferedImage): Array[Array[Int]] = {
    val groups = Array.ofDim[Int](img.getWidth(), img.getHeight())
    def leftNeighborGroup(x: Int, y: Int): Option[Int] =
      if (x > 0 && y >= 0) Some(groups(x-1)(y))
      else None
    def topNeighborGroup(x: Int, y: Int) : Option[Int] =
      if (x >= 0 && y > 0) Some(groups(x)(y-1))
      else None
    def leftNeighborColorMatches(x: Int, y: Int, c: Int): Boolean =
      getRGB(img, x-1, y).contains(c)
    def topNeighborColorMatches(x: Int, y: Int, c: Int): Boolean =
      getRGB(img, x, y-1).contains(c)
    var groupIds = 0
    def nextId(): Int = {
      groupIds = groupIds + 1
      groupIds
    }

    for (y <- 0 until img.getHeight; x <- 0 until img.getWidth) {
      val color = getRGB(img, x, y).get

      val isLeftGroup = leftNeighborColorMatches(x, y, color)
      val isTopGroup = topNeighborColorMatches(x, y, color)
      def setGroup(id: Int): Unit =
        groups(x)(y) = id

      if (y == 36 && x == 26)
        println()

      if (isLeftGroup) {
        if (isTopGroup) {
          val leftGroup = leftNeighborGroup(x, y)
          val topGroup = topNeighborGroup(x, y)
          if (!leftGroup.contentsEqual(topGroup)) {
            // replace all with lowest
            val id = for {
              lg <- leftGroup
              tg <- topGroup
            } yield mergeGroups(groups, x, y, lg, tg)
            id.foreach(setGroup)
          } else
            leftNeighborGroup(x, y).foreach(setGroup)
        } else
          leftNeighborGroup(x, y).foreach(setGroup)
      } else if (isTopGroup)
        topNeighborGroup(x, y).foreach(setGroup)
      else
        groups(x)(y) = nextId()
    }

    groups
  }

  def mergeGroups(gs: Array[Array[Int]], x: Int, y: Int, lg: Int, tg: Int): Int = {
    if (lg < tg) replaceGroup(gs, x, y-1, tg, lg)
    else replaceGroup(gs, x-1, y, lg, tg)
  }

  def replaceGroup(gs: Array[Array[Int]], x: Int, y: Int, from: Int, to: Int): Int = {

    @scala.annotation.tailrec
    def go(start: Int, by: Int => Int): Int =
      if (start < 0) 0
      else if (start >= gs.length) gs.length - 1
      else if (gs(start)(y) == from) {
        gs(start)(y) = to
        val next = by(start)
        if (next < 0 || next >= gs.length) start
        else go(by(start), by)
      } else start

    val xFrom = go(x, _ - 1) + 1
    val xTo = go(x + 1, _ + 1) - 1

    if (y == 0) to
    else {
      for (i <- xFrom to xTo)
        // TODO tailrec?
        // Actually there doesn't appear to do a lot of redundancy -
        // after the first pass gs(i + 1) != from, so the only
        // downside of this approach is not having tailrec calls
        if (gs(i)(y - 1) == from) replaceGroup(gs, i, y - 1, from ,to)
      to
    }

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
    borderPoints = borderPoints ++ tracedBorder

    while (startingPoint != nextPoint) {
      outline = outline :+ nextPoint
      val pb = next()
      nextPoint = pb._1
      tracedBorder = pb._2
      borderPoints = borderPoints ++ tracedBorder
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
