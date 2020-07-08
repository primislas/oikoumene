package com.lomicron.oikoumene.writers.map

import com.lomicron.oikoumene.model.map.RiverSegment
import com.lomicron.oikoumene.model.provinces.{Province, ProvinceTypes}
import com.lomicron.oikoumene.parsers.map.RiverTypes

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

  val BORDER_GROUP = "border-group"

  private val province = ListSet(PROVINCE)
  private val river = ListSet(RIVER)

  def ofProvince(p: Province): Seq[String] = {
    val isCity = p.state.owner.map(_ => ProvinceTypes.city)
    Seq(Some(p.`type`), p.state.owner, isCity, p.geography.terrain).flatten
  }

  def ofRiver(rs: RiverSegment): String = rs.width match {
    case RiverTypes.NARROW => RIVER_NARROW
    case RiverTypes.WIDE => RIVER_WIDE
    case RiverTypes.WIDEST => RIVER_WIDEST
    case _ => RIVER_NARROWEST
  }

}
