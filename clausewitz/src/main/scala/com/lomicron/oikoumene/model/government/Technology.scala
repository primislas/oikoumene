package com.lomicron.oikoumene.model.government

import com.fasterxml.jackson.annotation.{JsonCreator, JsonProperty}
import com.lomicron.oikoumene.model.Entity
import com.lomicron.oikoumene.model.government.Technology.defaultId
import com.lomicron.oikoumene.model.localisation.Localisation
import com.lomicron.oikoumene.model.modifiers.Modifier
import com.lomicron.utils.json.FromJson

case class Technology
(
  id: String = defaultId,
  localisation: Localisation = Localisation.empty,
  sourceFile: Option[String] = None,
  monarchPower: String = MonarchPowers.ADM,
  aheadOfTime: Option[Modifier] = None,
  @JsonProperty("technology")
  levels: IndexedSeq[TechLevel] = IndexedSeq.empty
) extends Entity {
  @JsonCreator def this() = this(defaultId)
}

object Technology extends FromJson[Technology] {
  val defaultLevel: Int = 1000
  val defaultId: String = s"${MonarchPowers.ADM}$defaultLevel"
}
