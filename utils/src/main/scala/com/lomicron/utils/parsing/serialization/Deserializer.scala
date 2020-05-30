package com.lomicron.utils.parsing.serialization

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.{ArrayNode, ObjectNode}
import com.lomicron.utils.io.IO
import com.lomicron.utils.json.JsonMapper
import com.lomicron.utils.parsing.JsonParser.rootKey
import com.lomicron.utils.parsing.scopes.ObjectScope.arrayKey
import com.lomicron.utils.parsing.scopes.ParsingError
import com.typesafe.scalalogging.LazyLogging

import scala.collection.JavaConverters._

trait Deserializer {
  def run(obj: ObjectNode): (JsonNode, Seq[ParsingError])
}

object DefaultDeserializer extends BaseDeserializer {
  override def run(obj: ObjectNode): (JsonNode, Seq[ParsingError]) = {
    val (deserializedObj, _, deserializationErrors) = rec(obj, List(rootKey), Seq.empty)
    (deserializedObj, deserializationErrors)
  }

  override def rec(o: ObjectNode, path: List[String], errors: Seq[ParsingError]): (JsonNode, List[String], Seq[ParsingError]) = {
    val array = Option(o.get(arrayKey))

    // legit array - all elements have been added as array elements
    // and no other fields exist
    if (array.isDefined) {
      if (o.fieldNames().asScala.length == 1) return getArray(o, path, errors)
      else {
        // A conflicting situation where an array was mixed with an object.
        // More often than not it is a result of an error. Thus, attempting
        // to address it by a best guess of a type based on the amount of elements.
        val errPath = path.mkString(" -> ")
        val err = ParsingError(s"A conflicting array-object definition is encountered at path $errPath: ${o.asText()}")
        val fieldCount = o.fieldNames().asScala.length - 1 // minus elements field
        val isArray = Option(o.get(arrayKey))
          .filter(_.isInstanceOf[ArrayNode])
          .map(_.asInstanceOf[ArrayNode])
          .map(_.iterator().asScala.length)
          .exists(_ > fieldCount)

        if (isArray) return getArray(o, path, errors :+ err)
        else {
          o.remove(arrayKey)
          return (o, path, errors :+ err)
        }

      }
    }

    deserializeObjectValues(o, path, errors)
  }

}

class BaseDeserializer(settings: SerializationSettings = DefaultSettings.settings)
  extends Deserializer
    with LazyLogging {

  override def run(obj: ObjectNode): (JsonNode, Seq[ParsingError]) = {
    val (deserializedObj, _, deserializationErrors) = rec(obj, List(rootKey), Seq.empty)
    (deserializedObj, deserializationErrors)
  }

  protected def rec(o: ObjectNode,
                    path: List[String],
                    errors: Seq[ParsingError] = Seq.empty)
  : (JsonNode, List[String], Seq[ParsingError]) = {

    val array = Option(o.get(arrayKey))

    // legit array - all elements have been added as array elements
    // and no other fields exist
    if (array.isDefined && o.fieldNames().asScala.length == 1)
      return getArray(o, path, errors)

    deserializeObjectValues(o, path, errors)
  }

  protected def deserializeObjectValues(o: ObjectNode,
                                        path: List[String],
                                        errors: Seq[ParsingError] = Seq.empty)
  : (JsonNode, List[String], Seq[ParsingError]) = {

    val objFieldsToDeserialize = o.fields().asScala.to[Seq]
      .filter(arrayKey != _.getKey)
      .filter(_.getValue.isInstanceOf[ObjectNode])
      .map(e => DeserializedObject(e.getKey, e.getValue.asInstanceOf[ObjectNode], path :+ e.getKey))

    val arrObjsToDeserialize = o.fields().asScala.to[Seq]
      .filter(_.getValue.isInstanceOf[ArrayNode])
      .flatMap(e => {
        val key = e.getKey
        val arr = e.getValue.asInstanceOf[ArrayNode]

        (0 until arr.size)
          .filter(arr.get(_).isInstanceOf[ObjectNode])
          .map(id => DeserializedObject(key, arr.get(id).asInstanceOf[ObjectNode], path :+ s"$key[$id]", Some(id)))
      })

    val deserializedArrays = arrObjsToDeserialize.map(_.field).distinct

    val objsToDeserialize = objFieldsToDeserialize ++ arrObjsToDeserialize

    if (objsToDeserialize.isEmpty) (o, path, errors)
    else {
      var recErrs = Seq.empty[ParsingError]

      objsToDeserialize
        .foreach(oConf => rec(oConf.obj, oConf.path) match {
          case (ro, _, errs) =>
            oConf.index match {
              case Some(id) => o.get(oConf.field).asInstanceOf[ArrayNode].set(id, ro)
              // Empty objects present an ambiguity, as in Clausewitz terms
              // such declaration could mean an empty array. Unfortunately,
              // there's no easy way to single out such arrays, so we have to
              // rely on settings to identify them.
              case _ =>
                if (!(ro.isInstanceOf[ObjectNode] && !ro.fieldNames().hasNext)) o.set(oConf.field, ro)
                else {
                  settings.fields
                    .find(_.matches(oConf.field))
                    .filter(_.isArray)
                    .map(_ => o.set(oConf.field, JsonMapper.arrayNode))
                    .getOrElse(o.set(oConf.field, ro))
                }
            }
            if (errs.nonEmpty) recErrs = recErrs ++ errs
        })

      deserializedArrays.foreach(field => {
        val flatArray = JsonMapper.flatten(o.get(field).asInstanceOf[ArrayNode])
        o.set(field, flatArray)
      })

      (o, path, errors ++ recErrs)
    }
  }

  protected def getArray(o: ObjectNode, path: List[String], errors: Seq[ParsingError] = Seq.empty): (JsonNode, List[String], Seq[ParsingError]) = {
    val node = o.get(arrayKey)
    if (node.isInstanceOf[ArrayNode]) (node, path, errors)
    else {
      val errPath = (path + arrayKey).mkString(" -> ")
      val err = ParsingError(s"Expected an array node at path '$errPath' but instead got ${node.asText()}")
      (JsonMapper.arrayNode, path, errors :+ err)
    }

  }
}

object BaseDeserializer extends BaseDeserializer(SerializationSettings.empty)

case class DeserializedObject
(
  field: String,
  obj: ObjectNode,
  path: List[String] = List.empty,
  index: Option[Int] = None
)

object DefaultSettings {
  val settingsJson: String =
    IO.readTextResource("com/lomicron/utils/parsing/serialization/serialization_settings.json")
  val settings: SerializationSettings =
    JsonMapper.fromJson[SerializationSettings](settingsJson)
}
