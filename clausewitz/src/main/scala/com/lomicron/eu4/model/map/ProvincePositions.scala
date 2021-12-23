package com.lomicron.eu4.model.map

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.utils.json.FromJson

case class ProvincePositions
(
  id: Int,
  city: PositionConf = PositionConf.ZERO,
  unit: PositionConf = PositionConf.ZERO,
  text: PositionConf = PositionConf.ZERO,
  port: PositionConf = PositionConf.ZERO,
  tradeRoute: PositionConf = PositionConf.ZERO,
  fightingUnit: PositionConf = PositionConf.ZERO,
  tradeWind: PositionConf = PositionConf.ZERO,
) extends FromJson[ProvincePositions] {

  @JsonCreator def this() = this(-1)

}
