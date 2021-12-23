package com.lomicron.eu4.model

import com.lomicron.eu4.model.modifiers.Modifier

trait WithModifier {
  def modifierId: String
  def modifier: Option[Modifier]
  def modifierWithId: Option[Modifier] = modifier.map(_.withId(modifierId))
}
