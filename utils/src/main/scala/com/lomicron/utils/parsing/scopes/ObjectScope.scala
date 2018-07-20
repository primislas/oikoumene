package com.lomicron.utils.parsing.scopes

import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.utils.json.JsonMapper.{objectNode, toJsonNode}
import com.lomicron.utils.parsing.tokenizer.{CloseBrace, Comment, Date, EOF, Identifier, Number, Token}

case class ObjectScope(
                        key: String,
                        parent: Option[ObjectScope],
                        obj: ObjectNode = objectNode,
                        errors: Seq[ParsingError] = Seq.empty)
  extends ParsingScope {
  self =>

  override def validTokens = Seq("identifier", "}")

  override def nextScope(t: Token): (ParsingScope, ObjectNode) =
    t match {
      case Identifier(s) => (FieldScope(Option(self), s), parsedObject)
      case d: Date => (FieldScope(Option(self), d.toString), parsedObject)
      case CloseBrace => moveParsingErrors
      case EOF => (rootScope, rootObject)
      case _: Comment => (self, parsedObject)
      case n: Number =>
        if (obj.fieldNames().hasNext) addParsingError(t)
        else (ArrayScope(parent, key, toJsonNode(n.asBigDecimal)), parsedObject)
      case _ => addParsingError(t)
    }

  private def moveParsingErrors: (ObjectScope, ObjectNode) = {
    val nextScope = parent
      .map(p => self match {
        case o: ObjectScope =>
          val errors = o.errors
          if (errors.nonEmpty) {
            val cp = p.copy(errors = p.errors ++ errors)
            cp
          } else p
        case _ => p
      })
      .getOrElse(self)
    (nextScope, parsedObject)
  }


  override def toString: String = {
    val sb = new StringBuilder("ObjectScope: ")

    @annotation.tailrec
    def rec(ss: Seq[String], sep: String): Unit = ss match {
      case h :: Nil => sb.append(h)
      case h :: t =>
        sb.append(h).append(sep)
        rec(t, sep)
      case _ => ()
    }

    rec(scopePath, " => ")
    sb.append(". Errors: ").append(errors.length).append('.')
    sb.toString()
  }
}
