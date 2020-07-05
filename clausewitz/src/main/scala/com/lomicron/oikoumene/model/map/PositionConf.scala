package com.lomicron.oikoumene.model.map

import com.lomicron.utils.geometry.Point2D

case class PositionConf
(
  position: Point2D,
  rotation: Double,
  height: Double,
)

object PositionConf {
  val ZERO: PositionConf = PositionConf(Point2D(0, 0), 0, 0)
}
