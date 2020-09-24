package com.lomicron.utils.parsing.scopes

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.utils.json.JsonMapper
import com.lomicron.utils.json.JsonMapper.{ObjectNodeEx, objectNode}
import com.lomicron.utils.parsing.scopes.ObjectScope.arrayKey
import com.lomicron.utils.parsing.tokenizer.{Bool, CloseBrace, Comment, Date, EOF, Identifier, Number, OpenBrace, Token}

case class ObjectScope(key: String,
                       parent: Option[ObjectScope],
                       obj: ObjectNode = objectNode,
                       errors: Seq[ParsingError] = Seq.empty,
                       elements: Seq[JsonNode] = Seq.empty)
  extends ParsingScope {
  self =>

  override def validTokens: Seq[String] = Seq("identifier", "number", "bool", "date","{", "}")

  override def nextScope(t: Token): (ParsingScope, ObjectNode) =
    t match {
      case Identifier(s) => (FieldScope(Option(self), s), parsedObject)
      case d: Date => (FieldScope(Option(self), d.toString), parsedObject)
      case _: Comment => (self, parsedObject)
      case n: Number => (ArrayScope(Some(self), elements :+ n.toJsonNode), parsedObject)
      case b: Bool => (ArrayScope(Some(self), elements :+ b.toJsonNode), parsedObject)
      case OpenBrace => AssignmentScope(Option(self), arrayKey).nextScope(OpenBrace)
      case CloseBrace => moveParsingErrors
      case EOF => (rootScope, rootObject)
      case _ => addParsingError(t)
    }

  private def moveParsingErrors: (ObjectScope, ObjectNode) = {
    val nextScope = parent
      .map(p => self match {
        case o: ObjectScope =>
          val errors = o.errors
          if (errors.nonEmpty) p.copy(errors = p.errors ++ errors)
          else p
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

  def withElements(es: Seq[JsonNode]): ObjectScope = {
    val array = JsonMapper.arrayNodeOf(es)
    obj.setEx(arrayKey, array)
    // TODO mutable obj is being copied
    copy(elements = es)
  }

}

object ObjectScope {
  val arrayKey = "arrayElements"
}
