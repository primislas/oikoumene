package com.lomicron.oikoumene.engine

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.utils.json.JsonMapper
import com.lomicron.utils.parsing.{Date, JsonParser, ParsingError, Tokenizer}

import scala.collection.mutable

object ClausewitzParser {

  type JsonEntry = java.util.Map.Entry[String, JsonNode]

  val eventsField = "events"
  val dateField = "date"
  val yearField = "year"
  val monthField = "month"
  val dayField = "day"
  val addPrefix = "add_"
  val removePrefix = "remove_"

  val empty: (ObjectNode, Seq[ParsingError]) =
    (JsonParser.objectNode, Seq.empty)

  //  def dateToObjectNode(d: Date): ObjectNode =
  //    JsonParser.objectNode
  //      .put(yearField, d.year)
  //      .put(monthField, d.month)
  //      .put(dayField, d.day)

  def parse(str: String): (ObjectNode, Seq[ParsingError]) =
    Option(str).map(JsonParser.parse).getOrElse(empty)

  def rollUpEvents(obj: ObjectNode, endDate: Date): ObjectNode = {
    val rolledUp = JsonParser.objectNode
    val it = obj.fields
    val eventsByDate: mutable.Map[Date, ObjectNode] = mutable.TreeMap[Date, ObjectNode]()

    while (it.hasNext) {
      val e = it.next
      val k = e.getKey
      val v = e.getValue

      strToDate(k)
        .filter(_.compare(endDate) <= 0)
        .filter(_ => v.isInstanceOf[ObjectNode])
        .map(date => {
          eventsByDate += (date -> v.asInstanceOf[ObjectNode])
          rolledUp
        })
        .getOrElse(mergeField(rolledUp, k, v))
    }

    eventsByDate.foldLeft(rolledUp)((acc, kv) => {
      val (date, update) = kv
      val event = JsonParser.objectNode
      event.set(dateField, date2json(date))
      update.fields.forEachRemaining(e => event.set(e.getKey, e.getValue))
      JsonMapper.mergeFieldValue(acc, eventsField, event)
    })
  }

  def date2json(date: Date): ObjectNode =
    JsonParser
      .objectNode
      .put(yearField, date.year)
      .put(monthField, date.month)
      .put(dayField, date.day)

  def strToDate(key: String): Option[Date] = key match {
    case Tokenizer.datePat(year, month, day) =>
      Option(Date(year, month, day))
    case _ => Option.empty
  }

  def mergeFields(source: ObjectNode, target: ObjectNode): ObjectNode = {
    source.fields().forEachRemaining(e => mergeField(target, e))
    target
  }

  def mergeField(target: ObjectNode, kv: JsonEntry): ObjectNode =
    mergeField(target, kv.getKey, kv.getValue)

  def mergeField(target: ObjectNode, key: String, value: JsonNode): ObjectNode = {
    if (key.startsWith(addPrefix)) {
      val field = fieldWithoutPrefix(key, addPrefix)
      JsonMapper.mergeFieldValue(target, field, value)
    } else if (key.startsWith(removePrefix)) {
      val field = fieldWithoutPrefix(key, removePrefix)
      JsonMapper.removeFieldValue(target, field, value)
    } else
      target.set(key, value).asInstanceOf[ObjectNode]
  }

  def fieldWithoutPrefix(field: String, prefix: String) =
    s"${field.drop(prefix.length)}s"

}
