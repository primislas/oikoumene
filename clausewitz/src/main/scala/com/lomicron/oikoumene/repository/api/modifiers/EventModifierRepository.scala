package com.lomicron.oikoumene.repository.api.modifiers

import com.lomicron.oikoumene.model.modifiers.Modifier
import com.lomicron.oikoumene.repository.api.AbstractRepository

trait EventModifierRepository extends AbstractRepository[String, Modifier]
