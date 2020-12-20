package com.lomicron.oikoumene.model

import com.lomicron.oikoumene.model.modifiers.Modifier

trait WithModifier {
  def modifierId: String
  def modifier: Option[Modifier]
  def modifierWithId: Option[Modifier] = modifier.map(_.withId(modifierId))
}
