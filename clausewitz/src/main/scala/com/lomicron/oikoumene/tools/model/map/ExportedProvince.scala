package com.lomicron.oikoumene.tools.model.map

case class ExportedProvince
(
  id: Int,
  `type`: Option[String] = None,
  terrain: Option[String] = None,
  shapes: Seq[PolygonSvgJson] = Seq.empty,
  area: Option[String] = None,
)
