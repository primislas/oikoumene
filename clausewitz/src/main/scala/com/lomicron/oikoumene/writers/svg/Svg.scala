package com.lomicron.oikoumene.writers.svg

import java.text.DecimalFormat

import com.lomicron.oikoumene.model.Color
import com.lomicron.oikoumene.parsers.map.Point2D
import com.lomicron.utils.collection.CollectionUtils.toOption

object Svg {

  val svgHeader: SvgElement = SvgElement(
    tag = SvgTags.SVG,
    customAttrs = Some("xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink= \"http://www.w3.org/1999/xlink\"")
  )
  def colorToSvg(c: Color) = s"rgb(${c.r},${c.g},${c.b})"

  def pointsToSvgPointsAttribute(ps: Seq[Point2D] = Seq.empty): String = {
    val sb = StringBuilder.newBuilder
    sb.append(" points=\"")
    sb.append(ps.map(pointToSvg(_)).mkString(" "))
    sb.append("\"")
    sb.toString()
  }

  def pointsToSvgLinearPath(ps: Seq[Point2D] = Seq.empty, isClosed: Boolean = false): String = {
    val head = ps.headOption.map(p => s"M ${pointToSvg(p, " " )}").toSeq
    val polyline = ps
      .sliding(2, 1)
      .flatMap(pair => toPath(pair.head, pair.last))
    val closing = head.headOption.filter(_ => isClosed).map(_ => "Z").toSeq
    (head ++ polyline ++ closing).mkString(" ")
  }

  def pointsToQuadraticPath(ps: Seq[Point2D]): String = {
    if (ps.size < 3) pointsToSvgLinearPath(ps)
    else {
      val head = ps.head
      val moveTo = Seq(s"M ${pointToSvg(head, " " )}")

      var remainingPs = ps.drop(1)
      val q1 = remainingPs.head
      remainingPs = remainingPs.drop(1)
      val q2 = remainingPs.head
      val quad = Seq(s"q ${offset(q1, head)}, ${offset(q2, q1)}")

      val tail = remainingPs
        .sliding(2, 1)
        .map(s => s"t ${offset(s.last, s.head)}")

      (moveTo ++ quad ++ tail).mkString(" ")
    }
  }

  def toPath(p1: Point2D, p2: Point2D): Option[String] = {
    if (p1 == p2) Option.empty
    else if (p1.x == p2.x) s"v ${dySvg(p1, p2)}"
    else if (p1.y == p2.y) s"h ${dxSvg(p1, p2)}"
    else s"l ${dxSvg(p1, p2)} ${dySvg(p1, p2)}"
  }
  def offset(p2: Point2D, p1: Point2D): String = {
    val o = p2 - p1
    s"${doubleToSvg(o.x)} ${doubleToSvg(o.y)}"
  }
  private def dxSvg(p1: Point2D, p2: Point2D): String = doubleToSvg(p2.dx(p1))
  private def dySvg(p1: Point2D, p2: Point2D): String = doubleToSvg(p2.dy(p1))

  val df = new DecimalFormat("#.#")
  def doubleToSvg(d: Double): String = df.format(d)
  def pointToSvg(p: Point2D, separator: String = ","): String =
    s"${doubleToSvg(p.x)}$separator${doubleToSvg(p.y)}"

}
