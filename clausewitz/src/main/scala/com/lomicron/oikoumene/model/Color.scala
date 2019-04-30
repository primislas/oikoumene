package com.lomicron.oikoumene.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.{ArrayNode, ObjectNode}
import com.lomicron.utils.json.FromJson
import com.lomicron.utils.json.JsonMapper._

case class Color
(r: Int = 0,
 g: Int = 0,
 b: Int = 0) {

  @JsonCreator def this() = this(0)

}

object Color extends FromJson[Color] {

  val black = new Color()

  @JsonCreator def apply(n: JsonNode): Color = n match {
    case a: ArrayNode => apply(a)
    case o: ObjectNode => apply(o)
  }

  def apply(a: ArrayNode): Color = {
    val r = Option(a.get(0)).map(_.asInt).getOrElse(0)
    val g = Option(a.get(1)).map(_.asInt).getOrElse(0)
    val b = Option(a.get(2)).map(_.asInt).getOrElse(0)
    Color(r, g, b)
  }

  def apply(color: ObjectNode): Color = {
    val r = color.getInt("r").getOrElse(0)
    val g = color.getInt("g").getOrElse(0)
    val b = color.getInt("b").getOrElse(0)
    Color(r, g, b)
  }

}