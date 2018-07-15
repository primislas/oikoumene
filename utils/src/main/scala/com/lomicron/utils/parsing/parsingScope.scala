package com.lomicron.utils.parsing

import java.math.BigDecimal

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node._
import com.lomicron.utils.json.JsonMapper
import com.lomicron.utils.json.JsonMapper.objectNode

sealed trait ParsingScope {
  self =>

  /**
    *
    *
    * @param t [[Token]] that should advance the scope
    * @return
    */
  def nextScope(t: Token): (ParsingScope, ObjectNode)

  /**
    * Returns reference to the parent scope, if any. A parent scope
    * is the closest object scope on the scope stack.
    *
    * @return reference to the parent scope, if any
    */
  def parent: Option[ObjectScope]

  /**
    * Scope's key, if any. Key is the JSON field name the scope
    * belongs to.
    *
    * @return JSON key the scope is processing
    */
  def key: String

  def objectScope: Option[ObjectScope] = self match {
    case o: ObjectScope => Option(o)
    case _ => parent
  }

  /**
    * Returns JSON object closest to the calling scope on scope stack.
    * I.e. it will return reference to an object node belonging
    * to the closest object scope on scope stack.
    *
    * @return {ObjectNode} that the scope is expected to
    *         be modifying
    */
  def parsedObject: ObjectNode = {
    self match {
      case ObjectScope(_, _, obj, _) => obj
      case _ =>
        if (parent.isDefined) parent.get.obj
        else throw ParsingException("No parsed object exists for the scope", null)
    }
  }

  def rootScope: ObjectScope = {
    @annotation.tailrec
    def rec(s: ObjectScope): ObjectScope =
      if (s.parent.isDefined) rec(s.parent.get)
      else s

    rec(objectScope.getOrElse(throw ParsingException("No root object scope exists")))
  }

  def rootObject: ObjectNode = rootScope.obj

  def addField(field: String, value: JsonNode): ParsingScope = {

    val (nextParent, nextParsedObject) = value match {
      case on: ObjectNode => (objectScope, on)
      case _ => (parent, parsedObject)
    }

    val nextKey = if (value.isInstanceOf[ObjectNode]) field else key
    JsonMapper.mergeFieldValue(parsedObject, field, value)

    ObjectScope(nextKey, nextParent, nextParsedObject)
  }

  def removeField(o: ObjectNode, key: String, value: JsonNode): ParsingScope = {
    if (o.has(key)) {
      val v = o.get(key)
      if (v.isArray) {
        val it = v.asInstanceOf[ArrayNode].iterator()
        var removed = false
        while (it.hasNext && !removed) {
          val i = it.next()
          if (i eq value) {
            it.remove()
            removed = true
          }
        }
      } else o.remove(key)
    }
    objectScope.get
  }

  def scopePath: Seq[String] = {
    @annotation.tailrec
    def rec(scope: ParsingScope, path: Seq[String]): Seq[String] = {
      val keys: Seq[String] = scope.key +: path
      scope.parent match {
        case Some(s) => rec(s, keys)
        case None => keys
      }
    }

    rec(self, Nil)
  }

  def validTokens: Seq[String]

  def addParsingError(t: Token): (ParsingScope, ObjectNode) = {
    val err = ParsingError(scopePath, validTokens, t)
    val scope = objectScope.get
    val scopeWithErr = scope.copy(errors = scope.errors :+ err)
    (scopeWithErr, scopeWithErr.parsedObject)
  }

  def scopeErrors: Seq[ParsingError] = objectScope.map(_.errors).getOrElse(Nil)
}

//case class RootScope(objects: List[ObjectNode]) extends ParsingScope

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
              println("copying errors to: " + p)
              println("next scope: " + cp)
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

case class ParsingError(path: Seq[String], expected: Seq[String], encountered: Token) {
  override def toString: String = {
    val sb = new StringBuilder("Parsing error at: ")

    @annotation.tailrec
    def rec(ss: Seq[String], sep: String): Unit = ss match {
      case h :: Nil => sb.append(h)
      case h :: t =>
        sb.append(h).append(sep)
        rec(t, sep)
      case _ => ()
    }

    rec(path, " => ")
    sb.append(". Expected: ")
    rec(expected, ", ")
    sb.append(". Encountered: ").append(encountered)
    sb.toString
  }
}

case class ParsingException(private val message: String = "",
                            private val cause: Throwable = None.orNull)
  extends RuntimeException(message, cause)