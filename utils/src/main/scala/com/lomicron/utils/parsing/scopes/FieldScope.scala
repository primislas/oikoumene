package com.lomicron.utils.parsing.scopes

import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.utils.json.JsonMapper
import com.lomicron.utils.json.JsonMapper.toJsonNode
import com.lomicron.utils.parsing._

case class FieldScope(parent: Option[ObjectScope], key: String) extends ParsingScope {
  self =>

  override def validTokens = Seq("=")

  override def nextScope(t: Token): (ParsingScope, ObjectNode) =
    t match {
      case Equals => (AssignmentScope(parent, key), parsedObject)
      case _: Comment => (self, parsedObject)
      case id: Identifier => (ArrayScope(parent.flatMap(_.parent), parent.get.key, toJsonNode(key), toJsonNode(id.lexeme)), parsedObject)
      case CloseBrace => (parent.get.setField(parent.get.key, asArrayNode), parsedObject)
      case _ => addParsingError(t)
    }

  private def asArrayNode =
    JsonMapper.arrayNodeOf(key)
}
