package com.lomicron.utils.parsing.scopes

import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.utils.json.JsonMapper.objectNode
import com.lomicron.utils.parsing._

case class ObjectScope(
                        key: String,
                        parent: Option[ObjectScope],
                        obj: ObjectNode = objectNode,
                        errors: Seq[ParsingError] = Nil)
  extends ParsingScope {
  self =>

  override def validTokens = Seq("identifier", "}")

  override def nextScope(t: Token): (ParsingScope, ObjectNode) = {
    def moveParsingErrors = {
      val nextScope =
        if (parent.isDefined) self match {
          case o: ObjectScope =>
            val errors = o.errors
            val p = parent.get
            if (errors.nonEmpty) {
              val cp = p.copy(errors = p.errors ++ errors)
              cp
            } else p
          case _ => parent.get
        }
        else self
      (nextScope, parsedObject)
    }

    t match {
      case Identifier(s) => (FieldScope(Option(self), s), parsedObject)
      case d: Date => (FieldScope(Option(self), d.toString), parsedObject)
      case CloseBrace => moveParsingErrors
      case EOF => (rootScope, rootObject)
      case _: Comment => (self, parsedObject)
      case _ => addParsingError(t)
    }
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
