package com.lomicron.oikoumene.repository.api.map

import com.lomicron.oikoumene.model.provinces.Province

object ProvinceFields {

  val tag = "tag"
  val core = "core"
  def tagOf(p: Province): Option[String] = p.state.owner
  def coreOf(p: Province): Set[String] = p.state.cores

  val religion = "religion"
  val religionGroup = "religion_group"
  val culture = "culture"
  val cultureGroup = "culture_group"
  def religionOf(p: Province): Option[String] = p.state.religion
  def religionGroupOf(p: Province): Option[String] = p.state.religionGroup
  def cultureOf(p: Province): Option[String] = p.state.culture
  def cultureGroupOf(p: Province): Option[String] = p.state.cultureGroup

  val area = "area"
  val region = "region"
  val superregion = "superregion"
  val continent = "continent"
  def areaOf(p: Province): Option[String] = p.geography.area
  def regionOf(p: Province): Option[String] = p.geography.region
  def superregionOf(p: Province): Option[String] = p.geography.superRegion
  def continentOf(p: Province): Option[String] = p.geography.continent

  val tradeGood = "trade_good"
  val tradeNode = "trade_node"
  def tradeGoodOf(p: Province): Option[String] = p.state.tradeGood
  def tradeNodeOf(p: Province): Option[String] = p.geography.tradeNode

  val history = "history"

}
