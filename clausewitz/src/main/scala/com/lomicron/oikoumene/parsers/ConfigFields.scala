package com.lomicron.oikoumene.parsers

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node._
import com.lomicron.utils.collection.CollectionUtils._
import com.typesafe.scalalogging.LazyLogging

import scala.collection.mutable

case class ConfigFields
(commonFields: Seq[String],
 optionalFields: Seq[String],
 fieldTypes: Map[String, String])

object ConfigFields extends LazyLogging {
  def apply(entities: Seq[ObjectNode]): ConfigFields = {
    val allFields = entities.flatMap(_.fieldNames.toStream).to[mutable.LinkedHashSet]
    val commonFields = entities.map(_.fieldNames.toSeq.toSet).foldLeft(allFields)(_.intersect(_))
    val optionalFields = allFields -- commonFields

    val fieldTypes = entities
      .flatMap(_.fields().toStream)
      .map(e => e.getKey -> getNodeType(e.getValue))
      .groupBy(_._1)
      .mapValues(_.map(_._2).distinct)
      .mapKVtoValue((field, types) => {
        if (types.size <= 0) {
          logger.warn(s"Field $field has no identified value types")
          "UNKNOWN"
        } else {
          if (types.size > 1) logger.warn(s"Field $field was identified as having multiple value types: $types")
          types.head
        }
      })

    ConfigFields(commonFields.toSeq, optionalFields.toSeq, fieldTypes)
  }

  private val booleans = Set(TextNode.valueOf("yes"), TextNode.valueOf("no"))

  private def getNodeType(n: JsonNode) = n match {
    case _: ObjectNode => "object"
    case t: TextNode => if (booleans.contains(t)) "boolean" else "string"
    case _: BooleanNode => "boolean"
    case _: ArrayNode => "array"
    case _: NumericNode => "number"
    case _ => "UNKNOWN"
  }

}