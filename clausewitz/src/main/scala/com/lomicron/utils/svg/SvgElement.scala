package com.lomicron.utils.svg

import com.lomicron.oikoumene.model.Color
import com.lomicron.utils.geometry.Point2D

import scala.collection.immutable.ListSet
import scala.collection.mutable

case class SvgElement
(
  tag: String,
  id: Option[String] = Option.empty,
  width: Option[Int] = Option.empty,
  height: Option[Int] = Option.empty,
  classes: ListSet[String] = ListSet.empty,
  href: Option[String] = Option.empty,
  fill: Option[SvgFill] = Option.empty,
  fillRule: Option[String] = Option.empty,
  opacity: Option[Double] = Option.empty,
  strokeWidth: Option[Double] = Option.empty,
  strokeOpacity: Option[Double] = Option.empty,
  strokeColor: Option[Color] = Option.empty,

  // butt,round,square
  strokeLinecap: Option[String] = Option.empty,
  points: Option[Seq[Point2D]] = Option.empty,
  customAttrs: Option[String] = Option.empty,

  // image
  x: Option[Int] = Option.empty,
  y: Option[Int] = Option.empty,
  patternUnits: Option[String] = Option.empty,

  // text
  startOffset: Option[String] = Option.empty,
  textLength: Option[String] = Option.empty,
  textAnchor: Option[String] = Option.empty,
  fontSize: Option[String] = Option.empty,
  dominantBaseline: Option[String] = Option.empty,

  // circle
  centerX: Option[Double] = Option.empty,
  centerY: Option[Double] = Option.empty,
  radius: Option[Double] = Option.empty,

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

  def addContent(c: String): SvgElement = copy(customContent = customContent.map(_.concat(c)).orElse(Option(c)))

  def addTitle(t: String): SvgElement =
    add(SvgElements.title.copy(customContent = Option(t)))

  def toSvg: String = toStringBuilder.toString

  def toStringBuilder: mutable.StringBuilder = {
    val attrs = Seq(
      id.map(i => s"""id="$i""""),
      svgClass,
      patternUnits.map(i => s"""patternUnits="$i""""),
      x.map(i => s"""x="$i""""),
      y.map(i => s"""y="$i""""),
      width.map(i => s"""width="$i""""),
      height.map(i => s"""height="$i""""),
      href.map(i => s"""xlink:href="$i""""),
      fill.map(_.toSvg),
      fillRule.map(i => s"""fill-rule="$i""""),
      opacity.map(i => s"""opacity="$i""""),
      strokeWidth.map(i => s"""stroke-width="${Svg.doubleToSvg(i)}""""),
      strokeOpacity.map(i => s"""stroke-opacity="${Svg.doubleToSvg(i)}""""),
      strokeColor.map(i => s"""stroke="${Svg.colorToSvg(i)}""""),
      strokeLinecap.map(i => s"""stroke-linecap="$i""""),

      startOffset.map(i => s"""startOffset="$i""""),
      textLength.map(i => s"""textLength="$i""""),
      textAnchor.map(i => s"""text-anchor="$i""""),
      fontSize.map(i => s"""font-size="$i""""),
      dominantBaseline.map(i => s"""dominant-baseline="$i""""),

      centerX.map(i => s"""cx="${Svg.doubleToSvg(i)}""""),
      centerY.map(i => s"""cy="${Svg.doubleToSvg(i)}""""),
      radius.map(i => s"""r="${Svg.doubleToSvg(i)}""""),

      customAttrs,
      points.map(i => Svg.pointsToSvgPointsAttribute(i)),
      path.map(i => s"""d="$i""""),
    )
      .flatten
      .mkString(" ")

    val sb = new mutable.StringBuilder()
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
