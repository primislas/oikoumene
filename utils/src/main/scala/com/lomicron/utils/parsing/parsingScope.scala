package com.lomicron.utils.parsing

import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.BooleanNode
import com.fasterxml.jackson.databind.node.TextNode
import com.fasterxml.jackson.databind.node.DecimalNode
import java.math.BigDecimal

sealed trait ParsingScope { self =>
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
    case ObjectScope(_, _, _, _) => Option(self.asInstanceOf[ObjectScope])
    case _                       => parent
  }

  /**
   * Returns JSON object closest to the calling scope on scope stack.
   * I.e. it will return reference to an object node belonging
   * to the closest object scope on scope stack.
   *
   * @return {@link ObjectNode} that the scope is expected to
   * be modifying
   */
  def parsedObject: ObjectNode = {
    self match {
      case ObjectScope(_, _, obj, _) => obj
      case _ =>
        if (parent.isDefined) parent.get.obj
        else throw new ParsingException("No parsed object exists for the scope", null)
    }
  }

  def rootScope: ObjectScope = {
    @annotation.tailrec
    def rec(s: ObjectScope): ObjectScope =
      if (s.parent.isDefined) rec(s.parent.get)
      else s
    rec(objectScope.getOrElse(throw new ParsingException("No root object scope exists")))
  }
  
  def rootObject: ObjectNode = rootScope.obj

  def addField(field: String, value: JsonNode): ParsingScope = {

    def addField(o: ObjectNode, k: String, v: JsonNode) = {
      if (!o.has(k)) o.set(k, v)
      else {
        val exst = o.get(k)
        if (exst.isArray())
          exst.asInstanceOf[ArrayNode].add(v)
        else {
          val arr = new ArrayNode(JsonNodeFactory.instance)
          arr.add(exst)
          arr.add(v)
          o.set(k, arr)
        }
      }
      o
    }

    val (nextParent, nextParsedObject) =
      if (value.isInstanceOf[ObjectNode]) (objectScope, value.asInstanceOf[ObjectNode])
      else (parent, parsedObject)

    val notEvent = nextParent.map(_.key != JsonParser.EVENTS_FIELD).getOrElse(true)

    val (realKey, remove) = if (notEvent) {
      val addCommand = "add_"
      val removeCommand = "remove_"
      val extractKey = (s: String, prefix: String) => s.drop(prefix.length()) + "s"
      if (field.startsWith(addCommand)) (extractKey(field, addCommand), false)
      else if (field.startsWith(removeCommand)) (extractKey(field, removeCommand), true)
      else (field, false)
    } else (field, false)

    val nextKey = if (value.isInstanceOf[ObjectNode]) realKey else key

    if (remove) removeField(parsedObject, realKey, value)
    else addField(parsedObject, realKey, value)
    ObjectScope(nextKey, nextParent, nextParsedObject)
  }

  def removeField(o: ObjectNode, key: String, value: JsonNode): ParsingScope = {
    if (o.has(key)) {
      val v = o.get(key)
      if (v.isArray()) {
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
        case None    => keys
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
  obj: ObjectNode = new ObjectNode(JsonNodeFactory.instance),
  val errors: Seq[ParsingError] = Nil)
    extends ParsingScope { self =>

  override def validTokens = Seq("identifier", "}")

  override def nextScope(t: Token): (ParsingScope, ObjectNode) = {
    def addDateObject(ds: Date) = {
      import JsonParser._
      val dateObj = JsonParser.buildDate(ds)
      val nextObj = new ObjectNode(JsonNodeFactory.instance).set(DATE_FIELD, dateObj)
      val nextScope = addField(EVENTS_FIELD, nextObj)
      (nextScope, parsedObject)
    }

    def moveParsingErrors = {
      val nextScope =
        if (parent.isDefined) self match {
          case ObjectScope(_, _, _, es) => {
            val p = parent.get
            val curr = self
            if (es.length > 0) {
              val cp = p.copy(errors = p.errors ++ es)
              println("copying errors to: " + p)
              println("next scope: " + cp)
              cp
            } else parent.get
          }
          case _ => parent.get
        }
        else self
      (nextScope, parsedObject)
    }

    t match {
      case Identifier(s)    => (FieldScope(Option(self), s), parsedObject)
      case Date(_, _, _, _) => addDateObject(t.asInstanceOf[Date])
      case CloseBrace       => moveParsingErrors
      case EOF              => (rootScope, rootObject)
      case _                => addParsingError(t)
    }
  }

  override def toString = {
    val sb = new StringBuilder("ObjectScope: ");

    @annotation.tailrec
    def rec(ss: Seq[String], sep: String): Unit = ss match {
      case h :: Nil => sb.append(h)
      case h :: t => {
        sb.append(h).append(sep)
        rec(t, sep)
      }
      case _ => ()
    }

    rec(scopePath, " => ")
    sb.append(". Errors: ").append(errors.length).append('.')
    sb.toString()
  }
}

case class FieldScope(parent: Option[ObjectScope], key: String) extends ParsingScope {
  override def validTokens = Seq("=")

  override def nextScope(t: Token): (ParsingScope, ObjectNode) =
    t match {
      case Equals => (AssignmentScope(parent, key), parsedObject)
      case _      => addParsingError(t)
    }
}

case class AssignmentScope(parent: Option[ObjectScope], key: String) extends ParsingScope {
  val bools = Set("yes", "no")
  def isBoolean(s: String) = bools.contains(s)
  override def validTokens = Seq("{", "identifier", "number", "date")

  override def nextScope(t: Token): (ParsingScope, ObjectNode) = {
    val value = t match {
      case OpenBrace        => Option(new ObjectNode(JsonNodeFactory.instance))
      case Bool(_, b)       => Option(BooleanNode.valueOf(b))
      case Identifier(s)    => Option(TextNode.valueOf(s))
      case Number(lex, _)   => Option(DecimalNode.valueOf(new BigDecimal(lex)))
      case Date(d, _, _, _) => Option(TextNode.valueOf(d))
      case _                => None
    }

    value match {
      case Some(v) => (parent.get.addField(key, v), parsedObject)
      case _       => addParsingError(t)
    }
  }
}

case class ParsingError(path: Seq[String], expected: Seq[String], encountered: Token) {
  override def toString = {
    val sb = new StringBuilder("Parsing error at: ");

    @annotation.tailrec
    def rec(ss: Seq[String], sep: String): Unit = ss match {
      case h :: Nil => sb.append(h)
      case h :: t => {
        sb.append(h).append(sep)
        rec(t, sep)
      }
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
    extends Exception(message, cause) 