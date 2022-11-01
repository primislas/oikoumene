package com.lomicron.vicky.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.utils.json.FromJson

case class Chance
(
  // hits = 387, isOptional = false, sample = 2
  base: Int = 0,
  // hits = 384, isOptional = true, sample = [{"factor":3,"mechanical_production":1},{"factor":2,"organized_factories":1}]
  modifier: Seq[ObjectNode] = Seq.empty,
) {
  @JsonCreator def this() = this(0)
}

object Chance extends FromJson[Chance]
