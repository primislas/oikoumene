package com.lomicron.oikoumene.parsers

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node._
import com.lomicron.oikoumene.parsers.ClausewitzParser.Fields.tradeGoods
import com.lomicron.utils.collection.CollectionUtils._
import com.lomicron.utils.json.JsonMapper
import com.lomicron.utils.json.JsonMapper._
import com.lomicron.utils.parsing.JsonParser
import com.lomicron.utils.parsing.scopes.{ObjectScope, ParsingError}
import com.lomicron.utils.parsing.serialization.{DefaultDeserializer, Deserializer}
import com.lomicron.utils.parsing.tokenizer.{Date, Tokenizer}
import com.typesafe.scalalogging.LazyLogging

import scala.annotation.tailrec

object ClausewitzParser extends LazyLogging {

  type JsonEntry = java.util.Map.Entry[String, JsonNode]

  object Fields {
    val localisation = "localisation"
    val sourceFile = "source_file"
    val history = "history"
    val state = "state"
    val init = "init"
    val events = "events"
    val date = "date"
    val update = "update"
    val year = "year"
    val month = "month"
    val day = "day"
    val addPrefix = "add_"
    val removePrefix = "remove_"
    val color = "color"
    val tradeGoods = "trade_goods"
    val provinceIdsKey = "province_ids"
    val idKey = "id"
    val regionIdsKey = "region_ids"
    val terrainCategoriesKey = "categories"
    val terrainProvincesKey = "terrain_override"
    val terrainKey = "terrain"
    val modifier = "modifier"
    val potential = "potential"
    val allow = "allow"
    val aiWillDo = "ai_will_do"
  }

  val empty: (ObjectNode, Seq[ParsingError]) =
    (JsonParser.objectNode, Seq.empty)

  val startDate: Date = Date(1444, 11, 11)
  val endDate: Date = Date(Int.MaxValue, Int.MaxValue, Int.MaxValue)

  def parse(str: String): (ObjectNode, Seq[ParsingError]) =
    parse(str, DefaultDeserializer)

  def parse(str: String, deserializer: Deserializer): (ObjectNode, Seq[ParsingError]) =
    Option(str)
      .map(JsonParser.parse(_, deserializer))
      .filter(t => {
        val isObj = t._1.isInstanceOf[ObjectNode]
        if (!isObj) logger.warn(s"Expected an object node but encountered ${t._1}")
        isObj
      })
      .map(t => (t._1.asInstanceOf[ObjectNode], t._2))
      .getOrElse(empty)

  def parseAndLogErrors(str: String): ObjectNode =
    parse(str)
      .map(oes => {
        val (o, es) = oes
        es.foreach(e => logger.warn(s"Encountered a parsing error: ${e.message}"))
        o
      })
      .getOrElse(objectNode)

  def objToEmptyArray(n: JsonNode): JsonNode =
    if (n.isObject && n.isEmpty) arrayNode
    else n

  /**
    * Returns a seq of object fields. Optionally
    * sets field names as idKey in thos objects.
    *
    * @param o     source JSON object
    * @param idKey field names will be set to idKey fields
    *              of resulting objects, if idKey is provided
    * @return object fields
    */
  def fieldsToObjects(o: ObjectNode, idKey: Option[String] = None): Seq[ObjectNode] = {
    o.fieldNames().toSeq.flatMap(id => o.getObject(id).map(v => idKey.map(v.setEx(_, id)).getOrElse(v)))
  }

  /**
    * Returns a seq of object fields. Optionally
    * sets field names as idKey in thos objects.
    *
    * @param o     source JSON object
    * @param idKey field names will be set to idKey fields
    *              of resulting objects, if idKey is provided
    * @return object fields
    */
  def fieldsToObjects(o: ObjectNode, idKey: String): Seq[ObjectNode] =
    fieldsToObjects(o, Option(idKey))

  def parseEvents(obj: ObjectNode): Seq[ObjectNode] = {
    val events = getEvents(obj)
      .map(dm => {
        val (date, event) = dm
        obj.remove(date.lexeme)
        event.setEx(Fields.date, date.lexeme)

        // cleaning up
        if (event.has(tradeGoods)) {
          // it is a bug, shouldn't be reported twice
          event.getArray(tradeGoods).foreach(a => {
            val actualGood = a.get(a.size() - 1)
            event.setEx(tradeGoods, actualGood)
          })
        }

        event
      })

    events
  }

  def parseHistory(obj: ObjectNode): ObjectNode = {
    val events = parseEvents(obj)
    objectNode(Fields.init, obj)
      .setEx(Fields.events, events)
  }

  def parseHistory(obj: ObjectNode, sourceFile: String): ObjectNode =
    parseHistory(obj).setEx(Fields.sourceFile, sourceFile)

  def filesWithPrependedNames(filesByName: Map[String, String]): Map[String, String] =
    filesByName
      .mapValuesEx(_.split("\n"))
      .mapKVtoValue((name, lines) => s"${Fields.sourceFile} = $name" +: lines)
      .mapValuesEx(_.mkString("\n"))

  def mapEntityFilesToFileNames(filesByName: Map[String, String]): Map[String, ObjectNode] =
    parseFilesByFileNames(filesByName, o => Seq(o))
      .filterValues(_.nonEmpty)
      .mapValues(_.head)

  def parseFilesAsEntities(filesByName: Map[String, String]): Seq[ObjectNode] =
    parseFiles(filesByName, o => Seq(o))

  def parseFileFieldsAsEntities(filesByName: Map[String, String]): Seq[ObjectNode] =
    parseFiles(filesByName, parseValuesAsEntities)

  def fieldsToArrays(o: ObjectNode, fs: Seq[String]): ObjectNode = {
    fs.foreach(f => {
      val a = o.get(f) match {
        case an: ArrayNode => an
        case on: ObjectNode => on.getArray(ObjectScope.arrayKey).getOrElse(arrayNodeOf(on))
        case _: NullNode => arrayNode
        case jn: JsonNode => arrayNodeOf(jn)
        case _ => nullNode
      }
      if (a != nullNode) o.setEx(f, a)
      else o
    })
    o
  }

  private def parseFiles
  (filesByName: Map[String, String],
   fileParser: ObjectNode => Seq[ObjectNode])
  : Seq[ObjectNode] = parseFilesByFileNames(filesByName, fileParser).values.toList.flatten

  private def parseFilesByFileNames
  (filesByName: Map[String, String],
   fileParser: ObjectNode => Seq[ObjectNode])
  : Map[String, Seq[ObjectNode]] =
    filesByName
      .mapValues(parse)
      .mapKVtoValue((filename, o) => {
        if (o._2.nonEmpty) logger.warn(s"Encountered ${o._2.size} errors while parsing $filename: ${o._2}")
        fileParser(o._1)
      })
      .mapKVtoValue((filename, entities) => entities.map(_.setEx(Fields.sourceFile, filename)))

  /**
    * Treats root object fields as ids of entities configure
    * at those fields. Returns those entities with root fields
    * set as 'id' field.
    *
    * @param obj object where separate fields are actually separate entities
    * @return
    */
  def parseValuesAsEntities(obj: ObjectNode): Seq[ObjectNode] =
    parseValuesAsEntities(obj, Some("id"))

  def parseValuesAsEntities(objectNode: ObjectNode, setFieldAs: Option[String]): Seq[ObjectNode] = {
    objectNode.fields().toSeq.filter(_.getValue.isObject).map(e => {
      val (field, obj) = (e.getKey, e.getValue.asInstanceOf[ObjectNode])
      setFieldAs.foreach(obj.setEx(_, field))
      obj
    })
  }

  def setIndex(jsons: Seq[ObjectNode]): Seq[ObjectNode] =
    jsons.zipWithIndex.map(oi => oi._1.setEx("index", oi._2))

  def rollUpEvents(obj: ObjectNode): ObjectNode =
    rollUpEvents(obj, endDate)

  def rollUpEvents(obj: ObjectNode, endDate: Date): ObjectNode = {
    val events = parseEvents(obj)
    val state = events
      .flatMap(Option(_).cast[ObjectNode])
      .filter(e => !e.has(Fields.date) || e.getObject(Fields.date).map(json2date).exists(_.compare(endDate) <= 0))
      .flatMap(_.getObject(Fields.update))
      .foldLeft(objectNode)(mergeFields)

    objectNode
      .setEx(Fields.state, state)
      .setEx(Fields.events, JsonMapper.arrayNodeOf(events))
  }

  def getEvents(history: ObjectNode): Seq[(Date, ObjectNode)] = history.fields.toSeq
    .flatMap(e => strToDate(e.getKey).map(date => (date, e.getValue)))
    .flatMap(de => de._2 match {
      case node: ObjectNode => Some(de._1, node)
      case a: ArrayNode => a.toSeq.flatMap(_.asObject).map((de._1, _))
      case _ => None
    })

  def date2json(date: Date): ObjectNode =
    JsonParser
      .objectNode
      .setEx(Fields.year, IntNode.valueOf(date.year))
      .setEx(Fields.month, IntNode.valueOf(date.month))
      .setEx(Fields.day, IntNode.valueOf(date.day))

  def json2date(jsonDate: ObjectNode): Date = Date(
    jsonDate.get(Fields.year).asInt,
    jsonDate.get(Fields.month).asInt,
    jsonDate.get(Fields.day).asInt)

  /**
    * Replaces Clausewitz color definition -
    * which is normally an array of 3 int values (RGB),
    * wit Oikoumene color representation.
    *
    * @param o incoming object that may contain a Clausewitz color field
    * @return object with the color field in Oikoumene format
    */
  def parseColor(o: ObjectNode): ObjectNode = parseColor(o, Fields.color)

  def parseColor(o: ObjectNode, colorField: String): ObjectNode = {
    o.getArray(colorField).filter(_.size == 3).foreach(a => {
      val c = objectNode
        .setEx("r", a.get(0))
        .setEx("g", a.get(1))
        .setEx("b", a.get(2))

      o.setEx(colorField, c)
    })

    o
  }

  def decimalColorToInt(a: ArrayNode): ArrayNode = {
    val ints = a.toSeq.map(_.asDouble(0) * 255).map(_.toInt)
    arrayNodeOfVals(ints)
  }

  def parseDates(o: ObjectNode): ObjectNode = {
    o.fields().toSeq.filter(_.getKey.endsWith("date"))
      .filter(_.getValue.isTextual)
      .flatMap(e => strToDateNode(e.getValue.asText()).map(date => (e.getKey, date)))
      .foreach { case (k, date) => o.setEx(k, date) }
    o
  }

  def strToDate(key: String): Option[Date] = key match {
    case Tokenizer.datePat(year, month, day) =>
      Option(Date(year, month, day))
    case _ => Option.empty
  }

  def strToDateNode(key: String): Option[ObjectNode] =
    strToDate(key).map(date2json)

  def isDate(n: JsonNode): Boolean = n match {
    case t: TextNode => isDate(t.asText)
    case o: ObjectNode =>
      (o.has(Fields.year) && o.get(Fields.year).isInt
        && o.has(Fields.month) && o.get(Fields.month).isInt
        && o.has(Fields.day) && o.get(Fields.day).isInt)
    case _ => false
  }

  def isDate(s: String): Boolean = s.matches(Tokenizer.datePat.pattern.pattern())

  def isColor(n: JsonNode): Boolean = n match {
    case a: ArrayNode => a.size() == 3 && a.toSeq.forall(_.isInt)
    case o: ObjectNode =>
      (o.has("r") && o.get("r").isInt
        && o.has("g") && o.get("g").isInt
        && o.has("b") && o.get("b").isInt)
    case _ => false
  }

  def mergeFields(target: ObjectNode, update: ObjectNode): ObjectNode = {
    update.fields.toSeq.foreach(e => mergeField(target, e))
    target
  }

  def mergeField(target: ObjectNode, kv: JsonEntry): ObjectNode =
    mergeField(target, kv.getKey, kv.getValue)

  def mergeField(target: ObjectNode, key: String, value: JsonNode): ObjectNode = {
    if (key.startsWith(Fields.addPrefix)) {
      target.remove(key)
      val field = fieldNameWithoutPrefix(key, Fields.addPrefix)
      JsonMapper.mergeFieldValue(target, field, value)
    } else if (key.startsWith(Fields.removePrefix)) {
      target.remove(key)
      val field = fieldNameWithoutPrefix(key, Fields.removePrefix)
      if (value.isNumber)
        println(s"Removing a number value: $key=${value.asText}")
      JsonMapper.removeFieldValue(target, field, value)
    } else
      target.setEx(key, value)
  }

  def fieldNameWithoutPrefix(field: String, prefix: String): String = {
    val cs = field.toSeq.drop(prefix.length)
    cs match {
      case h +: tail => s"${h.toLower}${tail.mkString}"
      case _ => cs.mkString
    }
  }

  def pluralFieldNameFromPrefix(field: String, prefix: String): String =
    s"${fieldNameWithoutPrefix(field, prefix)}s"

  def parseNestedConditions(triggers: Seq[ObjectNode]): Seq[ObjectNode] = {

    @tailrec def rec(unchecked: Seq[ObjectNode], checked: Seq[ObjectNode]): Seq[ObjectNode] = {
      val nots = unchecked.flatMap(_.getSeqOfObjects("NOT"))
      val ors = unchecked.flatMap(_.getSeqOfObjects("OR"))
      val ands = unchecked.flatMap(_.getSeqOfObjects("AND"))
      val froms = unchecked.flatMap(_.getObject("FROM"))
      val modifiers = unchecked.flatMap(_.getSeqOfObjects("modifier"))
      val owners = unchecked.flatMap(_.getObject("owner"))
      val controllers = unchecked.flatMap(_.getObject("controller"))
      val nestedTriggers = ors ++ ands ++ nots ++ froms ++ modifiers ++ owners ++ controllers

      if (nestedTriggers.isEmpty) checked ++ unchecked
      else rec(nestedTriggers, checked ++ unchecked)
    }

    rec(triggers, Seq.empty)
  }

  def removeEmptyObjects(o: ObjectNode): ObjectNode = {
    o.entrySeq()
      .filter(e => e.getValue.isObject && e.getValue.isEmpty)
      .foreach(e => o.remove(e.getKey))
    o
  }

  def selectLastDefinitionFromArray(n: JsonNode): JsonNode =
    n match {
      case a: ArrayNode => a.toSeq.last
      case _ => n
    }

}
