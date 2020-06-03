package com.lomicron.oikoumene.writers.map.svg

import com.lomicron.oikoumene.model.Color
import com.lomicron.oikoumene.parsers.map.Polygon
import com.lomicron.utils.collection.CollectionUtils.toOption

object Svg {

  val svgHeader = "<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink= \"http://www.w3.org/1999/xlink\">"

  val provinceStyle: PolygonSvgStyle =
    PolygonSvgStyle(None, 1, Color(), 0.5, 0.5)
  val groupStyle: PolygonSvgStyle =
    PolygonSvgStyle(stroke = Color(), strokeWidth = 2)

  def build(polygons: Seq[Polygon]): String =
    wrapAsSvg(toSvg(polygons))

  def wrapAsSvg(sb: StringBuilder): String = {
    val envelope = StringBuilder.newBuilder
    envelope.append(svgHeader)
    envelope.append(sb)
    envelope.append("\n</svg>")
    envelope.toString()
  }

  def toSvg(polygons: Seq[Polygon]): StringBuilder = {
    val sb = StringBuilder.newBuilder
    sb.append("<g ")
    sb.append(provinceStyle.toSvg)
    sb.append(">")
    polygons.filter(_.nonEmpty).map(toSvg(_)).foreach(sb.append)
    sb.append("</g>")
    sb
  }

  def toSvg(p: Polygon, style: PolygonSvgStyle = PolygonSvgStyle.empty): StringBuilder = {
    val svgStyle = style.copy(fill = Some(Color(p.color)))
    val sb = StringBuilder.newBuilder
    sb.append("\n<polygon")
    sb.append(" points=\"")
    sb.append(p.points.map(p => s"${p.x},${p.y}").mkString(" "))
    sb.append("\" ")
    sb.append(svgStyle.toSvg)
    sb.append("></polygon>")
    sb
  }

}
