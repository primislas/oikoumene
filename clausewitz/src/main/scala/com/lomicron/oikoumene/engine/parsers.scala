package com.lomicron.oikoumene.engine

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.utils.parsing.{Date, JsonParser, ParsingError, Tokenizer}

import scala.collection.mutable

object ClausewitzParser {
  val eventsField = "events"
  val dateField = "date"
  val yearField = "year"
  val monthField = "month"
  val dayField = "day"
  val addPrefix = "add_"
  val removePrefix = "remove_"

  val empty: (ObjectNode, Seq[ParsingError]) =
    (JsonParser.objectNode, Seq.empty)

  def buildDate(d: Date): ObjectNode =
    JsonParser.objectNode
      .put(yearField, d.year)
      .put(monthField, d.month)
      .put(dayField, d.day)

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

      toDate(k)
        .filter(_.compare(endDate) <= 0)
        .filter(_ => v.isInstanceOf[ObjectNode])
        .map(date => {
          eventsByDate += (date -> v.asInstanceOf[ObjectNode])
          rolledUp
        })
        .getOrElse(addField(rolledUp, k, v))
    }

    eventsByDate.foldLeft(rolledUp)((acc,kv) => mergeFields(kv._2, acc))
  }

  def toDate(key: String): Option[Date] = key match {
    case Tokenizer.datePat(year, month, day) =>
      Option(Date(year.toInt, month.toInt, day.toInt))
    case _ => Option.empty
  }

  private def mergeFields(source: ObjectNode, target: ObjectNode): ObjectNode = {
    source.fields().forEachRemaining(e => {
      val k = e.getKey
      val v = e.getValue
      addField(target, k, v)
    })
    target
  }

  private def addField(target: ObjectNode, key: String, value: JsonNode) = {
    val validatedKey = preprocessKey(key)
    JsonParser.addField(target, validatedKey, value)
  }

  private def preprocessKey(key: String) =
    if (key.startsWith(addPrefix)) key.drop(addPrefix.length) + "s"
    else if (key.startsWith(removePrefix)) key.drop(removePrefix.length) + "s"
    else key

}
