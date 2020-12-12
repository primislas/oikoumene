package com.lomicron.oikoumene.model.provinces

import com.lomicron.oikoumene.model.Entity
import com.lomicron.oikoumene.model.Entity.UNDEFINED
import com.lomicron.oikoumene.model.localisation.Localisation
import com.lomicron.oikoumene.model.modifiers.Modifier
import com.lomicron.utils.json.FromJson
import com.lomicron.utils.json.JsonMapper.JsonMap

case class Building
(
  id: String = UNDEFINED,
  localisation: Localisation = Localisation.empty,
  cost: Int = 0,
  time: Int = 0,
  modifier: Modifier = Modifier.empty,
  aiWillDo: JsonMap = Map.empty,

  // optional fields
  makeObsolete: Option[String] = None,
  allowInGoldProvinces: Boolean = true,
  trigger: JsonMap = Map.empty,
  onmap: Boolean = false,
  influencingFort: Boolean = false,
  onePerCountry: Boolean = false,
  governmentSpecific: Boolean = false,

  manufactory: Seq[String] = Seq.empty,
  potential: JsonMap = Map.empty,
  showSeparate: Boolean = false
) extends Entity {
  def this() = this(UNDEFINED)
}

object Building extends FromJson[Building]
