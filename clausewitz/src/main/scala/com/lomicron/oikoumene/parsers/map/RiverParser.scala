package com.lomicron.oikoumene.parsers.map

import java.awt.Point
import java.awt.image.BufferedImage

import com.lomicron.oikoumene.model.Color
import com.lomicron.oikoumene.parsers.map.MapParser.getRGB
import com.typesafe.scalalogging.LazyLogging

object RiverParser {

  import RiverTypes._

  val riverColors = Set(SOURCE, FLOW_IN, FLOW_OUT, NARROWEST, NARROW, WIDE, WIDEST)
  val waterColors = Set(NARROWEST, NARROW, WIDE, WIDEST)
  val edgeColors = Set(SOURCE, FLOW_IN, FLOW_OUT)
  val nonRiverColors = Set(LAND, SEA)

  def trace(img: BufferedImage): Seq[River] = {

    var id = -1

    def nextId: Int = {
      id += 1
      id
    }

    var rivers = Seq.empty[River]

    val parsed = Array.ofDim[Boolean](img.getWidth, img.getHeight)
    for (y <- 0 until img.getHeight; x <- 0 until img.getWidth) {
      val co = getRGB(img, x, y)
      if (co.exists(c => !nonRiverColors.contains(c) && !parsed(x)(y))) {
        val r = RiverParser(img, co.get).trace(new Point(x, y))
        r.path.foreach(p => parsed(p.x.toInt)(p.y.toInt) = true)
        rivers = rivers :+ r
      }
    }

    rivers
  }


}

case class RiverParser(img: BufferedImage, color: Int) extends BitmapWalker with LazyLogging {

  import RiverParser._
  import RiverTypes._

  def trace(p: Point): River = {
    val d1 = Up.directions.find(neighborColor(p, _).exists(riverColors.contains))
    val d2 = d1.flatMap(_.directionsAfter().find(neighborColor(p, _).exists(riverColors.contains)))

    val r1 = d1.map(traceDirection(p, _)).getOrElse(TraceResult())
    val r2 = d2.map(traceDirection(p, _)).getOrElse(TraceResult())

    if (r2.ps.isEmpty) {
      if (r1.ps.isEmpty) River(Seq.empty[Point2D], color)
      else {
        if (r1.directionType.isDefined) r1.toRiver(color)
        else {
          val withDirection = if (r1.ps.headOption.exists(isInLand)) r1.ps.reverse else r1.ps
          River.ofInts(withDirection, color)
        }
      }
    } else {
      val ps = if (r1.directionType.isEmpty && r2.directionType.isEmpty) {
        logger.warn(s"Failed to identify direction of river at $p")
        r1.ps ++ r2.ps.drop(1).reverse
      }
      else if (r2.directionType.contains(SOURCE) || r1.directionType.contains(END))
        r1.ps ++ r2.ps.drop(1).reverse
      else
        r2.ps ++ r1.ps.drop(1).reverse

      River.ofInts(ps, color)
    }

  }

  def traceDirection(p: Point, d: Direction): TraceResult = {
    var points = Seq(p)
    var currentPoint = p
    var currentColor = color
    var nextD = Option(d)

    do {
      currentPoint = neighbor(currentPoint, nextD.get).get
      currentColor = colorOf(currentPoint)
      points = points :+ currentPoint
      nextD = nextD.flatMap(nextRiverPoint(currentPoint, _))
    } while (nextD.isDefined && !edgeColors.contains(currentColor))

    val riverDirection = typeOf(currentPoint, currentColor)

    TraceResult(points, riverDirection)
  }

  def nextRiverPoint(p: Point, d: Direction): Option[Direction] =
    d.directionsForward() find (neighborColor(p, _).exists(riverColors.contains))

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

}

object RiverTypes {
  val SOURCE: Int = Color(0, 255).toInt
  val FLOW_IN: Int = Color(255).toInt
  val FLOW_OUT: Int = Color(255, 252).toInt
  val NARROWEST: Int = Color(0, 225, 255).toInt
  val NARROW: Int = Color(0, 200, 255).toInt
  val WIDE: Int = Color(0, 100, 255).toInt
  val WIDEST: Int = Color(0, 0, 200).toInt
  val SEA: Int = Color(122, 122, 122).toInt
  val LAND: Int = Color(255, 255, 255).toInt
  val END: Int = Color().toInt

  def typeOf(color: Int): Option[Int] = color match {
    case SOURCE => Some(SOURCE)
    case FLOW_IN => Some(FLOW_IN)
    case FLOW_OUT => Some(FLOW_OUT)
    case NARROWEST => Some(NARROWEST)
    case NARROW => Some(NARROW)
    case WIDE => Some(WIDE)
    case WIDEST => Some(WIDEST)
    case _ => None
  }

}

case class TraceResult(ps: Seq[Point] = Seq.empty, directionType: Option[Int] = None) {
  def toRiver(c: Int): River = {
    val withDirection = if (directionType.contains(RiverTypes.SOURCE)) ps.reverse else ps
    River.ofInts(withDirection, c)
  }
}
