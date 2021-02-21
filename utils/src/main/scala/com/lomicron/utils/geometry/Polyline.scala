package com.lomicron.utils.geometry

import com.fasterxml.jackson.core.{JsonGenerator, JsonParseException, JsonParser}
import com.fasterxml.jackson.databind._
import com.fasterxml.jackson.databind.annotation.{JsonDeserialize, JsonSerialize}
import com.fasterxml.jackson.databind.node.{ArrayNode, ObjectNode}
import com.lomicron.utils.json.JsonMapper

@JsonSerialize(using = classOf[PolylineSerializer])
@JsonDeserialize(using = classOf[PolylineDeserializer])
case class Polyline(points: Seq[Point2D] = Seq.empty) extends TPath {
  override def reverse: Polyline = copy(points.reverse)
}

class PolylineSerializer extends JsonSerializer[Polyline] {

  override def serialize(p: Polyline, json: JsonGenerator, serializers: SerializerProvider): Unit = {
    val pArray = JsonMapper.arrayNodeOf(p.points)
    val target = JsonMapper.objectNode("polyline", pArray)
    json.writeObject(target)
  }

}

class PolylineDeserializer extends JsonDeserializer[TPath] {

  override def deserialize(p: JsonParser, ctxt: DeserializationContext): TPath = {
    val node: JsonNode = p.getCodec.readTree(p)
    node match {
      case o: ObjectNode =>
        if (o.has("polyline")) {
          val points = Point2DDeserializer.deserialize(o, "polyline")
          Polyline(points)
        } else {
        throw new JsonParseException(p, s"Unrecognized Polyline json: $node")
      }
      case a: ArrayNode =>
        val points = Point2DDeserializer.deserialize(a)
        Polyline(points)
      case _ =>
        throw new JsonParseException(p, s"Unrecognized Polyline json: $node")
    }
  }

}
