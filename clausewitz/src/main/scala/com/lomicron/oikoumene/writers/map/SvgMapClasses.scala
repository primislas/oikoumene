package com.lomicron.oikoumene.writers.map

import com.lomicron.oikoumene.model.provinces.{Province, ProvinceTypes}

import scala.collection.immutable.ListSet

object SvgMapClasses {
  val PROVINCE_GROUP = "province-group"
  val PROVINCE: String = ProvinceTypes.province
  val UNCOLONIZED: String = ProvinceTypes.uncolonized
  val WASTELAND: String = ProvinceTypes.wasteland
  val SEA: String = ProvinceTypes.sea
  val LAKE: String = ProvinceTypes.lake
  val RIVER = "river"
  val BORDER = "border"

  private val province = ListSet(PROVINCE)

  def of(p: Province): ListSet[String] =
    province + p.`type`

}
