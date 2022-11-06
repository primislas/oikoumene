package com.lomicron.oikoumene.parsers.map

import java.awt.Point
import java.awt.image.BufferedImage
import com.lomicron.oikoumene.model.Color
import com.lomicron.oikoumene.model.map.{River, RiverSegment}
import com.lomicron.oikoumene.parsers.map.MapParser.getRGB
import com.lomicron.utils.geometry.Point2D
import com.typesafe.scalalogging.LazyLogging

import scala.collection.immutable.ListSet

object RiverParser {

  import RiverTypes._

  val riverColors = Set(SOURCE, FLOW_IN, FLOW_OUT) ++ RIVER
  val waterColors = RIVER
  val edgeColors = Set(SOURCE, FLOW_IN, FLOW_OUT)
  val nonRiverColors = Set(LAND, SEA)

  def trace(img: BufferedImage): Seq[River] = RiverParser(img).trace

}

case class RiverParser(img: BufferedImage) extends BitmapWalker with LazyLogging {

  import RiverParser._
  import RiverTypes._
  val labels: Array[Array[Int]] = Array.empty

  def trace: Seq[River] = {
    val riverSources = identifyRiverSources(img)
    val traced = Array.ofDim[Boolean](img.getWidth, img.getHeight)
    val rs = riverSources.sources.flatMap(traceRiver(_, traced))
    val fi = riverSources.flowIns.flatMap(traceFlowIn(_, traced))
    val fo = riverSources.flowOuts.flatMap(traceFlowOut(_, traced))
    rs ++ fi ++ fo
  }

  def traceRiver(p: Point, traced: Array[Array[Boolean]]): Option[River] =
    untracedDirection(p, traced)
      .map(traceDirection(p, _))
      .map(ps => {
        ps.foreach(p => traced(p.x)(p.y) = true)
        toRiver(ps)
      })

  def traceFlowIn(p: Point, traced: Array[Array[Boolean]]): Option[River] = {
    val startingPoint = tracedSiblingRiver(p, traced)
    traceRiver(p, traced)
      .flatMap(r => startingPoint.map(r.addStartingPoint))
      .map(_.reverse)
  }

  def traceFlowOut(p: Point, traced: Array[Array[Boolean]]): Seq[River] = {
    val startingPoint = tracedSiblingRiver(p, traced)
    Up.directions
      .filter(isUntracedDirection(p, _, traced))
      .map(traceDirection(p, _))
      .map(toRiver)
      .flatMap(r => startingPoint.map(r.addStartingPoint))
  }

  def untracedDirection(p: Point, traced: Array[Array[Boolean]]): Option[Direction] =
    Up.directions.find(isUntracedDirection(p, _, traced))

  def tracedSiblingRiver(p: Point, traced: Array[Array[Boolean]]): Option[Point] =
    Up.directions.flatMap(neighbor(p, _)).find(n => traced(n.x)(n.y))

  def isUntracedDirection(p: Point, d: Direction, traced: Array[Array[Boolean]]): Boolean =
    neighbor(p, d)
      .filter(isRiver)
      .exists(isUntracedPoint(_, traced))

  def isRiver(p: Point): Boolean = riverColors.contains(colorOf(p))

  def isUntracedPoint(p: Point, traced: Array[Array[Boolean]]): Boolean = !traced(p.x)(p.y)

  def identifyRiverSources(img: BufferedImage): RiverSources = {
    var sources: Seq[Point] = Seq.empty
    var flowIns: Seq[Point] = Seq.empty
    var flowOuts: Seq[Point] = Seq.empty

    for (y <- 0 until img.getHeight; x <- 0 until img.getWidth) {
      val color = getRGB(img, x, y).get
      if (color == SOURCE) sources = sources :+ p(x, y)
      else if (color == FLOW_IN) flowIns = flowIns :+ p(x, y)
      else if (color == FLOW_OUT) flowOuts = flowOuts :+ p(x, y)
    }

    RiverSources(sources, flowIns, flowOuts)
  }

  def traceDirection(p: Point, d: Direction): Seq[Point] = {
    var points = Seq(p)
    var currentPoint = p
    var nextD = Option(d)

    do {
      currentPoint = neighbor(currentPoint, nextD.get).get
      points = points :+ currentPoint
      nextD = nextD.flatMap(nextRiverPoint(currentPoint, _))
    } while (nextD.isDefined)

    points
  }

  def toRiver(ps: Seq[Point]): River = {
    if (ps.isEmpty) River()

    val source = ps.head
    val sourceType = colorOf(ps.head)

    val ss = parseRiverPoints(ps.drop(1), sourceType)
    val sourceSegment = ss.headOption
      .map(s => s.copy(points = Point2D(source) +: s.points))
    val segments = (sourceSegment.toSeq ++ ss.drop(1)).filter(_.nonEmpty)

    River(segments)
  }

  def parseRiverPoints
  (ps: Seq[Point],
   prevType: Int,
   segments: Seq[RiverSegment] = Seq.empty)
  : Seq[RiverSegment] = {

    if (ps.isEmpty) segments
    else {
      val segmentType = ps.headOption.map(colorOf).get
      var segmentPs = ps.takeWhile(colorOf(_) == segmentType)
      val untracedPs = ps.drop(segmentPs.size)
      untracedPs.headOption.foreach(p => segmentPs = segmentPs :+ p)
      val segment = RiverSegment.ofIntPoints(prevType, segmentType, segmentPs)
      parseRiverPoints(untracedPs, segmentType, segments :+ segment)
    }

  }

  def nextRiverPoint(p: Point, d: Direction): Option[Direction] =
    d.directionsForward() find (neighborColor(p, _).exists(waterColors.contains))

  def typeOf(p: Point, c: Int): Option[Int] = {
    if (edgeColors.contains(c)) c match {
      case FLOW_IN => Some(END)
      case FLOW_OUT => Some(SOURCE)
      case SOURCE => Some(SOURCE)
    }
    else if (isInSea(p)) Some(END)
    else if (isInLand(p)) Some(SOURCE)
    else None
  }

  def isInSea(p: Point): Boolean = isIn(p, SEA)

  def isInLand(p: Point): Boolean = isIn(p, LAND)

  def isIn(p: Point, color: Int): Boolean =
    Up.directions.flatMap(neighborColor(p, _)).count(_ == color) >= 3

  def p(x: Int, y: Int): Point = new Point(x, y)

}

object RiverTypes {
  val SOURCE: Int = Color(0, 255).toInt
  //noinspection RedundantDefaultArgument
  val FLOW_IN: Int = Color(255, 0, 0).toInt
  val FLOW_OUT: Int = Color(255, 252).toInt
  val NARROWEST: Int = Color(0, 225, 255).toInt

  val RIVER: ListSet[Int] = ListSet(
    Color(0, 250, 255).toInt,
    Color(0, 200, 255).toInt,
    Color(0, 100, 255).toInt,
    Color(0, 0, 255).toInt,
    Color(0, 0, 150).toInt,
    Color(0, 0, 100).toInt,
  )

  val SEA: Int = Color(122, 122, 122).toInt
  val LAND: Int = Color(255, 255, 255).toInt
  val END: Int = Color().toInt

  def typeOf(color: Int): Option[Int] = color match {
    case SOURCE => Some(SOURCE)
    case FLOW_IN => Some(FLOW_IN)
    case FLOW_OUT => Some(FLOW_OUT)
    case r if RIVER.contains(r) => Some(r)
    case _ => None
  }

}

case class RiverSources
(
  sources: Seq[Point] = Seq.empty,
  flowIns: Seq[Point] = Seq.empty,
  flowOuts: Seq[Point] = Seq.empty
)
