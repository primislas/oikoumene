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
    val metadata = s"// hits = $hits, isOptional = $isOptional, sample = ${JsonMapper.toJson(valueSample).take(500)}"
    val camelCaseField = JsonParser.camelCase(field)
    val defVal = defaultValue.getOrElse("")
    val classField = caseClassField(camelCaseField, valueType, defVal)

    Seq(metadata, classField)
  }

  private def caseClassField(field: String, valueType: String, defaultVal: String) =
    if (field == "type") s"`$field`: $valueType = $defaultVal,"
    else s"$field: $valueType = $defaultVal,"

}

object ConfigField extends LazyLogging {

  object ValueTypes {
    val unknown = "UNKNOWN"
    val `object` = "ObjectNode"
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
      .mapValues(cfs => cfs.map(cf => if (cfs.size == entities.size) cf.copy(isOptional = false) else cf))
      .mapKVtoValue(aggregateFieldMetadata)
      .values.toSeq
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
    val hasId = fields.exists(cf => cf.field == "id" && !cf.isOptional && cf.valueType == ValueTypes.string)
    val hasLocalisation = fields.exists(cf => cf.field == "localisation")
    val isEntity = hasId && hasLocalisation
    val usesEntityConstant = fields.exists(cf => cf.defaultValue.contains("Entity.UNDEFINED"))
    val importsEntity = isEntity || usesEntityConstant
    val hasColor = fields.exists(_.field == "color")

    println("import com.fasterxml.jackson.annotation.JsonCreator")

    if (hasColor && importsEntity)
      println("import com.lomicron.oikoumene.model.{Color, Entity}")
    else if (importsEntity)
      println("import com.lomicron.oikoumene.model.Entity")
    else if (hasColor)
      println("import com.lomicron.oikoumene.model.Color")

    if (hasLocalisation) println("import com.lomicron.oikoumene.model.localisation.Localisation")

    println("import com.lomicron.utils.json.FromJson")
    println()

    println(s"case class $name")
    println("(")
    val id = fields.find(_.field == "id").toSeq
    val typE = fields.find(_.field == "`type`").toSeq
    val loc = fields.find(_.field == "localisation").toSeq
    val sourceFile = fields.find(_.field == "source_file").toSeq
    val reordered = id ++ typE ++ loc ++ sourceFile ++ fields.filterNot(f => reorderedFields.contains(f.field))
    reordered.flatMap(_.caseClassCode).foreach(println)

    val firstFieldDefault = reordered.headOption.flatMap(_.defaultValue).getOrElse("")
    if (isEntity) println(") extends Entity {")
    else println(") {")
    println(s"\t@JsonCreator def this() = this($firstFieldDefault)\n}")

    println()
    println(s"object $name extends FromJson[$name]")
    println()
  }

  private val booleans = Set(TextNode.valueOf("yes"), TextNode.valueOf("no"))
  private val reorderedFields = Set("id", "`type`", "localisation", "source_file")

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

  def getDefaultTypeValue(nodeType: String, field: String): String = nodeType match {
    case ValueTypes.`object` => field match {
      case "localisation" => "Localisation.empty"
      case "color" => "Color.black"
      case _ => "JsonMapper.objectNode"
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
        // TODO isFloatingPointNumber seems to return unexpected result, investigate
          if (vals.exists(_.valueSample.exists(_.isFloatingPointNumber))) ValueTypes.decimal
          else ValueTypes.integer
        else t
      }
      // array and single object means that in general the field is array of <object_type>
      else if (types.size == 2 && types.contains(ValueTypes.array))
        types.find(_ != ValueTypes.array).map(ValueTypes.arrayOf).get
      // if ints are sometimes reported back as decimals, the value is actually a decimal
      else if (types.size == 2 && types.contains(ValueTypes.decimal) &&
        (types.contains(ValueTypes.integer) || types.contains(ValueTypes.number)))
        ValueTypes.decimal
      else {
        logger.warn(s"Field $field was identified as having multiple value types: $types")
        getMostFrequentValueType(vals)
      }

    val withValueType = vals.head.copy(valueType = valueType, hits = vals.size)
    val withDefaultValue = setDefaultValue(withValueType, vals)

    withDefaultValue
  }

  private def setDefaultValue(cf: ConfigField, cfs: Seq[ConfigField]): ConfigField =
    if (cf.valueType == ValueTypes.boolean) {
      val distinctVals = cfs.flatMap(_.valueSample).distinct
      if (distinctVals.size == 1) {
        val v = distinctVals.head
        if (v == BooleanNode.getFalse) cf.copy(defaultValue = Some("true"))
        else if (v == BooleanNode.getTrue) cf.copy(defaultValue = Some("false"))
        else cf
      } else cf.copy(valueType = "Option[Boolean]", defaultValue = Some("None"))
    }
    else if (cf.valueType == ValueTypes.string)
      asDate(cf, cfs).getOrElse(evalAndSetDefaultValue(cf))
    else if (cf.valueType == ValueTypes.`object`)
      asDate(cf, cfs).orElse(asColor(cf, cfs)).getOrElse(evalAndSetDefaultValue(cf))
    else if (ValueTypes.isArray(cf.valueType))
      asColor(cf, cfs).getOrElse(evalAndSetDefaultValue(cf))
    else evalAndSetDefaultValue(cf)

  private def asDate(cf: ConfigField, cfs: Seq[ConfigField]): Option[ConfigField] = {
    val isDate = cfs.flatMap(_.valueSample).forall(ClausewitzParser.isDate)
    if (isDate) {
      val d =
        if (cf.isOptional) cf.copy(valueType = "Option[Date]", defaultValue = Option("None"))
        else cf.copy(valueType = "Date", defaultValue = Option("Date.zero"))
      Some(d)
    }
    else None
  }

  private def asColor(cf: ConfigField, cfs: Seq[ConfigField]): Option[ConfigField] = {
    val isColor = cfs.flatMap(_.valueSample).forall(ClausewitzParser.isColor)
    if (isColor) {
      val d =
        if (cf.isOptional) cf.copy(valueType = "Option[Color]", defaultValue = Option("None"))
        else cf.copy(valueType = "Color", defaultValue = Option("Color.black"))
      Some(d)
    }
    else None
  }

  private def evalAndSetDefaultValue(cf: ConfigField): ConfigField = {
    val (preciseType, defaultVal) = evalDefaultValue(cf)
    cf.copy(valueType = preciseType, defaultValue = Option(defaultVal))
  }

  private def evalDefaultValue(cf: ConfigField): (String, String) = {
    val preciseType =
      if (cf.field == "localisation") "Localisation"
      else if (ValueTypes.isArray(cf.valueType)) cf.valueType
      else if (cf.field == "color") "Color"
      else if (cf.field == "date") "Date"
      else cf.valueType

    val optType =
      if (cf.isOptional && !ValueTypes.isArray(preciseType)
        && !typesWithEmptyObjects.contains(preciseType) && cf.defaultValue.isEmpty) s"Option[$preciseType]"
      else preciseType

    val dv = cf.defaultValue.getOrElse(
      if (optType == "Color") "Color.black"
      else if (ValueTypes.isArray(optType)) "Seq.empty"
      else if (cf.field == "localisation") "Localisation.empty"
      else if (cf.isOptional) "None"
      else ConfigField.getDefaultTypeValue(cf.valueType, cf.field)
    )

    (optType, dv)
  }

  private val typesWithEmptyObjects = Set("Localisation", "Color")

  private def getMostFrequentValueType(cfs: Seq[ConfigField]): String = {
    val countsByType = cfs.map(_.valueType).groupBy(identity).mapValues(_.size)
    countsByType.keys.foldLeft("undefined")((res, k) => {
      val isGreater = for {
        curr <- countsByType.get(res)
        check <- countsByType.get(k)
      } yield check > curr
      if (isGreater.contains(true) || !countsByType.contains(res)) k else res
    })
  }

}
