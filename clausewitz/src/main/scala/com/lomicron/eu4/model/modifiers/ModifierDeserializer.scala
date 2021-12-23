package com.lomicron.eu4.model.modifiers

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.{DeserializationContext, JsonDeserializer, JsonNode}
import com.lomicron.eu4.parsers.ClausewitzParser.Fields
import com.lomicron.oikoumene.model.localisation.Localisation
import com.lomicron.utils.json.JsonMapper.ObjectNodeEx
import com.typesafe.scalalogging.LazyLogging

class ModifierDeserializer extends JsonDeserializer[Modifier] with LazyLogging {
  override def deserialize(p: JsonParser, ctxt: DeserializationContext): Modifier = {
    val node: JsonNode = p.getCodec.readTree(p)
    node match {
      case o: ObjectNode =>

        val id = o.getString(Fields.idKey)
        val localisation = o.getObject(Fields.localisation).map(Localisation.fromJson)
        val sourceFile = o.getString(Fields.sourceFile)
        o.removeEx(Fields.idKey).removeEx(Fields.localisation).removeEx(Fields.sourceFile)
        Modifier(id, localisation, sourceFile, o)
      case _ =>
        logger.warn(s"Couldn't deserialize a modifier, expected an object node but found: ${node.asText()}")
        Modifier.empty
    }
  }
}
