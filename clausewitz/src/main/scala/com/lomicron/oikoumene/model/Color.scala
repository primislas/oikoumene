package com.lomicron.oikoumene.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.utils.json.FromJson

case class Color
(r: Int = 0,
 g: Int = 0,
 b: Int = 0) {

  @JsonCreator def this() = this(0)

}

object Color extends FromJson[Color] {

  val black = new Color()

}