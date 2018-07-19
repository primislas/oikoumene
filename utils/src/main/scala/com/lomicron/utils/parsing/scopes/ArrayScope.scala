package com.lomicron.utils.parsing.scopes

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.utils.json.JsonMapper.toJsonNode
import com.lomicron.utils.parsing.{CloseBrace, Date, Identifier, Number, Token}

case class ArrayScope(parent: Option[ObjectScope],
                      key: String,
                      elems: Seq[JsonNode]
                     ) extends ParsingScope {

  self =>

  /**
    *
    *
    * @param t [[Token]] that should advance the scope
    * @return
    */
  override def nextScope(t: Token): (ParsingScope, ObjectNode) = t match {
    case id: Identifier => addElement(toJsonNode(id.lexeme))
    case date: Date => addElement(toJsonNode(date.lexeme))
    case Number(_, bd) => addElement(toJsonNode(bd))
    case CloseBrace => (parent.get.setField(key, toJsonNode(elems)), parsedObject)
    case _ => addParsingError(t)
  }

  override def validTokens: Seq[String] =
    Seq("identifier", "}")

  def addElement(e: JsonNode): (ParsingScope, ObjectNode) =
    (ArrayScope(parent, key, elems :+ e), parsedObject)

}

object ArrayScope {
  def apply(parent: Option[ObjectScope],
            key: String,
            elems: JsonNode): ArrayScope =
    new ArrayScope(parent, key, Seq(elems))

  def apply(parent: Option[ObjectScope],
            key: String,
            e1: JsonNode,
            e2: JsonNode): ArrayScope =
    new ArrayScope(parent, key, Seq(e1, e2))

}
