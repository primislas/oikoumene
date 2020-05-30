package com.lomicron.utils.parsing.serialization

case class FieldConfig
(
  field: Option[String],
  fieldSuffix: Option[String],
  fieldPattern: Option[String],
  fieldType: String = ClausewitzFieldTypes.ClzObject
)
{

  def matches(f: String): Boolean =
    field.contains(f) ||
      fieldSuffix.exists(f.endsWith) ||
      fieldPattern.exists(f.matches)

  def isArray: Boolean = ClausewitzFieldTypes.isArray(fieldType)

}
