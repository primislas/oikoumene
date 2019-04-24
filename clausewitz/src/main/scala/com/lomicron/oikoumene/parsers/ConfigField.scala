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
 valueSample: Option[JsonNode] = None,
 hits: Int = 0,
 defaultValue: Option[String] = None) extends ToJson with Ordered[ConfigField] {

  override def compare(that: ConfigField): Int =
    if (hits != that.hits) that.hits.compareTo(hits)
    else field.compareTo(that.field)

  def +(cf: ConfigField): ConfigField = copy(hits = hits + cf.hits)

  def caseClassCode: Seq[String] = {
    val metadata = s"// hits = $hits, isOptional = $isOptional, sample = ${JsonMapper.toJson(valueSample)}"
    val camelCaseField = JsonParser.camelCase(field)
    val (preciseType, defaultValue) = getTypeValue
    val classField = caseClassField(camelCaseField, preciseType, defaultValue)

    Seq(metadata, classField)
  }

  private def getTypeValue: (String, String) = {
    val preciseType =
      if (field == "localisation") "Localisation"
      else if (ValueTypes.isArray(valueType)) valueType
      else if (field == "color") "Color"
      else if (field == "date") "Date"
      else valueType

    val optType =
      if (isOptional && !ValueTypes.isArray(preciseType) && defaultValue.isEmpty) s"Option[$preciseType]"
      else preciseType

    val dv = defaultValue.getOrElse(
      if (optType == "Color") "Color.black"
      else if (ValueTypes.isArray(optType)) "Seq.empty"
      else if (field == "localisation") "Localisation.empty"
      else if (isOptional) "None"
      else ConfigField.getDefaultValue(valueType, field)
    )

    (optType, dv)
  }

  private def caseClassField(field: String, valueType: String, defaultVal: String) =
    if (field == "type") s"`$field`: $valueType = $defaultVal,"
    else s"$field: $valueType = $defaultVal,"

}

object ConfigField extends LazyLogging {

  object ValueTypes {
    val unknown = "UNKNOWN"
    val `object` = "Object"
    val array = "Seq[]"
    val boolean = "Boolean"
    val number = "Int"
    val integer = "Int"
    val decimal = "BigDecimal"
    val string = "String"

    def arrayOf(vt: String): String = s"Seq[$vt]"

    def isArray(vt: String): Boolean = vt.startsWith("Seq[")
  }

  def apply(entry: java.util.Map.Entry[String, JsonNode]): ConfigField =
    ConfigField(entry.getKey.toLowerCase, valueType = getNodeType(entry.getValue), valueSample = Option(entry.getValue))

  def apply(entities: Seq[ObjectNode]): Seq[ConfigField] = {
    entities
      .flatMap(_.fields.toStream)
      .map(ConfigField(_))
      .groupBy(_.field)
      .mapKVtoValue(aggregateFieldMetadata)
      .values.toSeq
      .map(cf => if (cf.hits == entities.size) cf.copy(isOptional = false) else cf)
      .sorted
  }

  def groupById(entities: Seq[ObjectNode]): mutable.Map[String, ConfigField] =
    apply(entities).foldLeft[mutable.Map[String, ConfigField]](mutable.LinkedHashMap[String, ConfigField]())((acc, e) => acc + (e.field -> e))

  def printCaseClass(entities: Seq[ObjectNode]): Unit =
    printCaseClass("ClassName", entities)

  def printCaseClass(name: String, entities: Seq[ObjectNode]): Unit = {
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
    println("(")
    val id = fields.find(_.field == "id").toSeq
    val fs = id ++ fields.filter(_.field != "id")
    fs.flatMap(_.caseClassCode).foreach(println)
    // TODO only extend entity if it has id: String and localisation
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
    case n: NumericNode =>
      // TODO isFloatingPointNumber returns 'true' for negative values such as -1
      if (n.isFloatingPointNumber) ValueTypes.decimal
      else ValueTypes.number
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
    case ValueTypes.integer => "0"
    case ValueTypes.decimal => "BigDecimal(0)"
    case _ => "None"
  }

  private def aggregateFieldMetadata(field: String, vals: Seq[ConfigField]): ConfigField = {
    val types = vals.map(_.valueType).distinct
    val valueType: String =
      if (types.size <= 0) {
        logger.warn(s"Field $field has no identified value types")
        "undefined"
      }
      else if (types.size == 1) {
        val t = types.head
        if (ValueTypes.isArray(t))
          vals.find(_.valueSample.exists(_.isArray))
            .flatMap(_.valueSample).map(_.get(0)).map(getNodeType).map(ValueTypes.arrayOf)
            .getOrElse(ValueTypes.arrayOf(ValueTypes.unknown))
        else if (ValueTypes.number == t)
            if (vals.exists(_.valueSample.exists(_.isFloatingPointNumber))) ValueTypes.decimal
            else ValueTypes.integer
        else t
      }
      else if (types.size == 2 && types.contains(ValueTypes.array))
        types.find(_ != ValueTypes.array).map(ValueTypes.arrayOf).get
      else {
        logger.warn(s"Field $field was identified as having multiple value types: $types")
        getMostFrequentValueType(vals)
      }

    val withValueType = vals.head.copy(valueType = valueType, hits = vals.size)
    val withDefaultValue = setDefaultValueType(withValueType, vals)

    withDefaultValue
  }

  private def setDefaultValueType(cf: ConfigField, cfs: Seq[ConfigField]): ConfigField =
    if (cf.valueType == ValueTypes.boolean) {
      val distinctVals = cfs.flatMap(_.valueSample).distinct
      if (distinctVals.size == 1) {
        val v = distinctVals.head
        if (v == BooleanNode.getFalse) cf.copy(defaultValue = Some("true"))
        else if (v == BooleanNode.getTrue) cf.copy(defaultValue = Some("false"))
        else cf
      } else cf
    } else cf

  private def getMostFrequentValueType(cfs: Seq[ConfigField]): String = {
    val countsByType = cfs.map(_.valueType).groupBy(identity).mapValues(_.size)
    countsByType.keys.foldLeft("undefined")((res, k) => {
      val isGreater = for {
        curr <- countsByType.get(res)
        check <- countsByType.get(k)
      } yield check > curr
      if (isGreater.contains(true) || countsByType.get(res).isEmpty) k else res
    })
  }


}
