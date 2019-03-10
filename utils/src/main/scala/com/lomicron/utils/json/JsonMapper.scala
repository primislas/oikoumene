package com.lomicron.utils.json

import java.util.Map.Entry

import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY
import com.fasterxml.jackson.core.JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN
import com.fasterxml.jackson.core.`type`.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature.{ACCEPT_SINGLE_VALUE_AS_ARRAY, FAIL_ON_UNKNOWN_PROPERTIES}
import com.fasterxml.jackson.databind.node.{ArrayNode, JsonNodeFactory, ObjectNode}
import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper, PropertyNamingStrategy}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper

import collection.JavaConverters._
import scala.util.Try

object JsonMapper {

  private val mapper = new ObjectMapper() with ScalaObjectMapper
  mapper
    .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
    // sometimes JSON fields contain single object where accepting class
    // expects a list; this feature allows jackson to deserialize such fields
    .configure(ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
    .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
    // omits 'empty' (null or empty collections/objects) fields from output
    .setSerializationInclusion(NON_EMPTY)
    .registerModule(DefaultScalaModule)
  private val writer = defaultWriter(mapper)
  private val prettyPrinter = defaultWriter(mapper).withDefaultPrettyPrinter

  private def defaultWriter(mapper: ObjectMapper) =
    mapper.writer.`with`(WRITE_BIGDECIMAL_AS_PLAIN)

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
    mapper.convertValue(obj, new TypeReference[JsonNode] {})

  def toObjectNode(obj: AnyRef): Option[ObjectNode] =
    Try(toJsonNode(obj).asInstanceOf[ObjectNode]).toOption

  def objectNode: ObjectNode = new ObjectNode(JsonNodeFactory.instance)

  def arrayNode: ArrayNode = new ArrayNode(JsonNodeFactory.instance)

  def arrayNodeOf(e: AnyRef): ArrayNode =
    arrayNode.add(toJsonNode(e))

  def arrayNodeOf(args: Seq[AnyRef]): ArrayNode =
    args.map(JsonMapper.toJsonNode).foldLeft(arrayNode)(_.add(_))

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
    mergeFieldValue(target, e.getKey, e.getValue)

  def mergeFieldValue(target: ObjectNode, k: String, v: JsonNode): ObjectNode = {
    if (!target.has(k)) target.set(k, v)
    else {
      val existing = target.get(k)
      val existingArray =
        if (existing.isArray) existing.asInstanceOf[ArrayNode]
        else arrayNodeOf(existing)

      if (v.isArray) v.forEach(n => existingArray.add(n))
      else existingArray.add(v)

      target.set(k, existingArray)
    }
    target
  }

  def removeFieldValue(o: ObjectNode, k: String, v: JsonNode): ObjectNode = {
    Option(o.get(k))
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
    val withUpdatedTo = Option(o).map(_.get(from)) match {
      case Some(v) => overwriteField(o, to, v)
      case None => removeField(o, to)
    }
    // cleaning up from field
    removeField(withUpdatedTo, from)
  }

  def flatten(a: ArrayNode): ArrayNode = {
    val flatArray = arrayNode
    a.elements().asScala
      .flatMap {
        case o: ObjectNode => Seq(o)
        case an: ArrayNode => an.elements().asScala.to[Seq]
      }
      .foreach(flatArray.add)

    flatArray
  }

}
