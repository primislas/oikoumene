package com.lomicron.utils.parsing.serialization

case class SerializationSettings(fields: Seq[FieldConfig] = Seq.empty)

object SerializationSettings {
  val empty: SerializationSettings = new SerializationSettings()
}
