package com.lomicron.oikoumene.serializers

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.{ArrayNode, BooleanNode, ObjectNode}
import com.lomicron.oikoumene.parsers.ClausewitzParser.Fields
import com.lomicron.utils.json.JsonMapper._


object ClausewitzSerializer {

  def serialize[T <: AnyRef](entity: T): String =
    Option(entity)
      .flatMap(toObjectNode)
      .map(serializeJsonObj)
      .getOrElse("")

  def serializeJsonObj(node: ObjectNode): String = {
    val events = node.getArray(Fields.history)
      .map(_.toSeq)
      .getOrElse(Seq.empty)
      .flatMap(_.asObject)
    node.remove(Fields.history)

    val initState = recSerialize(node)
    val history = events.flatMap(recSerialize(_))
    val lines = initState ++ history
    lines.mkString("\n")
  }

  def recSerialize(node: ObjectNode, depth: Int = 0): Seq[String] = {
    val offset = "\t" * depth

    node.entries().flatMap(e => {
      val (k, v) = e
      v match {
        case o: ObjectNode =>
          val serializedObj = recSerialize(o, depth + 1)
          wrapAsObject(k, serializedObj, depth)
        case a: ArrayNode =>
          val serializedArray = serializeArray(a, depth + 1)
          wrapAsObject(k, serializedArray, depth)
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
