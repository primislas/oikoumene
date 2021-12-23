package com.lomicron.eu4.model.provinces

import com.lomicron.eu4.model.provinces.ProvinceProduction.bd0

case class ProvinceProduction
(
  taxes: BigDecimal = bd0,
  goodsProduced: BigDecimal = bd0,
  production: BigDecimal = bd0,
  tradePower: BigDecimal = bd0,
  tradeValue: BigDecimal = bd0,
  manpower: BigDecimal = bd0,
  sailors: BigDecimal = bd0,
  landForcelimit: BigDecimal = bd0,
  navalForcelimit: BigDecimal = bd0,
) {
  def +(p: ProvinceProduction): ProvinceProduction =
    ProvinceProduction(
      taxes + p.taxes,
      goodsProduced + p.goodsProduced,
      production + p.production,
      tradePower + p.tradePower,
      tradeValue + p.tradeValue,
      manpower + p.manpower,
      sailors + p.sailors,
      landForcelimit + p.landForcelimit,
      navalForcelimit + p.navalForcelimit,
    )
}

object ProvinceProduction {
  val empty: ProvinceProduction = ProvinceProduction()
  val bd0: BigDecimal = BigDecimal(0)
}
