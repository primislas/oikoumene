package com.lomicron.oikoumene.writers.svg

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
    sb.append(ps.map(p => f"${p.x}%.2f,${p.y}%.2f").mkString(" "))
    sb.append("\"")
    sb.toString()
  }

}
