package com.lomicron.oikoumene.writers.svg

import com.lomicron.oikoumene.model.Color
import com.lomicron.oikoumene.parsers.map.Point2D

import scala.collection.immutable.ListSet

case class SvgElement
(
  tag: String,
  id: Option[String] = Option.empty,
  width: Option[Int] = Option.empty,
  height: Option[Int] = Option.empty,
  classes: ListSet[String] = ListSet.empty,
  fill: Option[SvgFill] = Option.empty,
  fillRule: Option[String] = Option.empty,
  opacity: Option[Double] = Option.empty,
  strokeWidth: Option[Int] = Option.empty,
  strokeOpacity: Option[Double] = Option.empty,
  strokeColor: Option[Color] = Option.empty,
  // butt,round,square
  strokeLinecap: Option[String] = Option.empty,
  points: Option[Seq[Point2D]] = Option.empty,
  customAttrs: Option[String] = Option.empty,
  customContent: Option[String] = Option.empty,
  children: Seq[SvgElement] = Seq.empty,
  path: Option[String] = Option.empty,
) {

  def add(e: SvgElement): SvgElement =
    copy(children = children :+ e)

  def add(es: Seq[SvgElement] = Seq.empty): SvgElement =
    copy(children = children ++ es)

  def addClass(c: String): SvgElement = copy(classes = classes + c)

  def addClasses(cs: Seq[String]): SvgElement = copy(classes = classes ++ cs)

  def clearClasses: SvgElement = copy(classes = ListSet.empty)

  def toSvg: String = toStringBuilder.toString

  def toStringBuilder: StringBuilder = {
    val attrs = Seq(
      id.map(i => s"""id="$i""""),
      svgClass,
      width.map(i => s"""width="$i""""),
      height.map(i => s"""height="$i""""),
      fill.map(_.toSvg),
      fillRule.map(i => s"""fill-rule="$i""""),
      opacity.map(i => s"""opacity="$i""""),
      strokeWidth.map(i => s"""stroke-width="$i""""),
      strokeOpacity.map(i => s"""stroke-opacity="$i""""),
      strokeColor.map(i => s"""stroke="${Svg.colorToSvg(i)}""""),
      strokeLinecap.map(i => s"""stroke-linecap="$i""""),
      customAttrs,
      points.map(i => Svg.pointsToSvgPointsAttribute(i)),
      path.map(i => s"""d="$i""""),
    )
      .flatten
      .mkString(" ")

    val sb = StringBuilder.newBuilder
    sb.append(s"<$tag")
    if (attrs.nonEmpty) sb.append(s" $attrs>")
    else sb.append(">")

    if (children.nonEmpty) {
      children.map(_.toStringBuilder).foreach(cb => sb.append("\n").append(cb))
      sb.append(s"\n")
    }
    customContent.foreach(sb.append)

    sb.append(s"</$tag>")

    sb
  }

  private def svgClass: Option[String] =
    Option(classes).filter(_.nonEmpty).map(cs => s"""class="${cs.mkString(" ")}"""")

}
