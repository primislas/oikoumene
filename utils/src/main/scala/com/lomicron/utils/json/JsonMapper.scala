package com.lomicron.utils.json

import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY
import com.fasterxml.jackson.core.JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN
import com.fasterxml.jackson.core.`type`.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature.{ACCEPT_SINGLE_VALUE_AS_ARRAY, FAIL_ON_UNKNOWN_PROPERTIES}
import com.fasterxml.jackson.databind._
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.node._
import com.fasterxml.jackson.module.scala.{DefaultScalaModule, ScalaObjectMapper}
import com.lomicron.utils.collection.CollectionUtils._
import com.lomicron.utils.json.JsonMapper._
import com.typesafe.scalalogging.LazyLogging

import java.util.Map.Entry
import scala.util.Try

case class JsonMapper(mapper: ObjectMapper with ScalaObjectMapper) extends LazyLogging {

  private val writer = defaultWriter(mapper)
  private val prettyPrinter = defaultWriter(mapper).withDefaultPrettyPrinter
  private val jsonNodeTypeRef = new TypeReference[JsonNode] {}

  private def defaultWriter(mapper: ObjectMapper) =
    mapper.writer.`with`(WRITE_BIGDECIMAL_AS_PLAIN)

  def configure(mapper: ObjectMapper with ScalaObjectMapper): JsonMapper =
    JsonMapper(mapper)

  def toJson(obj: AnyRef): String = obj match {
    case s: String => s
    case _ => writer.writeValueAsString(obj)
  }

  def prettyPrint(obj: AnyRef): String = obj match {
    case s: String => prettyPrint(toJsonNode(s))
    case _ => prettyPrinter.writeValueAsString(obj)
  }

  def fromJson[T: Manifest](json: String): T =
    mapper.readValue[T](json)

  def clone[T <: AnyRef](obj: T): T =
    convert[obj.type](obj)

  def convert[T: Manifest](source: AnyRef): T =
    fromJson[T](toJson(source))

  def toJsonNode(obj: AnyRef): JsonNode =
    obj match {
      case node: JsonNode => node
      case _ => mapper.convertValue(obj, jsonNodeTypeRef)
    }

  def toJsonNodeFromVal(obj: AnyVal): JsonNode =
    mapper.convertValue(obj, jsonNodeTypeRef)

  def toObjectNode(obj: AnyRef): Option[ObjectNode] =
    Try(toJsonNode(obj).asInstanceOf[ObjectNode]).toOption

  def objectNode: ObjectNode = new ObjectNode(JsonNodeFactory.instance)

  def objectNode(field: String, value: JsonNode): ObjectNode =
    objectNode.set(field, value).asInstanceOf[ObjectNode]

  def arrayNode: ArrayNode = new ArrayNode(JsonNodeFactory.instance)

  def arrayNodeOf(e: AnyRef): ArrayNode =
    arrayNode.add(toJsonNode(e))

  def arrayNodeOf(args: Seq[AnyRef]): ArrayNode =
    args.map(JsonMapper.toJsonNode).foldLeft(arrayNode)(_.add(_))

  def arrayNodeOfVals(args: Seq[AnyVal]): ArrayNode =
    args.map(JsonMapper.toJsonNodeFromVal).foldLeft(arrayNode)(_.add(_))

  def textNode(t: String): TextNode = TextNode.valueOf(t)

  def booleanNode(b: Boolean): BooleanNode = BooleanNode.valueOf(b)

  def numericNode(int: Int): NumericNode = IntNode.valueOf(int)

  def numericNode(double: Double): NumericNode = DoubleNode.valueOf(double)

  def numericNode(bigDecimal: BigDecimal): NumericNode = DoubleNode.valueOf(bigDecimal.doubleValue)

  def patch[T <: AnyRef, P <: AnyRef]
  (target: T, update: P): T =
    patch(target, update, patch)

  def patchMerge[T <: AnyRef, P <: AnyRef]
  (target: T, update: P): T =
    patch(target, update, patchMerge)

  private def patch[T <: AnyRef, P <: AnyRef]
  (target: T,
   update: P,
   f: (ObjectNode, ObjectNode) => ObjectNode): T = {

    val patched = for {
      t <- toObjectNode(target)
      p <- toObjectNode(update)
    } yield f(t, p)
    patched.map(convert[target.type](_)).getOrElse(target)
  }

  def patch(target: ObjectNode, update: ObjectNode): ObjectNode =
    patchF(target, update, overwriteField)

  def patchMerge(target: ObjectNode, update: ObjectNode): ObjectNode =
    patchF(target, update, mergeField)

  private def patchF(target: ObjectNode,
                     update: ObjectNode,
                     f: (ObjectNode, Entry[String, JsonNode]) => ObjectNode) = {
    update.fields().forEachRemaining(e => f(target, e))
    target
  }

  private def overwriteField(target: ObjectNode, e: Entry[String, JsonNode]): ObjectNode = {
    target.set(e.getKey, e.getValue)
    target
  }

  private def mergeField(target: ObjectNode, e: Entry[String, JsonNode]): ObjectNode =
    patchFieldValue(target, e.getKey, e.getValue)

  def patchFieldValue(target: ObjectNode, k: String, update: JsonNode): ObjectNode = {
    if (!target.has(k)) target.set(k, update)
    else {
      val existing = target.get(k)

      if (existing.isArray) {
        val existingArray = existing.asInstanceOf[ArrayNode]
        if (update.isArray) update.forEach(n => existingArray.add(n))
        else existingArray.add(update)

      } else if (existing.isObject && update.isObject) {
        patchMerge(existing.asInstanceOf[ObjectNode], update.asInstanceOf[ObjectNode])

      } else if (existing.isNumber && update.isNumber) {
        JsonNodeFactory.instance.numberNode(new java.math.BigDecimal(existing.asText).add(new java.math.BigDecimal(update.asText)))
      } else {
        target.set(k, update)
      }

    }
    target
  }

  /**
    * Would merge values rather than overwriting or patching them
    * using the following approach:
    * <ul>
    *   <li> Values existing on the same key will be combined into an array.
    *   This is useful for configs which can repeat the same field several times
    *   as a way of describing a collection of values, like 'discoveredBy'.
    *   <li> Numbers will be added together.
    * </ul>
    *
    *
    * @param target object to which update is applied
    * @param k field to which update is applied
    * @param update field update
    * @return target object with field update merged to provided field
    */
  def mergeFieldValue(target: ObjectNode, k: String, update: JsonNode): ObjectNode = {
    if (!target.has(k)) target.set(k, update)
    else {
      val existing = target.get(k)
      if (existing.isArray) {
        val existingArray = existing.asInstanceOf[ArrayNode]
        if (update.isArray) update.forEach(n => existingArray.add(n))
        else existingArray.add(update)
      } else if (existing.isNumber && update.isNumber) {
        JsonNodeFactory.instance.numberNode(new java.math.BigDecimal(existing.asText).add(new java.math.BigDecimal(update.asText)))
      } else {
        target.setEx(k, arrayNodeOf(Seq(existing, update)))
      }

    }
    target
  }

  def removeFieldValue(o: ObjectNode, k: String, v: JsonNode): ObjectNode = {
    o.getField(k)
      .foreach(existingVal => {
        if (existingVal.isArray) {
          val it = existingVal.asInstanceOf[ArrayNode].iterator
          var removed = false
          while (it.hasNext && !removed) {
            val e = it.next
            if (v.equals(e)) {
              it.remove()
              removed = true
            }
          }
        } else if (v.equals(existingVal))
          o.remove(k)
      })
    o
  }

  def overwriteField(o: ObjectNode, field: String, value: JsonNode): ObjectNode = {
    o.set(field, value)
    o
  }

  def removeField(o: ObjectNode, field: String): ObjectNode = {
    o.remove(field)
    o
  }

  def renameField(o: ObjectNode, from: String, to: String): ObjectNode = {
    val withUpdatedTo = Option(o).flatMap(_.getField(from)) match {
      case Some(v) => overwriteField(o, to, v)
      case None => removeField(o, to)
    }
    // cleaning up from field
    removeField(withUpdatedTo, from)
  }

  def flatten(a: ArrayNode): ArrayNode = {
    val flatArray = arrayNode
    a.toSeq
      .flatMap {
        case an: ArrayNode => an.toSeq
        case o => Seq(o)
      }
      .foreach(flatArray.add)

    flatArray
  }

}

object JsonMapper {

  type JsonMap = Map[String, AnyRef]
  val nullNode: NullNode = JsonNodeFactory.instance.nullNode
  val booleanYes: BooleanNode = JsonNodeFactory.instance.booleanNode(true)
  val booleanNo: BooleanNode = JsonNodeFactory.instance.booleanNode(false)

  def defaultObjectMapper(): ObjectMapper with ScalaObjectMapper = {
    val m = new ObjectMapper() with ScalaObjectMapper
    val customSerializers = new SimpleModule()
    customSerializers.addDeserializer(classOf[ObjectNode], new ObjectNodeDeserializer())

    m
      .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
      // sometimes JSON fields contain single object where accepting class
      // expects a list; this feature allows jackson to deserialize such fields
      .configure(ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
      .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
      // omits 'empty' (null or empty collections/objects) fields from output
      .setSerializationInclusion(NON_EMPTY)
      .registerModule(customSerializers)
      .registerModule(DefaultScalaModule)
    m
  }

  private val jacksonMapper = defaultObjectMapper()
  private val defaultMapper = JsonMapper(jacksonMapper)

  def apply(): JsonMapper = defaultMapper

  def toJson(obj: AnyRef): String = defaultMapper.toJson(obj)

  def prettyPrint(obj: AnyRef): String = defaultMapper.prettyPrint(obj)

  def fromJson[T: Manifest](json: String): T = defaultMapper.fromJson[T](json)

  def clone[T <: AnyRef](obj: T): T = defaultMapper.clone(obj)

  def convert[T: Manifest](source: AnyRef): T = defaultMapper.convert[T](source)

  def toJsonNode(obj: AnyRef): JsonNode = defaultMapper.toJsonNode(obj)

  def toJsonNodeFromVal(obj: AnyVal): JsonNode = defaultMapper.toJsonNodeFromVal(obj)

  def toObjectNode(obj: AnyRef): Option[ObjectNode] = defaultMapper.toObjectNode(obj)

  def objectNode: ObjectNode = defaultMapper.objectNode

  def objectNode(field: String, value: JsonNode): ObjectNode = defaultMapper.objectNode(field, value)

  def arrayNode: ArrayNode = defaultMapper.arrayNode

  def arrayNodeOf(e: AnyRef): ArrayNode = defaultMapper.arrayNodeOf(e)

  def arrayNodeOf(args: Seq[AnyRef]): ArrayNode = defaultMapper.arrayNodeOf(args)

  def arrayNodeOfVals(args: Seq[AnyVal]): ArrayNode = defaultMapper.arrayNodeOfVals(args)

  def textNode(t: String): TextNode = defaultMapper.textNode(t)

  def booleanNode(bool: Boolean): BooleanNode = defaultMapper.booleanNode(bool)

  def numericNode(i: Int): NumericNode = defaultMapper.numericNode(i)

  def numericNode(d: Double): NumericNode = defaultMapper.numericNode(d)

  def numericNode(bd: BigDecimal): NumericNode = defaultMapper.numericNode(bd)

  def patch[T <: AnyRef, P <: AnyRef](target: T, update: P): T = defaultMapper.patch(target, update)

  def patchMerge[T <: AnyRef, P <: AnyRef](target: T, update: P): T = defaultMapper.patchMerge(target, update)

  def patch(target: ObjectNode, update: ObjectNode): ObjectNode = defaultMapper.patch(target, update)

  def patchMerge(target: ObjectNode, update: ObjectNode): ObjectNode = defaultMapper.patchMerge(target, update)

  def patchFieldValue(target: ObjectNode, k: String, update: JsonNode): ObjectNode = defaultMapper.patchFieldValue(target, k, update)

  /**
    * Would merge values rather than overwriting or patching them
    * using the following approach:
    * <ul>
    * <li> Values existing on the same key will be combined into an array.
    * This is useful for configs which can repeat the same field several times
    * as a way of describing a collection of values, like 'discoveredBy'.
    * <li> Numbers will be added together.
    * </ul>
    *
    * @param target object to which update is applied
    * @param k      field to which update is applied
    * @param update field update
    * @return target object with field update merged to provided field
    */
  def mergeFieldValue(target: ObjectNode, k: String, update: JsonNode): ObjectNode =
    defaultMapper.mergeFieldValue(target, k, update)

  def removeFieldValue(o: ObjectNode, k: String, v: JsonNode): ObjectNode =
    defaultMapper.removeFieldValue(o, k, v)

  def overwriteField(o: ObjectNode, field: String, value: JsonNode): ObjectNode =
    defaultMapper.overwriteField(o, field, value)

  def removeField(o: ObjectNode, field: String): ObjectNode =
    defaultMapper.removeField(o, field)

  def renameField(o: ObjectNode, from: String, to: String): ObjectNode =
    defaultMapper.renameField(o, from, to)

  def flatten(a: ArrayNode): ArrayNode =
    defaultMapper.flatten(a)

  implicit class JsonNodeEx(n: JsonNode) {

    def asObject: Option[ObjectNode] = Option(n).cast[ObjectNode]

    def asArray: Option[ArrayNode] = Option(n).cast[ArrayNode]

    def asString: Option[String] = Option(n).filter(_.isTextual).map(_.asText())

    def getField(f: String): Option[JsonNode] = Option(n.get(f))

    def getObject(f: String): Option[ObjectNode] = getField(f).cast[ObjectNode]

    def getArray(f: String): Option[ArrayNode] = getField(f).cast[ArrayNode]

    def getString(f: String): Option[String] = getField(f).cast[TextNode].map(_.asText)

    def getInt(f: String): Option[Int] = getField(f).map(_.asInt)

    def getSeq(f: String): Seq[JsonNode] =
      if (!n.has(f)) Seq.empty
      else n.get(f) match {
        case a: ArrayNode => a.toSeq
        case n: JsonNode => Seq(n)
        case _ => Seq.empty
      }

    def getSeqOfObjects(f: String): Seq[ObjectNode] =
      getSeq(f).flatMap(_.asObject)

  }

  implicit class ArrayNodeEx(a: ArrayNode) {
    def toSeq: Seq[JsonNode] = a.elements().toSeq
    //noinspection AccessorLikeMethodIsEmptyParen
    def isEmpty(): Boolean = a.size() == 0
    def nonEmpty: Boolean = a.size() > 0
  }

  implicit class ObjectNodeEx(o: ObjectNode) {

    def setEx(field: String, value: JsonNode): ObjectNode = o.set(field, value).asInstanceOf[ObjectNode]

    def setEx(field: String, value: String): ObjectNode = setEx(field, textNode(value))

    def setEx(field: String, value: Boolean): ObjectNode = setEx(field, booleanNode(value))

    def setEx(field: String, value: Int): ObjectNode = setEx(field, numericNode(value))

    def setEx(field: String, value: Double): ObjectNode = setEx(field, numericNode(value))

    def setEx(field: String, value: BigDecimal): ObjectNode = setEx(field, numericNode(value))

    def setEx(field: String, a: Seq[JsonNode]): ObjectNode = setEx(field, arrayNodeOf(a))

    def setEx(e: Entry[String, JsonNode]): ObjectNode = setEx(e.getKey, e.getValue)

    def setEx(t: (String, JsonNode)): ObjectNode = setEx(t._1, t._2)

    def removeEx(field: String): ObjectNode = {
      o.remove(field)
      o
    }

    def fieldSeq(): Seq[String] = o.fieldNames().toSeq

    def entrySeq(): Seq[Entry[String, JsonNode]] = o.fields().toSeq

    def entries(): Seq[(String, JsonNode)] =
      o.entrySeq().map(e => (e.getKey, e.getValue))

    def values(): Seq[JsonNode] = entrySeq().map(_.getValue)

    def getNode(f: String): Option[JsonNode] = Option(o.get(f))

    def getObject(f: String): Option[ObjectNode] = Option(o.get(f)).cast[ObjectNode]

    def getArray(f: String): Option[ArrayNode] = Option(o.get(f)).cast[ArrayNode]

    def getNumber(f: String): Option[NumericNode] = Option(o.get(f)).filter(_.isNumber).cast[NumericNode]

    def getBoolean(f: String): Option[Boolean] = Option(o.get(f)).filter(_.isBoolean).map(_.booleanValue())

    def getString(f: String): Option[String] = Option(o.get(f)).cast[TextNode].map(_.asText)

    def getInt(f: String): Option[Int] = Option(o.get(f)).map(_.asInt)

    def getBigDecimal(f: String): Option[BigDecimal] =
      Option(o.get(f)).filter(_.isNumber).map(_.numberValue().doubleValue()).map(BigDecimal(_))

    //noinspection AccessorLikeMethodIsEmptyParen
    def isEmpty(): Boolean = !nonEmpty

    def nonEmpty: Boolean = o.fieldNames().hasNext

  }

}
