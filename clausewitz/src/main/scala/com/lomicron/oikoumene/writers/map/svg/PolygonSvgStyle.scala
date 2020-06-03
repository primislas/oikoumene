package com.lomicron.oikoumene.writers.map.svg

import com.lomicron.oikoumene.model.Color

case class PolygonSvgStyle
(
  fill: Option[Color] = None,
  strokeWidth: Option[Int] = None,
  stroke: Option[Color] = None,
  strokeOpacity: Option[Double] = None,
  opacity: Option[Double] = None,
) {

  def nonEmpty: Boolean = fill.isDefined ||
    strokeWidth.isDefined ||
    stroke.isDefined ||
    strokeOpacity.isDefined ||
    opacity.isDefined

  def isEmpty: Boolean = !nonEmpty

  def toSvg: String =
    if (isEmpty) ""
    else {
      val sb = StringBuilder.newBuilder
      sb.append("style=\"")
      fill.foreach(c => sb.append(s"fill:${colorToSvg(c)};"))
      strokeWidth.foreach(v => sb.append(s"stroke-width:$v;"))
      stroke.foreach(v => sb.append(s"stroke:${colorToSvg(v)};"))
      strokeOpacity.foreach(v => sb.append(s"stroke-opacity:$v;"))
      opacity.foreach(v => sb.append(s"opacity:$v;"))
      sb.append("\"")
      sb.toString()
    }

  def colorToSvg(c: Color) = s"rgb(${c.r},${c.g},${c.b})"

}

object PolygonSvgStyle {
  val empty: PolygonSvgStyle = PolygonSvgStyle()
}
