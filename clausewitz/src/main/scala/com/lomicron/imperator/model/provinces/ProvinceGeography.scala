package com.lomicron.imperator.model.provinces

import com.lomicron.eu4.model.provinces.ProvinceTypes
import com.lomicron.utils.json.FromJson

case class ProvinceGeography
(
  `type`: Option[String] = None,
  area: Option[String] = None,
  region: Option[String] = None,
  landlocked: Boolean = false,
) {
  def typeIs(t: String): Boolean = `type`.contains(t)
  def isImpassable: Boolean = `type`.exists(t => t == ProvinceTypes.wasteland || t == ProvinceTypes.impassable || t == ProvinceTypes.uninhabitable)
  def isLand: Boolean = `type`.exists(ProvinceTypes.landTypes.contains)
  def isWater: Boolean = !isLand
  def isRiver: Boolean = `type`.contains(ProvinceTypes.river)
  def isLake: Boolean = `type`.contains(ProvinceTypes.lake)
  def isSea: Boolean = `type`.contains(ProvinceTypes.sea)
  def isOcean: Boolean = `type`.contains(ProvinceTypes.sea)
  def isCoastal: Boolean = isLand && !landlocked
  def hasPort: Boolean = isCoastal
}

object ProvinceGeography extends FromJson[ProvinceGeography] {
  val empty: ProvinceGeography = ProvinceGeography()
}
