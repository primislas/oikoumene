package com.lomicron.oikoumene.model.save.tag

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.utils.json.FromJson

@JsonCreator
case class TagCustomColors
(
  // hits = 6, isOptional = false, sample = 6
  color: Int = 0,
  // hits = 6, isOptional = false, sample = 53
  flag: Int = 0,
  // hits = 6, isOptional = false, sample = [0,6,6]
  flagColors: Vector[Int] = Vector.empty,
  // hits = 6, isOptional = false, sample = 79
  symbolIndex: Int = 0,
)

object TagCustomColors extends FromJson[TagCustomColors]
