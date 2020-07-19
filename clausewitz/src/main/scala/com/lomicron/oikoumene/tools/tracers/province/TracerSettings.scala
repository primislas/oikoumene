package com.lomicron.oikoumene.tools.tracers.province

case class TracerSettings
(
  input: String = "provinces.bmp",
  output: String = "provinces.json",
  includeBorders: Boolean = true,
  isHelp: Boolean = false,
)
