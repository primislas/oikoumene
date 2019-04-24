package com.lomicron.oikoumene.parsers

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node._
import com.lomicron.oikoumene.parsers.ClausewitzParser.Fields.tradeGoods
import com.lomicron.utils.collection.CollectionUtils._
import com.lomicron.utils.json.JsonMapper
import com.lomicron.utils.json.JsonMapper._
import com.lomicron.utils.parsing.JsonParser
import com.lomicron.utils.parsing.scopes.ParsingError
import com.lomicron.utils.parsing.serialization.{DefaultDeserializer, Deserializer}
import com.lomicron.utils.parsing.tokenizer.{Date, Tokenizer}

object ClausewitzParser {

  type JsonEntry = java.util.Map.Entry[String, JsonNode]

  object Fields {
    val history = "history"
    val state = "state"
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
  }

  val empty: (ObjectNode, Seq[ParsingError]) =
    (JsonParser.objectNode, Seq.empty)

  val startDate = Date(1444, 11, 11)
  val endDate = Date(Int.MaxValue, Int.MaxValue, Int.MaxValue)

  def parse(str: String): (ObjectNode, Seq[ParsingError]) =
    parse(str, DefaultDeserializer)

  def parse(str: String, deserializer: Deserializer): (ObjectNode, Seq[ParsingError]) = {
    Option(str)
      .map(JsonParser.parse(_, deserializer))
      // TODO add at least logging to highlight the case
      // where returned value is not an object node
      .filter(_._1.isInstanceOf[ObjectNode])
      .map(t => (t._1.asInstanceOf[ObjectNode], t._2))
      .getOrElse(empty)
  }

  def parseEvents(obj: ObjectNode): Seq[ObjectNode] = {
    val events = getEvents(obj)
      .map(dm => {
        val (date, event) = dm
        obj.remove(date.lexeme)
        event.set(Fields.date, date2json(date))

        // cleaning up
        if (event.has(tradeGoods)) {
          // it is a bug, shouldn't be reported twice
          event.getArray(tradeGoods).foreach(a => {
            val actualGood = a.get(a.size() - 1)
            event.set(tradeGoods, actualGood)
          })
        }

        event
      })

    events
  }

  def rollUpEvents(obj: ObjectNode): ObjectNode =
    rollUpEvents(obj, endDate)

  def rollUpEvents(obj: ObjectNode, endDate: Date): ObjectNode = {
    val events = parseEvents(obj)
    val state = events
      .flatMap(Option(_).cast[ObjectNode])
      .filter(e => !e.has(Fields.date) || e.getObject(Fields.date).map(json2date).exists(_.compare(endDate) <= 0))
      .flatMap(_.getObject(Fields.update))
      .foldLeft(objectNode)(mergeFields)

    val history = objectNode
    history.set(Fields.state, state)
    history.set(Fields.events, JsonMapper.arrayNodeOf(events))
    history
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

  def json2date(jsonDate: ObjectNode) = Date(
    jsonDate.get(Fields.year).asInt,
    jsonDate.get(Fields.month).asInt,
    jsonDate.get(Fields.day).asInt)

  /**
    * Replaces Clausewitz color definition -
    * which is normally an array of 3 int values (RGB),
    * wit Oikoumene color representation.
    *
    * @param o - incoming object that may contain a Clausewitz color field
    * @return object with the color field in Oikoumene format
    */
  def parseColor(o: ObjectNode): ObjectNode = parseColor(o, Fields.color)

  def parseColor(o: ObjectNode, colorField: String): ObjectNode = {
    o.getArray(colorField).filter(_.size == 3).foreach(a => {
      val c = objectNode
      c.set("r", a.get(0))
      c.set("g", a.get(1))
      c.set("b", a.get(2))

      o.set(colorField, c)
    })

    o
  }

  def parseDates(o: ObjectNode): ObjectNode = {
    o.fields().toSeq.filter(_.getKey.endsWith("date"))
      .filter(_.getValue.isTextual)
      .flatMap(e => strToDateNode(e.getValue.asText()).map(date => (e.getKey, date)))
      .foreach{ case (k, date) => o.setEx(k, date) }
    o
  }

  def strToDate(key: String): Option[Date] = key match {
    case Tokenizer.datePat(year, month, day) =>
      Option(Date(year, month, day))
    case _ => Option.empty
  }

  def strToDateNode(key: String): Option[ObjectNode] =
    strToDate(key).map(date2json)

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
      target.set(key, value).asInstanceOf[ObjectNode]
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

}