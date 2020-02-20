package com.lomicron.oikoumene.serializers

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.{ArrayNode, BooleanNode, ObjectNode}
import com.lomicron.oikoumene.model.history.{HistEvent, HistState, History}
import com.lomicron.oikoumene.parsers.ClausewitzParser.Fields
import com.lomicron.utils.json.JsonMapper._

object ClausewitzSerializer {

  def serialize[T <: AnyRef](entity: T, options: Set[SerializationOption] = Set.empty): String =
    Option(entity)
      .flatMap(toObjectNode)
      .map(recSerialize(_, 0, options))
      .map(_.mkString("\n"))
      .getOrElse("")

  def serializeHistory[H <: History[H, S, E], S <: HistState[S, E], E <: HistEvent](a: History[H, S, E]): String = {
    val init = Option(a.init).flatMap(toObjectNode).getOrElse(objectNode)
    val events = a.events
      .flatMap(e => for {
        json <- toObjectNode(e)
        date <- e.date
      } yield (date, json.removeEx(Fields.date)))
      .foldLeft(objectNode)((acc, t) => acc.setEx(t._1.toString, t._2))

    val initState = recSerialize(init, 0, Set(ArraysToIndividualRecords))
    val history = recSerialize(events, 0, Set(ArraysToIndividualRecords))
    val lines = initState ++ history
    lines.mkString("\n")
  }

  def recSerialize(node: ObjectNode, depth: Int = 0, options: Set[SerializationOption] = Set.empty): Seq[String] = {
    val offset = "\t" * depth

    node.entries().flatMap(e => {
      val (k, v) = e
      v match {
        case o: ObjectNode =>
          val serializedObj = recSerialize(o, depth + 1)
          wrapAsObject(k, serializedObj, depth)
        case a: ArrayNode =>
          if (options.contains(ArraysToIndividualRecords)) {
            a.toSeq.map(objectNode(k, _)).flatMap(recSerialize(_, depth, options))
          } else {
            val serializedArray = serializeArray(a, depth + 1)
            wrapAsObject(k, serializedArray, depth)
          }
        case _ =>
          nodeToText(v)
            .filter(_.nonEmpty)
            .map(s => s"$k = $s")
            .map(strWithOffset(_, offset))
            .toSeq
      }
    })
  }

  def serializeArray(a: ArrayNode, depth: Int = 0): Seq[String] = {
    val aSeq = a.toSeq
    val isArrayOfPrimitives = !aSeq.exists(_.isObject)
    val elements = if (isArrayOfPrimitives) {
      val serializedArray = aSeq
        .map(_.asText)
        .map(wrapInQuotesIfWithWhitespace)
        .filter(_.nonEmpty)
        .mkString(" ")
      Seq(serializedArray)
    } else {
      aSeq.flatMap(nodeToText)
    }

    val offset = strOffset(depth)
    elements.map(strWithOffset(_, offset))
  }

  def nodeToText(node: JsonNode): Option[String] = node match {
    case o: ObjectNode =>
      val objStr = o.entries()
        .flatMap(e => {
          val (k, v) = e
          val strVal = nodeToText(v).mkString(" ")
          if (strVal.nonEmpty) Some(s"$k = $strVal")
          else None
        })
        .mkString(" ")
      Option(objStr).filter(_.nonEmpty).map(s => s"{ $s }")
    case a: ArrayNode => Option(serializeArray(a).mkString(" ")).filter(_.nonEmpty)
    case b: BooleanNode => if (b.booleanValue()) Some("yes") else Some("no")
    case _ =>
      val str = wrapInQuotesIfWithWhitespace(node.asText)
      Option(str).filter(_.nonEmpty)
  }

  def wrapAsObject(key: String, lines: Seq[String], depth: Int = 0): Seq[String] = {
    val offset = strOffset(depth)
    val objStart = s"$offset$key = {"
    val objEnd = s"$offset}"
    objStart +: lines :+ objEnd
  }

  def wrapInQuotesIfWithWhitespace(str: String): String = {
    val hasWhitespaces = str.exists(c => c == ' ' || c == '\t')
    if (hasWhitespaces) s""""$str""""
    else str
  }

  def strOffset(depth: Int): String = "\t" * depth

  def strWithOffset(s: String, depth: Int = 0): String = strWithOffset(s, strOffset(depth))

  def strWithOffset(s: String, offset: String) = s"$offset$s"

}

trait SerializationOption

object ArraysToIndividualRecords extends SerializationOption
