package com.lomicron.eu4.tools.model.map

import com.lomicron.eu4.service.svg.Svg
import com.lomicron.oikoumene.model.Color
import com.lomicron.utils.collection.CollectionUtils.toOption
import com.lomicron.utils.geometry.Polygon

case class PolygonSvgJson
(
  color: Option[Color] = None,
  provId: Option[Int] = None,
  path: String = "",
  clip: Seq[PolygonSvgJson] = Seq.empty,
  classes: Seq[String] = Seq.empty,
) {
  def setId(id: Int): PolygonSvgJson = copy(color = None, provId = id)
  def addClasses(cs: Seq[String]): PolygonSvgJson = copy(classes = classes ++ cs)
}

object PolygonSvgJson {
  def apply(p: Polygon): PolygonSvgJson = {
    val path = Svg.pointsToSvgLinearPath(p.points)
    PolygonSvgJson(Color(p.color), p.provinceId, path, p.clip.map(PolygonSvgJson(_).copy(color = None)))
  }
}
