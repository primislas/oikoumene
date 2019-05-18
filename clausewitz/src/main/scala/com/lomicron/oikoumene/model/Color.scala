package com.lomicron.oikoumene.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.{ArrayNode, ObjectNode}
import com.lomicron.utils.json.FromJson
import com.lomicron.utils.json.JsonMapper._

case class Color
(r: Int = 0,
 g: Int = 0,
 b: Int = 0)
extends Ordered[Color]
{

  @JsonCreator def this() = this(0)

  override def compare(that: Color): Int = toInt - that.toInt

  private def toInt: Int = r * 1000000 +  g * 1000 + b

}

object Color extends FromJson[Color] {

  val black = new Color()

  @JsonCreator def apply(n: JsonNode): Color = n match {
    case a: ArrayNode => apply(a)
    case o: ObjectNode => apply(o)
  }

  def apply(argb: Int): Color = {
    val r = (argb >> 16) & 0xFF
    val g = (argb >> 8) & 0xFF
    val b = (argb >> 0) & 0xFF
    Color(r, g, b)
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
