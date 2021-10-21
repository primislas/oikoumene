package com.lomicron.utils.geometry

import com.fasterxml.jackson.core.{JsonGenerator, JsonParseException, JsonParser}
import com.fasterxml.jackson.databind._
import com.fasterxml.jackson.databind.annotation.{JsonDeserialize, JsonSerialize}
import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.utils.json.JsonMapper
import com.typesafe.scalalogging.LazyLogging

@JsonSerialize(using = classOf[TPathSerializer])
@JsonDeserialize(using = classOf[TPathDeserializer])
trait TPath {
  def points: Seq[Point2D]
  def setPoints(ps: Seq[Point2D]): TPath

  def reverse: TPath = setPoints(points.reverse)
  def reflectY(height: Double): TPath = setPoints(points.map(_.reflectY(height)))
}

object TPath {
  type Polypath = Seq[TPath]
}

class TPathSerializer extends JsonSerializer[TPath] {

  override def serialize(p: TPath, json: JsonGenerator, serializers: SerializerProvider): Unit = {
    val field = p match {
      case _: Polyline => "polyline"
      case _: BezierCurve => "bezier"
    }
    val pArray = JsonMapper.arrayNodeOf(p.points)
    val target = JsonMapper.objectNode(field, pArray)
    json.writeObject(target)
  }

}

class TPathDeserializer extends JsonDeserializer[TPath] with LazyLogging {

  override def deserialize(p: JsonParser, ctxt: DeserializationContext): TPath = {
    val node: JsonNode = p.getCodec.readTree(p)
    node match {
      case o: ObjectNode =>
        if (o.has("bezier")) {
          val points = Point2DDeserializer.deserialize(o, "bezier")
          BezierCurve(points)
        } else if (o.has("polyline")) {
          val points = Point2DDeserializer.deserialize(o, "polyline")
          Polyline(points)
        } else {
          throw new JsonParseException(p, s"Unrecognized TPath json: $node")
        }
      case _ =>
        throw new JsonParseException(p, s"Unrecognized TPath json: $node")
    }
  }

}
