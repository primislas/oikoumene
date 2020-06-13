package com.lomicron.oikoumene.writers.svg

import com.lomicron.oikoumene.model.Color

sealed trait SvgFill {
  def toSvg: String
}

case object None extends SvgFill {
  override def toSvg: String = s"""fill="none""""
}

case class FillColor(c: Color) extends SvgFill {
  override def toSvg: String = s"""fill="${Svg.colorToSvg(c)}""""
}

case class FillUrl(id: String) extends SvgFill {
  override def toSvg: String = s"""fill="url(#$id))""""
}

object SvgFill {
  val None: SvgFill = apply()
  def apply(): SvgFill = None
  def apply(c: Color): SvgFill = FillColor(c)
  def apply(id: String): SvgFill = FillUrl(id)
}