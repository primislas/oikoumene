package com.lomicron.oikoumene.writers.svg

object SvgElements {

  val polygon: SvgElement = SvgElement(tag = SvgTags.POLYGON)
  val polyline: SvgElement = SvgElement(tag = SvgTags.POLYLINE)
  val path: SvgElement = SvgElement(tag = SvgTags.PATH)
  val group: SvgElement = SvgElement(tag = SvgTags.GROUP)
  val text: SvgElement = SvgElement(tag = SvgTags.TEXT)
  val textPath: SvgElement = SvgElement(tag = SvgTags.TEXT_PATH)

}
