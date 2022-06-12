package com.lomicron.utils.svg

import com.lomicron.oikoumene.model.Color

sealed trait SvgFill {
  def toSvg: String
}

case object SvgFillNone extends SvgFill {
  override def toSvg: String = s"""fill="none""""
}

case class FillColor(c: Color) extends SvgFill {
  override def toSvg: String = s"""fill="${Svg.colorToSvg(c)}""""
}

case class FillUrl(id: String) extends SvgFill {
  override def toSvg: String = s"""fill="url(#$id)""""
}

object SvgFill {
  val none: SvgFill = SvgFillNone
  def apply(): SvgFill = none
  def apply(c: Color): SvgFill = FillColor(c)
  def url(id: String): SvgFill = FillUrl(id)
}
