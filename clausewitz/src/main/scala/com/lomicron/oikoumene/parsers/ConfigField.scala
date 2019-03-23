package com.lomicron.oikoumene.parsers

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node._
import com.lomicron.oikoumene.parsers.ConfigField.ValueTypes
import com.lomicron.utils.collection.CollectionUtils._
import com.lomicron.utils.json.{JsonMapper, ToJson}
import com.lomicron.utils.parsing.JsonParser
import com.typesafe.scalalogging.LazyLogging

import scala.collection.mutable

case class ConfigField
(field: String,
 isOptional: Boolean = true,
 valueType: String = ValueTypes.undefined,
 valueSample: Option[AnyRef] = None,
 hits: Int = 0) extends ToJson with Ordered[ConfigField] {

  override def compare(that: ConfigField): Int =
    if (hits != that.hits) that.hits.compareTo(hits)
    else field.compareTo(that.field)

}

object ConfigField extends LazyLogging {

  object ValueTypes {
    val undefined = "undefined"
    val `object` = "object"
    val array = "array"
    val boolean = "boolean"
    val number = "number"
    val string = "string"
  }

  def apply(entry: java.util.Map.Entry[String, JsonNode]): ConfigField =
    ConfigField(entry.getKey, valueType = getNodeType(entry.getValue), valueSample = Option(entry.getValue))

  def apply(entities: Seq[ObjectNode]): Seq[ConfigField] = {
    val allFields = entities.flatMap(_.fieldNames.toStream).toSet
    val commonFields = entities.map(_.fieldNames.toSeq.toSet).foldLeft(allFields)(_.intersect(_))

    entities
      .flatMap(_.fields.toStream)
      .map(ConfigField(_))
      .groupBy(_.field)
      .mapKVtoValue((field, vals) => {
        val types = vals.map(_.valueType).distinct
        val valueType = if (types.size <= 0) {
          logger.warn(s"Field $field has no identified value types")
          "undefined"
        } else {
          if (types.size > 1) {
            logger.warn(s"Field $field was identified as having multiple value types: $types")
            val countsByType = types.groupBy(identity).mapValues(_.size)
            countsByType.keys.foldLeft("undefined")((res, k) => {
              val isGreater = for {
                curr <- countsByType.get(res)
                check <- countsByType.get(k)
              } yield check > curr
              if (isGreater.contains(true)) k else res
            })
          }
          else types.head
        }
        vals.find(_.valueType == valueType).map(_.copy(hits = vals.size))
      })
      .values
      .flatten
      .toSeq
      .map(cf => if (commonFields.contains(cf.field)) cf.copy(isOptional = false) else cf)
      .sorted
  }

  def groupById(entities: Seq[ObjectNode]): mutable.Map[String, ConfigField] =
    apply(entities).foldLeft[mutable.Map[String, ConfigField]](mutable.LinkedHashMap[String, ConfigField]())((acc, e) => acc + (e.field -> e))

  def print(fields: Seq[ConfigField]): Unit = {
    fields
      .flatMap(cf => Seq(
        s"// hits = ${cf.hits}, isOptional = ${cf.isOptional}, sample = ${JsonMapper.toJson(cf.valueSample)}",
        s"${JsonParser.camelCase(cf.field)}: Option[${cf.valueType}] = None,"
      ))
      .foreach(println)
  }

  private val booleans = Set(TextNode.valueOf("yes"), TextNode.valueOf("no"))

  def getNodeType(n: JsonNode): String = n match {
    case _: ObjectNode => "Object"
    case t: TextNode => if (booleans.contains(t)) "Boolean" else "String"
    case _: BooleanNode => "Boolean"
    case _: ArrayNode => "Seq"
    case _: NumericNode => "Int"
    case _ => "UNKNOWN"
  }

}
