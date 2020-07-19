package com.lomicron.oikoumene.tools.tracers.province

import com.lomicron.utils.json.ToJson

case class ExportedProvs
(
  provinces: Seq[ExportedPolygon] = Seq.empty,
  borders: Seq[ExportedBorder] = Seq.empty,
)
extends ToJson
