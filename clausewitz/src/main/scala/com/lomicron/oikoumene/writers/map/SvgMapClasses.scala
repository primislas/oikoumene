package com.lomicron.oikoumene.writers.map

import com.lomicron.oikoumene.model.provinces.{Province, ProvinceTypes}
import com.lomicron.oikoumene.parsers.map.{RiverSegment, RiverTypes}

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

  val RIVER_GROUP = "river-group"
  val RIVER_NARROWEST = "river-narrowest"
  val RIVER_NARROW = "river-narrow"
  val RIVER_WIDE = "river-wide"
  val RIVER_WIDEST = "river-widest"

  private val province = ListSet(PROVINCE)
  private val river = ListSet(RIVER)

  def ofProvince(p: Province): ListSet[String] =
    province + p.`type`

  def ofRiver(rs: RiverSegment): ListSet[String] = {
    val width = rs.width match {
      case RiverTypes.NARROW => RIVER_NARROW
      case RiverTypes.WIDE => RIVER_WIDE
      case RiverTypes.WIDEST => RIVER_WIDEST
      case _ => RIVER_NARROWEST
    }
    river + width
  }


}
