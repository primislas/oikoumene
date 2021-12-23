package com.lomicron.eu4.service.map

import com.lomicron.eu4.model.map.{MapModes, RiverSegment}
import com.lomicron.eu4.model.provinces.{Province, ProvinceTypes}
import com.lomicron.eu4.parsers.map.RiverTypes

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

  def ofProvince(p: Province, mapMode: String): Seq[String] = {
    val po = Option(p)
    val isWasteland = po.filter(_.geography.isImpassable).map(_.`type`)
    val isUncolonized = po.filter(_.`type` == ProvinceTypes.uncolonized).map(_.`type`)
    val isCity = p.state.owner.map(_ => ProvinceTypes.city)
    val provSettlementStatus = isCity.orElse(isUncolonized).orElse(isWasteland)
    val coloredMode = mapMode match {
      case MapModes.POLITICAL => p.state.owner
      case MapModes.SIMPLE_TERRAIN => p.geography.terrain
      case MapModes.TRADE_NODES => p.geography.tradeNode
      case _ => None
    }

    mapMode match {
      case MapModes.SIMPLE_TERRAIN =>Seq(Some(p.`type`), coloredMode).flatten
      case _ =>
        val head =
          if (p.`type` == ProvinceTypes.wasteland || p.`type` == ProvinceTypes.uncolonized)
            ProvinceTypes.province
          else
            p.`type`
        head +: Seq(coloredMode, provSettlementStatus, p.geography.terrain).flatten
    }

  }

  def ofRiver(rs: RiverSegment): String = rs.width match {
    case RiverTypes.NARROW => RIVER_NARROW
    case RiverTypes.WIDE => RIVER_WIDE
    case RiverTypes.WIDEST => RIVER_WIDEST
    case _ => RIVER_NARROWEST
  }

}
