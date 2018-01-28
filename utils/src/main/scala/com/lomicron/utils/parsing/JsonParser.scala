package com.lomicron.utils.parsing

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.{ArrayNode, JsonNodeFactory, ObjectNode}

object JsonParser {

  val rootKey = "ROOT"

  def objectNode = new ObjectNode(JsonNodeFactory.instance)

  def parse(str: String): (ObjectNode, Seq[ParsingError]) = {
    val tokens = Tokenizer.tokenize(str)
    parse(tokens)
  }

  def parse(ts: Seq[Token]): (ObjectNode, Seq[ParsingError]) = {
    @annotation.tailrec
    def rec(scope: ParsingScope, ts: Stream[Token]): ObjectScope =
      ts match {
        case Stream.Empty => scope.objectScope.get
        case h #:: t =>
          val (nextScope, _) = scope.nextScope(h)
          rec(nextScope, t)
      }

    val scope = rec(ObjectScope(rootKey, None), ts.toStream)
    (scope.parsedObject, scope.errors)
    //val (scope, states) = t.traverseS(nextState).run(ObjectScope(None))
    //states.last
  }

  def mergeField(o: ObjectNode, k: String, v: JsonNode): ObjectNode = {
    if (!o.has(k)) o.set(k, v)
    else {
      val exst = o.get(k)
      if (exst.isArray)
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

  def removeField(o: ObjectNode, k: String, v: JsonNode): ObjectNode = {
    Option(o.get(k))
      .map(existingVal => {
        if (existingVal.isArray) {
          val it = existingVal.asInstanceOf[ArrayNode].iterator
          while (it.hasNext) {
            val e = it.next
            var removed = false
            if (v.equals(e) && !removed) {
              it.remove()
              removed = true
            }
          }
        } else if (v.equals(existingVal))
          o.remove(k)
        o
      })
      .getOrElse(o)
  }

}