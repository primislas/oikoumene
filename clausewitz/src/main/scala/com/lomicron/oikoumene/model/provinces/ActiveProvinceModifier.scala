package com.lomicron.oikoumene.model.provinces

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.oikoumene.model.Entity.UNDEFINED
import com.lomicron.oikoumene.model.modifiers.Modifier
import com.lomicron.utils.parsing.tokenizer.Date

case class ActiveProvinceModifier
(
  name: String = UNDEFINED,
  effect: Option[Modifier] = None,
  added: Option[Date] = None,
  duration: Option[Int] = None,
) {

  @JsonCreator def this() = this(UNDEFINED)

}
