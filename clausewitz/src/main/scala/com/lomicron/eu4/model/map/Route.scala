package com.lomicron.eu4.model.map

import com.lomicron.eu4.model.provinces.{Province, ProvinceTypes}

case class Route
(
from: Int,
to: Int,
`type`: String = RouteTypes.IMPASSABLE,
cost: BigDecimal = 1
)

object Route {

  def apply(source: Province, target: Province): Route = {
    val rType = (source.geography.provinceType, target.geography.provinceType) match {
      case (ProvinceTypes.province, ProvinceTypes.province) => RouteTypes.LAND
      case (ProvinceTypes.province, ProvinceTypes.sea) => RouteTypes.BOARDING
      case (ProvinceTypes.sea, ProvinceTypes.province) => RouteTypes.LANDING
      case (ProvinceTypes.sea, ProvinceTypes.sea) => RouteTypes.SEA
      case _ => RouteTypes.IMPASSABLE
    }

    Route(source.id, target.id, rType)
  }

}
