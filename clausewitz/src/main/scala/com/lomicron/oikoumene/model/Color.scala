package com.lomicron.oikoumene.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.{ArrayNode, ObjectNode, TextNode}
import com.lomicron.utils.json.FromJson
import com.lomicron.utils.json.JsonMapper._

@JsonCreator
case class Color
(r: Int = 0,
 g: Int = 0,
 b: Int = 0)
extends Ordered[Color]
{

  override def compare(that: Color): Int = toInt - that.toInt

  def toInt: Int = ((r << 16) | (g << 8) | b) | 0xFF000000

  override def hashCode(): Int = toInt

  override def equals(obj: Any): Boolean =
    obj != null && obj.isInstanceOf[Color] && toInt == obj.hashCode()

}

object Color extends FromJson[Color] {

  val black = new Color()

  @JsonCreator def apply(n: JsonNode): Color = n match {
    case a: ArrayNode => apply(a)
    case o: ObjectNode => apply(o)
    case t: TextNode => apply(t)
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
    if (isHSV(color)) {
      val h = color.getBigDecimal("r").getOrElse(BigDecimal(0))
      val s = color.getBigDecimal("g").getOrElse(BigDecimal(0))
      val v = color.getBigDecimal("b").getOrElse(BigDecimal(0))
      // TODO HSV to RGB
      Color(h.intValue, s.intValue, v.intValue)
    } else {
      val r = color.getInt("r").getOrElse(0)
      val g = color.getInt("g").getOrElse(0)
      val b = color.getInt("b").getOrElse(0)
      Color(r, g, b)
    }
  }

  def isHSV(color: ObjectNode): Boolean =
    color.has("h") && color.has("s") && color.has("v")

  def apply(color: TextNode): Color = {
    Color()
  }

}
