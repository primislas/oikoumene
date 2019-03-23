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
    val tradeGoods = "trade_goods"
  }

  val empty: (ObjectNode, Seq[ParsingError]) =
    (JsonParser.objectNode, Seq.empty)

  private val startDate = Date(1444, 11, 11)
  private val endDate = Date(Int.MaxValue, Int.MaxValue, Int.MaxValue)

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

    val origin = JsonMapper.objectNode(Fields.update, obj)
    origin +: events
  }

  def rollUpEvents(obj: ObjectNode): ObjectNode =
    rollUpEvents(obj, endDate)

  def rollUpEvents(obj: ObjectNode, endDate: Date): ObjectNode = {
    val events = parseEvents(obj)
    val state = events
      .flatMap(Option(_).cast[ObjectNode])
      .filter(e => !e.has(Fields.date) || e.getObject(Fields.date).map(json2date).exists(_.compare(endDate) <= 0))
      .flatMap(_.getObject(Fields.update))
      .foldLeft(JsonMapper.objectNode)(mergeFields)

    val history = JsonMapper.objectNode
    history.set(Fields.state, state)
    history.set(Fields.events, JsonMapper.arrayNodeOf(events))
    history
  }

  def getEvents(history: ObjectNode): Seq[(Date, ObjectNode)] = history.fields.toSeq
    .flatMap(e => strToDate(e.getKey).map(date => (date, e.getValue)))
    .filter(de => de._2.isInstanceOf[ObjectNode])
    .map(de => (de._1, de._2.asInstanceOf[ObjectNode]))

  def date2json(date: Date): ObjectNode =
    JsonParser
      .objectNode
      .put(Fields.year, date.year)
      .put(Fields.month, date.month)
      .put(Fields.day, date.day)

  def json2date(jsonDate: ObjectNode) = Date(
    jsonDate.get(Fields.year).asInt,
    jsonDate.get(Fields.month).asInt,
    jsonDate.get(Fields.day).asInt)

  def strToDate(key: String): Option[Date] = key match {
    case Tokenizer.datePat(year, month, day) =>
      Option(Date(year, month, day))
    case _ => Option.empty
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