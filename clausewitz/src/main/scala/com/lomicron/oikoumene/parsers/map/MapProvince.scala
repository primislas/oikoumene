package com.lomicron.oikoumene.parsers.map

import com.lomicron.oikoumene.model.Color

case class MapProvince
(
owner: Option[String] = None,
ownerColor: Option[Color] = None,
`type`: Option[String] = None,
terrain: Option[String] = None,
terrainColor: Option[String] = None
)
