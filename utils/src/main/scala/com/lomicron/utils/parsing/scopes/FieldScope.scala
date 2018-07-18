package com.lomicron.utils.parsing.scopes

import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.utils.parsing.{Comment, Equals, Token}

case class FieldScope(parent: Option[ObjectScope], key: String) extends ParsingScope {
  self =>

  override def validTokens = Seq("=")

  override def nextScope(t: Token): (ParsingScope, ObjectNode) =
    t match {
      case Equals => (AssignmentScope(parent, key), parsedObject)
      case _: Comment => (self, parsedObject)
      case _ => addParsingError(t)
    }
}
