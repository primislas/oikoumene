package com.lomicron.eu4.service.map.projections

case class ProjectionSettings
(
  id: Option[String] = None,
  centerX: Option[Int] = None,
  centerY: Option[Int] = None,
  longitudeOfCenter: Option[BigDecimal] = None,
  latitudeOfCenter: Option[BigDecimal] = None,
  standardParallel1: Option[BigDecimal] = None,
  standardParallel2: Option[BigDecimal] = None,
)
