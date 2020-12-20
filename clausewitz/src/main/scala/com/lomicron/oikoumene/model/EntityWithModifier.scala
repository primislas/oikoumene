package com.lomicron.oikoumene.model

trait EntityWithModifier extends Entity with WithModifier {
  override def modifierId: String = id
}
