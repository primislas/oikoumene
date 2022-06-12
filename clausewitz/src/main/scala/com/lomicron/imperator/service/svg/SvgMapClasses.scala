package com.lomicron.imperator.service.svg

import com.lomicron.eu4.model.map.{MapModes, RiverSegment}
import com.lomicron.eu4.model.provinces.ProvinceTypes
import com.lomicron.eu4.parsers.map.RiverTypes
import com.lomicron.imperator.model.provinces.Province

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
    val geo = po.map(_.geography)
    val geoType = geo.flatMap(_.`type`)
    val isWasteland = geoType.filter(t => t == ProvinceTypes.wasteland)
    val isUncolonized = geoType.filter(t => t == ProvinceTypes.uncolonized)
    val isCity = geo.filter(g => g.isLand && p.state.owner.nonEmpty).flatMap(_.`type`)
    val provSettlementStatus = isCity.orElse(isUncolonized).orElse(isWasteland)
    val coloredMode = mapMode match {
      case MapModes.POLITICAL => p.state.owner
      case MapModes.SIMPLE_TERRAIN => Some(p.terrain)
      case _ => None
    }

    mapMode match {
      case MapModes.SIMPLE_TERRAIN => Seq(geoType, coloredMode).flatten
      case _ =>
        val head =
          if (geoType.contains(ProvinceTypes.wasteland) || geoType.contains(ProvinceTypes.uncolonized))
            ProvinceTypes.province
          else if (geoType.isEmpty) {
            ProvinceTypes.wasteland
          } else
            geoType.get
        head +: Seq(coloredMode, provSettlementStatus, Some(p.terrain)).flatten
    }

  }

  def ofRiver(rs: RiverSegment): String = rs.width match {
    case RiverTypes.NARROW => RIVER_NARROW
    case RiverTypes.WIDE => RIVER_WIDE
    case RiverTypes.WIDEST => RIVER_WIDEST
    case _ => RIVER_NARROWEST
  }

}
