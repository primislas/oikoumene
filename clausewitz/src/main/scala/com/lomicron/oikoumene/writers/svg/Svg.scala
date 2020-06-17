package com.lomicron.oikoumene.writers.svg

import java.text.DecimalFormat

import com.lomicron.oikoumene.model.Color
import com.lomicron.oikoumene.parsers.map.Point2D

object Svg {

  val svgHeader: SvgElement = SvgElement(
    tag = SvgTags.SVG,
    customAttrs = Some("xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink= \"http://www.w3.org/1999/xlink\"")
  )
  def colorToSvg(c: Color) = s"rgb(${c.r},${c.g},${c.b})"

  def pointsToSvg(ps: Seq[Point2D] = Seq.empty): String = {
    val sb = StringBuilder.newBuilder
    sb.append(" points=\"")
    sb.append(ps.map(pointToSvg(_)).mkString(" "))
    sb.append("\"")
    sb.toString()
  }

  val df = new DecimalFormat("#.#")
  def doubleToSvg(d: Double): String = df.format(d)
  def pointToSvg(p: Point2D, separator: String = ","): String =
    s"${doubleToSvg(p.x)}$separator${doubleToSvg(p.y)}"

}
