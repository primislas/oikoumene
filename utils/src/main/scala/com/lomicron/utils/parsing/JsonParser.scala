package com.lomicron.utils.parsing

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.utils.json.JsonMapper
import com.lomicron.utils.parsing.scopes.{ObjectScope, ParsingError, ParsingScope}
import com.lomicron.utils.parsing.serialization.{DefaultDeserializer, Deserializer}
import com.lomicron.utils.parsing.tokenizer.{Token, Tokenizer}

object JsonParser {

  val rootKey = "ROOT"

  def objectNode: ObjectNode = JsonMapper.objectNode

  def parse(str: String): (JsonNode, Seq[ParsingError]) =
    parse(str, DefaultDeserializer)

  def parse(str: String, deserializer: Deserializer): (JsonNode, Seq[ParsingError]) = {
    val tokens = Tokenizer.tokenize(str)
    parse(tokens, deserializer)
  }

  def parse(ts: Seq[Token]): (JsonNode, Seq[ParsingError]) =
    parse(ts, DefaultDeserializer)

  def parse(ts: Seq[Token], deserializer: Deserializer): (JsonNode, Seq[ParsingError]) = {
    @annotation.tailrec
    def rec(scope: ParsingScope, ts: LazyList[Token]): ObjectScope =
      ts match {
        case LazyList() => scope.objectScope.get
        case h #:: t =>
          val (nextScope, _) = scope.nextScope(h)
          rec(nextScope, t)
      }

    val scope = rec(ObjectScope(rootKey, None), ts.to(LazyList))
    val (node, errors) = deserializer.run(scope.parsedObject)
    (node, scope.errors ++ errors)
  }

  def camelify(obj: ObjectNode): ObjectNode = {
    val camelified = objectNode
    obj.fields.forEachRemaining(e => {
      val k = e.getKey
      val v = e.getValue match {
        case o: ObjectNode => camelify(o)
        case j: JsonNode => j
      }
      camelified.set(camelCase(k), v)
    })
    camelified
  }

  /**
    * Turns a string of format "foo_bar" into camel case "fooBar"
    *
    * Functional code courtesy of Jamie Webb (j@jmawebb.cjb.net) 2006/11/28
    * @param name the String to CamelCase
    *
    * @return the CamelCased string
    */
  def camelCase(name : String): String = {
    def loop(x : List[Char]): List[Char] = (x: @unchecked) match {
      case '_' :: '_' :: rest => loop('_' :: rest)
      case '_' :: c :: rest => Character.toUpperCase(c) :: loop(rest)
      case '_' :: Nil => Nil
      case c :: rest => c :: loop(rest)
      case Nil => Nil
    }
    if (name == null) ""
    else loop(name.toList).mkString
  }

}
