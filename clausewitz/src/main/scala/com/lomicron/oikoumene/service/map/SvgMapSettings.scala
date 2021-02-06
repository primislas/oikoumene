package com.lomicron.oikoumene.service.map

import com.lomicron.oikoumene.service.map.SvgMapSettings.{defaultApproximationError, defaultPointDecimalPrecision}

case class SvgMapSettings
(
  pointDecimalPrecision: Int = defaultPointDecimalPrecision,
  approximationError: Double = defaultApproximationError,
)

object SvgMapSettings {
  val defaultPointDecimalPrecision = 1
  val defaultApproximationError = 1.5

  val default: SvgMapSettings = SvgMapSettings()
}
