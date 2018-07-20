package com.lomicron.utils.parsing.scopes

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.{ArrayNode, ObjectNode}
import com.lomicron.utils.json.JsonMapper
import com.lomicron.utils.parsing.tokenizer.Token

trait ParsingScope {
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
    val objScope = objectScope
    val (nextParent, nextParsedObject) = value match {
      case on: ObjectNode => (objScope, on)
      case _ => (parent, parsedObject)
    }

    val nextKey = if (value.isInstanceOf[ObjectNode]) field else key
    JsonMapper.mergeFieldValue(parsedObject, field, value)

    val errors = objScope.map(_.errors).getOrElse(Seq.empty)
    ObjectScope(nextKey, nextParent, nextParsedObject, errors)
  }

  def setField(field: String, value: JsonNode): ParsingScope = {
    parsedObject.set(field, value)
    ObjectScope(key, parent, parsedObject)
  }

  def setField(value: JsonNode): ParsingScope = {
    parsedObject.set(key, value)
    ObjectScope(key, parent, parsedObject)
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
    println(err)
    val scope = objectScope.get
    val scopeWithErr = scope.copy(errors = scope.errors :+ err)
    (scopeWithErr, scopeWithErr.parsedObject)
  }

  def scopeErrors: Seq[ParsingError] = objectScope.map(_.errors).getOrElse(Nil)
}
