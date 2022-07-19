package com.lomicron.utils.geometry

import com.fasterxml.jackson.core.{JsonGenerator, JsonParseException, JsonParser}
import com.fasterxml.jackson.databind._
import com.fasterxml.jackson.databind.annotation.{JsonDeserialize, JsonSerialize}
import com.fasterxml.jackson.databind.node.{ArrayNode, ObjectNode}
import com.lomicron.utils.json.JsonMapper

@JsonSerialize(using = classOf[BezierCurveSerializer])
@JsonDeserialize(using = classOf[BezierCurveDeserializer])
case class BezierCurve
(
  p1: Point2D = Point2D.ZERO,
  cp1: Point2D = Point2D.ZERO,
  cp2: Point2D = Point2D.ZERO,
  p2: Point2D = Point2D.ZERO,
) extends TPath {
  override def points: Seq[Point2D] = Seq(p1, cp1, cp2, p2)
  override def reverse: BezierCurve = copy(p2, cp2, cp1, p1)
  override def scale(coef: Double): TPath = BezierCurve(points.map(_ * coef))
  def toArray: Array[Point2D] = points.toArray
}

object BezierCurve {

  def apply(points: Seq[Point2D]): BezierCurve =
    apply(points.toArray)

  def apply(array: Array[Point2D]): BezierCurve = {
    val p1 = array.headOption.getOrElse(Point2D.ZERO)
    val cp1 = array.lift(1).getOrElse(Point2D.ZERO)
    val cp2 = array.lift(2).getOrElse(Point2D.ZERO)
    val p2 = array.lift(3).getOrElse(Point2D.ZERO)
    BezierCurve(p1, cp1, cp2, p2)
  }

}

class BezierCurveSerializer extends JsonSerializer[BezierCurve] {

  override def serialize(p: BezierCurve, json: JsonGenerator, serializers: SerializerProvider): Unit = {
    val pArray = JsonMapper.arrayNodeOf(p.points)
    val target = JsonMapper.objectNode("bezier", pArray)
    json.writeObject(target)
  }

}

class BezierCurveDeserializer extends JsonDeserializer[TPath] {

  override def deserialize(p: JsonParser, ctxt: DeserializationContext): TPath = {
    val node: JsonNode = p.getCodec.readTree(p)
    node match {
      case o: ObjectNode =>
        if (o.has("bezier")) {
          val points = Point2DDeserializer.deserialize(o, "bezier")
          BezierCurve(points)
        } else {
          throw new JsonParseException(p, s"Unrecognized BezierCurve json: $node")
        }
      case a: ArrayNode =>
        val points = Point2DDeserializer.deserialize(a)
        if (points.size != 4) throw new JsonParseException(p, s"Unrecognized BezierCurve json: $node")
        else BezierCurve(points)
      case _ =>
        throw new JsonParseException(p, s"Unrecognized BezierCurve json: $node")
    }
  }

}
