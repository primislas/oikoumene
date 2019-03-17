package com.lomicron.oikoumene.model

import com.lomicron.oikoumene.model.localisation.WithLocalisation

trait Entity extends WithLocalisation with StringId

object Entity {
  val UNDEFINED: String = "UNDEFINED"
}