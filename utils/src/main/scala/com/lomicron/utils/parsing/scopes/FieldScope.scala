package com.lomicron.utils.parsing.scopes

import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.utils.json.JsonMapper.toJsonNode
import com.lomicron.utils.parsing.tokenizer._

case class FieldScope(parent: Option[ObjectScope],
                      key: String)
  extends ParsingScope {
  self =>

  override def validTokens: Seq[String] = Seq("=")

  override def nextScope(t: Token): (ParsingScope, ObjectNode) =
    t match {
      case Equals => (AssignmentScope(parent, key), parsedObject)
      case OpenBrace =>
        // treating this as a missing '='
        addParsingError(t)
        AssignmentScope(parent, key).nextScope(t)
      case _: Comment => (self, parsedObject)
      case id: Identifier => (ArrayScope(parent, Seq(toJsonNode(key), toJsonNode(id.lexeme))), parsedObject)
      case d: Date => (ArrayScope(parent, Seq(toJsonNode(key), toJsonNode(d.lexeme))), parsedObject)
      case CloseBrace =>
        // single element array
        val elem = toJsonNode(key)
        ArrayScope(parent, Seq(elem)).nextScope(t)
      case _ =>
        val (s,_) = addParsingError(t)
        // let parent scope to attempt re-evaluate the token
        s.nextScope(t)
    }

}
