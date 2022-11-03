package com.lomicron.vicky.model.province

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.oikoumene.model.map.ProvincePositions
import com.lomicron.oikoumene.model.provinces.ProvinceTypes
import com.lomicron.utils.json.FromJson

@JsonCreator
case class ProvinceGeography
(
  `type`: Option[String] = None,
  terrain: Option[String] = None,
  climate: Seq[String] = Seq.empty,
  area: Option[String] = None,
  region: Option[String] = None,
  superRegion: Option[String] = None,
  continent: Option[String] = None,
  adjacencies: Set[Int] = Set.empty,
  crossings: Set[Int] = Set.empty,
  landlocked: Boolean = false,
  tradeNode: Option[String] = None,
  positions: Option[ProvincePositions] = None,
) {

  def isImpassable: Boolean = climate.contains("impassable")

  def isLand: Boolean = ProvinceTypes.landTypes.contains(provinceType)

  def isSea: Boolean = `type`.contains(ProvinceTypes.sea)

  def isCoastal: Boolean = isLand && !landlocked

  def hasPort: Boolean = isCoastal

  def provinceType: String = `type`.getOrElse(ProvinceTypes.wasteland)
}

object ProvinceGeography extends FromJson[ProvinceGeography] {
  val empty: ProvinceGeography = ProvinceGeography()
}
