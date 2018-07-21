package com.lomicron.oikoumene.parsers

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.utils.json.JsonMapper
import com.lomicron.utils.parsing.JsonParser
import com.lomicron.utils.parsing.scopes.ParsingError
import com.lomicron.utils.parsing.tokenizer.{Date, Tokenizer}

import scala.collection.mutable

object ClausewitzParser {

  type JsonEntry = java.util.Map.Entry[String, JsonNode]

  val historyField = "events"
  val dateField = "date"
  val yearField = "year"
  val monthField = "month"
  val dayField = "day"
  val addPrefix = "add"
  val removePrefix = "remove"

  val empty: (ObjectNode, Seq[ParsingError]) =
    (JsonParser.objectNode, Seq.empty)

  private val endDate = Date(Int.MaxValue, Int.MaxValue, Int.MaxValue)

  def parse(str: String): (ObjectNode, Seq[ParsingError]) =
    Option(str).map(JsonParser.parse).getOrElse(empty)

  def rollUpEvents(obj: ObjectNode): ObjectNode =
    rollUpEvents(obj, endDate)

  def rollUpEvents(obj: ObjectNode, endDate: Date): ObjectNode = {
    val rolledUp = JsonParser.objectNode
    val eventsByDate: mutable.Map[Date, ObjectNode] = mutable.TreeMap[Date, ObjectNode]()

    obj.fields.forEachRemaining(kv => {
      val (k, v) = (kv.getKey, kv.getValue)

      strToDate(k)
        .map(date => {
          Option(date)
            .filter(_.compare(endDate) <= 0)
            .filter(_ => v.isInstanceOf[ObjectNode])
            .foreach(validDate => {
              val events = v.asInstanceOf[ObjectNode]
              eventsByDate += (validDate -> events)
              events.fields().forEachRemaining(kv => mergeField(rolledUp, kv))
            })
          rolledUp
        })
        .getOrElse(mergeField(rolledUp, k, v))
    })

    eventsByDate.foldLeft(rolledUp)((acc, kv) => {
      val (date, update) = kv
      val event = JsonParser.objectNode
      event.set(dateField, date2json(date))
      update.fields.forEachRemaining(e => event.set(e.getKey, e.getValue))
      JsonMapper.mergeFieldValue(acc, historyField, event)
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

  def fieldWithoutPrefix(field: String, prefix: String): String = {
    val cs = field.toSeq.drop(prefix.length)
    cs match {
      case h+:tail => s"${h.toLower}${tail.mkString}s"
      case _ => cs.mkString
    }
  }

}
