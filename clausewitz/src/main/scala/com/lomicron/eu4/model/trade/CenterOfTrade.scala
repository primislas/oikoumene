package com.lomicron.eu4.model.trade

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.eu4.model.modifiers.Modifier
import com.lomicron.eu4.model.trade.CenterOfTrade.Types
import com.lomicron.oikoumene.model.Entity
import com.lomicron.oikoumene.model.localisation.Localisation
import com.lomicron.utils.json.FromJson

@JsonCreator
case class CenterOfTrade
(
  id: String = Entity.UNDEFINED,
  localisation: Localisation = Localisation.empty,
  sourceFile: Option[String] = None,
  level: Int = 1,
  cost: Int = 0,
  `type`: String = Types.inland,
  provinceModifiers: Option[Modifier] = None,
  stateModifiers: Option[Modifier] = None,
  globalModifiers: Option[Modifier] = None,
) extends Entity

object CenterOfTrade extends FromJson[CenterOfTrade] {
  object Types {
    val inland = "inland"
    val coastal = "coastal"
  }
}
