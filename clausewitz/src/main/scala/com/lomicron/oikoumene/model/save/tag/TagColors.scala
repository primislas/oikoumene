package com.lomicron.oikoumene.model.save.tag

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.oikoumene.model.Color
import com.lomicron.utils.json.FromJson

@JsonCreator
case class TagColors
(
  // hits = 1179, isOptional = false, sample = [150,150,150]
  countryColor: Color = Color.black,
  // hits = 1179, isOptional = false, sample = [150,150,150]
  mapColor: Color = Color.black,
  // hits = 62, isOptional = true, sample = [13,8,13]
  revolutionaryColors: Vector[Int] = Vector.empty,
  // hits = 6, isOptional = true, sample = {"flag":53,"color":6,"symbol_index":79,"flag_colors":[0,6,6]}
  customColors: Option[TagCustomColors] = None,
)

object TagColors extends FromJson[TagColors] {
  val empty: TagColors = new TagColors()
}
