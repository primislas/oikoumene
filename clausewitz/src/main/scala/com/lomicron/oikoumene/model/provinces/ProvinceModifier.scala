package com.lomicron.oikoumene.model.provinces

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.oikoumene.model.Entity.UNDEFINED

case class ProvinceModifier
(name: String = UNDEFINED,
 duration: Option[Int] = None) {

  @JsonCreator def this() = this(UNDEFINED)

}
