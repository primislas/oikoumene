package com.lomicron.eu4.model

import com.lomicron.oikoumene.model.Entity

trait EntityWithModifier extends Entity with WithModifier {
  override def modifierId: String = id
}
