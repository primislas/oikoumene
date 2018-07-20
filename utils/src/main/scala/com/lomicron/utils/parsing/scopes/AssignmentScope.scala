package com.lomicron.utils.parsing.scopes

import java.math.BigDecimal

import com.fasterxml.jackson.databind.node.{BooleanNode, DecimalNode, ObjectNode, TextNode}
import com.lomicron.utils.parsing._
import com.lomicron.utils.parsing.tokenizer._

case class AssignmentScope(parent: Option[ObjectScope], key: String) extends ParsingScope {
  val booleans = Set("yes", "no")

  def isBoolean(s: String): Boolean = booleans.contains(s)

  override def validTokens = Seq("{", "identifier", "number", "date")

  override def nextScope(t: Token): (ParsingScope, ObjectNode) = {
    val value = t match {
      case OpenBrace => Option(JsonParser.objectNode)
      case Bool(_, b) => Option(BooleanNode.valueOf(b))
      case Identifier(s) => Option(TextNode.valueOf(s))
      case Number(lex, _) => Option(DecimalNode.valueOf(new BigDecimal(lex)))
      case Date(d, _, _, _) => Option(TextNode.valueOf(d))
      case _ => None
    }

    value match {
      case Some(v) => (parent.get.addField(key, v), parsedObject)
      case _ => addParsingError(t)
    }
  }
}
