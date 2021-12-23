package com.lomicron.eu4.model.modifiers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.{JsonSerializer, SerializerProvider}
import com.lomicron.utils.json.JsonMapper
import com.lomicron.utils.json.JsonMapper.ObjectNodeEx

class ModifierSerializer extends JsonSerializer[Modifier] {

  override def serialize(m: Modifier, json: JsonGenerator, serializers: SerializerProvider): Unit = {
    val target = JsonMapper.objectNode
    m.id.foreach(target.setEx("id", _))
    m.localisation.map(JsonMapper.toJsonNode).foreach(target.setEx("localisation", _))
    m.sourceFile.foreach(target.setEx("source_file", _))
    m.conf.entrySeq().foreach(e => target.setEx(e.getKey, e.getValue))
    json.writeObject(target)
  }

}
