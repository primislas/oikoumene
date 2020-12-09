package com.lomicron.oikoumene.model.provinces

case class ProvinceProduction
(
  taxes: BigDecimal = 0,
  goodsProduced: BigDecimal = 0,
  production: BigDecimal = 0,
  tradePower: BigDecimal = 0,
  tradeValue: BigDecimal = 0,
  manpower: BigDecimal = 0,
  sailors: BigDecimal = 0,
)

object ProvinceProduction {
  val empty: ProvinceProduction = ProvinceProduction()
}
