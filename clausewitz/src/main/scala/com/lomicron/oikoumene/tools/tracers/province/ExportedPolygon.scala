package com.lomicron.oikoumene.tools.tracers.province

import com.lomicron.oikoumene.model.Color
import com.lomicron.oikoumene.writers.svg.Svg
import com.lomicron.utils.geometry.Polygon

case class ExportedPolygon
(
  color: Color,
  path: String,
  clip: Seq[ExportedPolygon] = Seq.empty
)

object ExportedPolygon {
  def apply(p: Polygon): ExportedPolygon = {
    val path = Svg.pointsToSvgLinearPath(p.points)
    ExportedPolygon(Color(p.color), path, p.clip.map(ExportedPolygon(_)))
  }
}
