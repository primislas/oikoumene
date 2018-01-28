package com.lomicron.utils.parsing

import com.fasterxml.jackson.databind.node.{JsonNodeFactory, ObjectNode}

object JsonParser {
  val EVENTS_FIELD = "events"
  val DATE_FIELD = "date"
  val YEAR_FIELD = "year"
  val MONTH_FIELD = "month"
  val DAY_FIELD = "day"
  val ROOT_KEY = "ROOT"

  def parse(ts: Seq[Token]): (ObjectNode, Seq[ParsingError]) = {
    @annotation.tailrec
    def rec(scope: ParsingScope, ts: Stream[Token]): ObjectScope =
      ts match {
        case Stream.Empty => scope.objectScope.get
        case h #:: t => {
          val (nextScope, obj) = scope.nextScope(h)
          rec(nextScope, t)
        }
      }
    val scope = rec(ObjectScope(ROOT_KEY, None), ts.toStream)
    (scope.parsedObject, scope.errors)
    //val (scope, states) = t.traverseS(nextState).run(ObjectScope(None))
    //states.last
  }

  def buildDate(d: Date): ObjectNode =
    new ObjectNode(JsonNodeFactory.instance)
      .put(YEAR_FIELD, d.year)
      .put(MONTH_FIELD, d.month)
      .put(DAY_FIELD, d.day)
}