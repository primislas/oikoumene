package com.lomicron.eu4.service.map.projections

import java.lang.Math.PI
import com.lomicron.utils.collection.CollectionUtils.toOption

trait ProjectionSettings
trait AlbersProjectionSettings extends ProjectionSettings {
  def longitudeOfCenter: Option[BigDecimal]
  def latitudeOfCenter: Option[BigDecimal]
  def standardParallel1: Option[BigDecimal]
  def standardParallel2: Option[BigDecimal]
}
trait BraunProjectionSettings extends ProjectionSettings {
  def primeMeridianX: Option[Int]
  def equatorY: Option[Int]
}

case class ProjectionSettingsAllFields
(
  id: Option[String] = None,

  primeMeridianX: Option[Int] = None,
  equatorY: Option[Int] = None,

  longitudeOfCenter: Option[BigDecimal] = None,
  latitudeOfCenter: Option[BigDecimal] = None,
  standardParallel1: Option[BigDecimal] = None,
  standardParallel2: Option[BigDecimal] = None,
)  extends BraunProjectionSettings with AlbersProjectionSettings

object AlbersProjectionSettings {
  private val radPerDegree = PI / 180
  private implicit def degreesToRadianSetting(degrees: Double): Option[BigDecimal] =
    Some(BigDecimal(degrees * radPerDegree))

  val europe: AlbersProjectionSettings =
    ProjectionSettingsAllFields(
      id = "albers-europe",
      longitudeOfCenter = 10,
      latitudeOfCenter = 30,
      standardParallel1 = 43,
      standardParallel2 = 62,
    ).asInstanceOf[AlbersProjectionSettings]
}

object BraunProjectionSettings {
  val typus: BraunProjectionSettings =
    ProjectionSettingsAllFields(
      id = "typus",
      primeMeridianX = 2994,
      equatorY = 1464,
    ).asInstanceOf[BraunProjectionSettings]
}
