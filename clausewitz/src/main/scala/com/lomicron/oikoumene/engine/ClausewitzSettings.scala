package com.lomicron.oikoumene.engine

import com.lomicron.oikoumene.model.provinces.{ActiveProvinceModifier, ProvinceUpdate}

object ClausewitzSettings {
  // TODO apply these as configurable modifiers
//  val TaxDevModifiers = ProvinceUpdate(tax)

  val yearlyTaxPerDev: BigDecimal = 1
  val localRecruitmentTimePerDev: BigDecimal = -0.01
  val goodsProducedPerDev: BigDecimal = 0.2
  val localShipbuildingTimePerDev: BigDecimal = -0.01
  val manpowerPerDev: Int = 250
  val garrisonGrowthPerDev: BigDecimal = 0.01

}
