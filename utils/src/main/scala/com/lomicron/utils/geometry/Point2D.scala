package com.lomicron.utils.geometry

import com.fasterxml.jackson.core.{JsonGenerator, JsonParseException, JsonParser}
import com.fasterxml.jackson.databind._
import com.fasterxml.jackson.databind.annotation.{JsonDeserialize, JsonSerialize}
import com.fasterxml.jackson.databind.node.{ArrayNode, ObjectNode}
import com.lomicron.utils.geometry.Geometry.{halfPI, threeHalfPI, twoPI}
import com.lomicron.utils.geometry.Point2D.ZERO
import com.lomicron.utils.json.FromJson
import com.lomicron.utils.json.JsonMapper.{ArrayNodeEx, ObjectNodeEx}
import com.typesafe.scalalogging.LazyLogging
import org.apache.commons.math3.fitting.WeightedObservedPoint

import java.awt.Point
import java.lang.Math.{PI, abs, atan}

@JsonSerialize(using = classOf[Point2DSerializer])
@JsonDeserialize(using = classOf[Point2DDeserializer])
case class Point2D(x: Double = 0, y: Double = 0) extends Rotatable[Point2D] { self =>

  def offset(p: Point2D): Point2D = this + p

  def -(p: Point2D): Point2D = Point2D(x - p.x, y - p.y)

  def +(p: Point2D): Point2D = Point2D(x + p.x, y + p.y)

  def *(m: Double): Point2D = Point2D(x * m, y * m)

  def *(p: Point2D): Double = x * p.x + y * p.y

  def toInt: Point = new Point(x.toInt, y.toInt)

  def dx(p: Point2D): Double = x - p.x
  def dy(p: Point2D): Double = y - p.y

  def reflectX(width: Double): Point2D = Point2D(width - x, y)
  def reflectY(height: Double): Point2D = Point2D(x, height - y)
  def flipXY: Point2D = Point2D(y, x)

  def sqrDistance(p: Point2D = ZERO): Double = Math.pow(x - p.x, 2) + Math.pow(y - p.y, 2)
  def distance(p: Point2D): Double = Math.sqrt(sqrDistance(p))
  def angleTo(p: Point2D): Double = {
    if (x == p.x) { if (p.y >= y) halfPI else threeHalfPI }
    else {
      val pdx = p.dx(self)
      val pdy = p.dy(self)
      val a = abs(atan(pdy / pdx))
      if (pdy >= 0) { if (pdx >= 0) a else PI - a }
      else if (pdx < 0) PI + a else twoPI - a
    }
  }

  override def rotate(c: Point2D, a: Double): Point2D = {
    val originalAngle = c.angleTo(self)
    val dist = distance(c)
    val nextAngle = originalAngle + a
    val rx = dist * Math.cos(nextAngle) + c.x
    val ry = dist * Math.sin(nextAngle) + c.y
    Point2D(rx, ry)
  }

  def normalize: Point2D = {
    val length = distance(ZERO)
    Point2D(x / length, y / length)
  }

  def isBetween(topLeft: Point2D, bottomRight: Point2D): Boolean =
    x >= topLeft.x && x <= bottomRight.x && y <= topLeft.y && y >= bottomRight.y

}

object Point2D extends FromJson[Point2D] {

  val ZERO: Point2D = Point2D()

  def apply(x: Int, y: Int): Point2D = Point2D(x.toDouble, y.toDouble)

  def apply(p: Point): Point2D = apply(p.x, p.y)

  def apply(wp: WeightedObservedPoint): Point2D = new Point2D(wp.getX, wp.getY)

  implicit def toPoint3D(p: Point): Point2D = apply(p)

}

object Point2DDeserializer extends LazyLogging {

  def deserialize(o: ObjectNode, f: String): Seq[Point2D] =
    o.getArray(f).map(deserialize).getOrElse(Seq.empty)

  def deserialize(points: ArrayNode): Seq[Point2D] =
    points.toSeq.map(deserialize)

  def deserialize(p: JsonNode): Point2D = p match {
    case o: ObjectNode =>
      val x = o.getBigDecimal("x").map(_.toDouble).getOrElse(0.0)
      val y = o.getBigDecimal("y").map(_.toDouble).getOrElse(0.0)
      Point2D(x, y)
    case a: ArrayNode =>
      val seq = a.toSeq
      val x = seq.headOption.map(_.asDouble).getOrElse(0.0)
      val y = seq.drop(1).headOption.map(_.asDouble).getOrElse(0.0)
      Point2D(x, y)
    case _ =>
      logger.warn(s"Invalid point JSON, returning ZERO point: $p")
      Point2D.ZERO
  }
}

class Point2DSerializer extends JsonSerializer[Point2D] {

  override def serialize(p: Point2D, json: JsonGenerator, serializers: SerializerProvider): Unit = {
    json.writeStartArray()
    json.writeNumber(BigDecimal(p.x).setScale(3, BigDecimal.RoundingMode.HALF_UP).doubleValue)
    json.writeNumber(BigDecimal(p.y).setScale(3, BigDecimal.RoundingMode.HALF_UP).doubleValue)
    json.writeEndArray()
  }

}

class Point2DDeserializer extends JsonDeserializer[Point2D] {

  override def deserialize(p: JsonParser, ctxt: DeserializationContext): Point2D = {
    val node: JsonNode = p.getCodec.readTree(p)
    node match {
      case o: ObjectNode =>
        if (!o.has("x") || !o.has("y"))
          throw new JsonParseException(p, s"Invalid Point2D json: $node")
        val x = o.get("x").asDouble()
        val y = o.get("y").asDouble()
        Point2D(x, y)
      case a: ArrayNode =>
        if (a.size() < 2) throw new JsonParseException(p, s"Invalid Point2D json: $node")
        val x = a.get(0).asDouble()
        val y = a.get(1).asDouble()
        Point2D(x, y)
      case _ =>
        throw new JsonParseException(p, s"Invalid Point2D json: $node")
    }
  }

}
