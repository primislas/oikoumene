package com.lomicron.oikoumene.parsers.map

import java.awt.Point
import java.awt.image.BufferedImage

import com.lomicron.utils.collection.CollectionUtils.{OptionEx, toOption}

object Tracer {

  def trace(img: BufferedImage): Seq[Shape] = {
    var shapes = Seq.empty[Shape]
    val groups = BitmapGrouper.toGroups(img)
    var tracedGroups = Set.empty[Int]
    var nestedGroups = Seq.empty[(Int, Int)]

    for (y <- 0 until img.getHeight; x <- 0 until img.getWidth) {
      val group = groups(x)(y)
      if (!tracedGroups.contains(group)) {
        val startingPoint = new Point(x, y)
        val tracer = Tracer(img, startingPoint)
        val shape = tracer.trace().copy(groupId = group)

        // single border means nested shape,
        // we want to clip inner shapes through outer ones
        if (shape.borders.length == 1)
          Up.directions
            .flatMap(tracer.neighbor(startingPoint, _))
            .map(p => groups(p.x)(p.y))
            .find(_ != group)
            .foreach(enclosingGroup => nestedGroups = nestedGroups :+ (enclosingGroup, group))

        shapes = shapes :+ shape
        tracedGroups = tracedGroups + group
      }
    }

    clipShapes(shapes, nestedGroups)
  }

  def clipShapes(shapes: Seq[Shape], clips: Seq[(Int, Int)]): Seq[Shape] = {
    val modifiedIds = clips.map(_._1).toSet
    var byGroup = shapes.groupBy(_.groupId.get).mapValues(_.head)
    clips.foreach {
      case (outer, inner) =>
        val os = byGroup.get(outer)
        val is = byGroup.get(inner)
        val clipped = for {
          outerShape <- os
          innerShape <- is
        } yield outerShape.copy(clip = outerShape.clip ++ innerShape.polygon.toSeq)
        clipped.foreach(s => byGroup = byGroup.updated(outer, s))
    }

    shapes.map(s => if (s.groupId.exists(modifiedIds.contains)) byGroup.getOrElse(s.groupId.get, s) else s)
  }

}

case class Tracer(img: BufferedImage, p: Point, d: Direction = Right) extends BitmapWalker {

  private var currentPoint: Point = p
  private var currentDirection: Direction = d
  private var currentNeighbor: Option[Int] = neighborColor(p, d.rBackward)
  private val color: Int = colorOf(p)

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
      cd.turnIntPixelPoints(cp)
    } else if (!sameNeighbor(cc) && nextD != cd.rBackward) {
      val diffNeighbor = cd.turnIntPixelPoints(cp)
      val rn = cc
      cc = rotationNeighbor
      val turnPoints = if (!sameDirection) {
        val turnSmoothing = if (sameExistingNeighbor(cc, rn)) 1 else 0
        nextD.turnIntPixelPoints(cp, turnSmoothing, cd)
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
      nextD.turnIntPixelPoints(cp, smoothing, cd)
    }

    val bps = ps.map(BorderPoint(_, currentNeighbor))

    currentDirection = nextD
    currentPoint = cp
    currentNeighbor = cc

    bps
  }

  def toBorders(outline: Seq[BorderPoint]): Seq[Border] = {
    var bs = Seq.empty[Border]
    var p = outline.last
    val h = outline.head
    var neighbor = h.l
    var currNeighbor = neighbor
    var leftPs = outline
    var currBorder = Border(Seq.empty, h.l, h.r)

    while (leftPs.nonEmpty) {

      while (currNeighbor == neighbor && leftPs.nonEmpty) {
        currBorder = currBorder + p
        p = leftPs.head
        currNeighbor = p.l
        leftPs = leftPs.drop(1)
      }

      val lastP = currBorder.points.lastOption.map(BorderPoint(_)).get
      currNeighbor = p.l
      if (leftPs.nonEmpty) {
        bs = bs :+ currBorder
        currBorder = Border(Seq.empty, p.l, p.r) + lastP

        neighbor = p.l
        leftPs = p +: leftPs
      } else {
        if (neighbor != currNeighbor) {
          bs = bs :+ currBorder
          currBorder = Border(Seq.empty, p.l, p.r) + lastP + p
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
