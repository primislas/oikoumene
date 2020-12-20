package com.lomicron.oikoumene.model.modifiers

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.oikoumene.model.Entity.UNDEFINED
import com.lomicron.utils.parsing.tokenizer.Date

case class ActiveModifier
(
  name: String = UNDEFINED,
  effect: Option[Modifier] = None,
  added: Option[Date] = None,
  duration: Option[Int] = None,
) {

  @JsonCreator def this() = this(UNDEFINED)

}

object ActiveModifier {
  def of(m: Modifier): ActiveModifier = ActiveModifier(name = m.id.getOrElse(UNDEFINED), effect = Some(m))
}
