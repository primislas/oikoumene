package com.lomicron.imperator.model.provinces

import com.lomicron.oikoumene.model.Entity
import com.lomicron.oikoumene.model.Entity.UNDEFINED
import com.lomicron.oikoumene.model.localisation.Localisation
import com.lomicron.utils.json.FromJson

case class Building
(
  id: String = UNDEFINED,
  localisation: Localisation = Localisation.empty,
) extends Entity {
  def this() = this(UNDEFINED)
}

object Building extends FromJson[Building]
