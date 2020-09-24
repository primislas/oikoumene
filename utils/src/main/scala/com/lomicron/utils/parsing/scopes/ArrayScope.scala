package com.lomicron.utils.parsing.scopes

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.utils.json.JsonMapper.toJsonNode
import com.lomicron.utils.parsing.tokenizer._

case class ArrayScope(parent: Option[ObjectScope],
                      elems: Seq[JsonNode])
  extends ParsingScope {

  self =>

  val key: String = "__elements"

  /**
    *
    *
    * @param t [[Token]] that should advance the scope
    * @return
    */
  override def nextScope(t: Token): (ParsingScope, ObjectNode) = t match {
    case id: Identifier => addElement(toJsonNode(id.lexeme))
    case date: Date => addElement(toJsonNode(date.lexeme))
    case n: Number => addElement(n.toJsonNode)
    case b: Bool => addElement(b.toJsonNode)
    case Equals =>
      elems.lastOption match {
        case Some(jsonNode) =>
          // '=' token means that last token in array
          // is actually to be treated as a key in field scope
          val nextElems = elems.dropRight(1)
          val parentOpt = parent.map(_.withElements(nextElems))
          (AssignmentScope(parentOpt, jsonNode.asText()), parsedObject)
        case _ =>
          addParsingError(t)
          (self, parsedObject)
      }
    case CloseBrace =>
//      (parent.map(_.withElements(elems)).flatMap(_.parent).get, parsedObject)
      parent.map(_.withElements(elems)).get.nextScope(t)
    case _: Comment => (self, parsedObject)
    case _ =>
      addParsingError(t)
      (self, parsedObject)
  }

  override def validTokens: Seq[String] =
    Seq("identifier", "=", "}")

  def addElement(e: JsonNode): (ParsingScope, ObjectNode) =
    (ArrayScope(parent, elems :+ e), parsedObject)

}

object ArrayScope {

  def apply(parent: Option[ObjectScope],
            elems: JsonNode): ArrayScope =
    apply(parent, Seq(elems))

  def apply(parent: Option[ObjectScope],
            elems: Seq[JsonNode]): ArrayScope =
    new ArrayScope(parent, elems)

}
