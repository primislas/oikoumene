package com.lomicron.eu4.service.map.projections

trait ProjectionConfig

case class AlbersEqualConicalConfig
(
  longitudeOfCenter: Double,
  latitudeOfCenter: Double,
  standardParallel1: Double,
  standardParallel2: Double,
) extends ProjectionConfig
