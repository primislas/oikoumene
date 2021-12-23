package com.lomicron.eu4.repository.api.modifiers

import com.lomicron.eu4.model.modifiers.Modifier
import com.lomicron.oikoumene.repository.api.AbstractRepository

trait ModifierRepository extends AbstractRepository[String, Modifier] {

  def static: StaticModifierRepository

}
