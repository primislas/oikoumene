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
 valueType: String = ValueTypes.unknown,
 valueSample: Option[AnyRef] = None,
 hits: Int = 0) extends ToJson with Ordered[ConfigField] {

  override def compare(that: ConfigField): Int =
    if (hits != that.hits) that.hits.compareTo(hits)
    else field.compareTo(that.field)

  def caseClassCode: Seq[String] = {
    val metadata = s"// hits = $hits, isOptional = $isOptional, sample = ${JsonMapper.toJson(valueSample)}"

    val camelCaseField = JsonParser.camelCase(field)
    val preciseType =
      if (field == "localisation") "Localisation"
      else if (field == "type") "`type`"
      else if (valueType == ValueTypes.array) ValueTypes.array
      else if (isOptional) {
        if (field == "color") "Option[Color]"
        else s"Option[$valueType]"
      }
      else if (field == "color") "Color"
      else valueType
    val defaultValue =
      if (preciseType == "Color") "Color.black"
      else if (preciseType == ValueTypes.array) "Seq.empty"
      else if (field == "localisation") "Localisation.empty"
      else if (isOptional) "None"
      else ConfigField.getDefaultValue(valueType, field)

    val classField = caseClassField(camelCaseField, preciseType, defaultValue)

    Seq(metadata, classField)
  }

  private def caseClassField(field: String, valueType: String, defaultVal: String) =
    s"$field: $valueType = $defaultVal,"

}

object ConfigField extends LazyLogging {

  object ValueTypes {
    val unknown = "UNKNOWN"
    val `object` = "Object"
    val array = "Seq[]"
    val boolean = "Boolean"
    val number = "Int"
    val string = "String"
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

  def printCaseClass(entities: Seq[ObjectNode]): Unit =
    printCaseClass("ClassName", entities)

  def printCaseClass(name: String, entities: Seq[ObjectNode]) = {
    printCaseClassFromConfigs(name, apply(entities))
  }

  private def printCaseClassFromConfigs(name: String, fields: Seq[ConfigField]): Unit = {
    println("import com.fasterxml.jackson.annotation.JsonCreator")
    if (fields.exists(_.field == "color"))
      println("import com.lomicron.oikoumene.model.{Color, Entity}")
    else
      println("import com.lomicron.oikoumene.model.Entity")
    println("import com.lomicron.oikoumene.model.localisation.Localisation")
    println("import com.lomicron.utils.json.FromJson")
    println()

    println(s"case class $name")
    print("(")
    val id = fields.find(_.field == "id").toSeq
    val fs = id ++ fields.filter(_.field != "id")
    fs.flatMap(_.caseClassCode).foreach(println)
    println(") extends Entity {\n\t@JsonCreator def this() = this(Entity.UNDEFINED)\n}")

    println()
    println(s"object $name extends FromJson[$name]")
    println()
  }

  private val booleans = Set(TextNode.valueOf("yes"), TextNode.valueOf("no"))

  def getNodeType(n: JsonNode): String = n match {
    case _: ObjectNode => ValueTypes.`object`
    case t: TextNode => if (booleans.contains(t)) ValueTypes.boolean else ValueTypes.string
    case _: BooleanNode => ValueTypes.boolean
    case _: ArrayNode => ValueTypes.array
    case _: NumericNode => ValueTypes.number
    case _ => ValueTypes.unknown
  }

  def getDefaultValue(nodeType: String, field: String): String = nodeType match {
    case ValueTypes.`object` => field match {
      case "localisation" => "Localisation.empty"
      case "color" => "Color.black"
      case _ => "Object.empty"
    }
    case ValueTypes.string => "Entity.UNDEFINED"
    case ValueTypes.boolean => "false"
    case ValueTypes.array => "Seq.empty"
    case ValueTypes.number => "0"
    case _ => "None"
  }

}
