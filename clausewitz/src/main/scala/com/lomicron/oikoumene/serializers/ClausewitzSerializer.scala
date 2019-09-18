package com.lomicron.oikoumene.serializers

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.oikoumene.parsers.ClausewitzParser.Fields
import com.lomicron.utils.json.JsonMapper
import com.lomicron.utils.json.JsonMapper._


object ClausewitzSerializer {

  def serialize[T <: AnyRef](entity: T): Option[String] =
    Option(entity).flatMap(JsonMapper.toObjectNode).flatMap(serializeJsonObj)

  def serializeJsonObj(node: ObjectNode): Option[String] = {
    val events = node.getArray(Fields.history)
    node.remove(Fields.history)




    ???
  }

  def recSerialize(key: String, node: ObjectNode, depth: Int = 0): Seq[String] = {


    ???
  }

  def serializeJsonEntry(key: String, value: JsonNode, depth: Int = 0): Seq[String] =
    value match {
      case o: ObjectNode => recSerialize(key, o, depth + 1)
      case _ => Seq(s"$key = ${value.asText}")
    }

}
