package com.lomicron.oikoumene.tools.tracers.province

import com.lomicron.oikoumene.model.Color
import com.lomicron.oikoumene.writers.svg.Svg
import com.lomicron.utils.geometry.Border

case class ExportedBorder
(
  path: String,
  left: Color,
  right: Option[Color] = None,
)

object ExportedBorder {
  def apply(b: Border): ExportedBorder = {
    val path = Svg.pointsToSvgLinearPath(b.points)
    if (b.left.isEmpty || b.right.isEmpty) {
      val left = b.left.orElse(b.right).map(Color(_)).get
      ExportedBorder(path, left)
    } else
      ExportedBorder(path, b.left.map(Color(_)).get, b.right.map(Color(_)))
  }
}
