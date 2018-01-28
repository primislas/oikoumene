package com.lomicron.utils.parsing

import com.fasterxml.jackson.databind.node.{JsonNodeFactory, ObjectNode}
import com.lomicron.utils.parsing.JsonParser._
import org.specs2.mutable.Specification

class ParsingScopeSpec extends Specification {
  val rootKey = "ROOT"
  val rootScope = ObjectScope(rootKey, None)
  val objectKey = "obj_field"
  val objectScope = rootScope.addField(objectKey, new ObjectNode(JsonNodeFactory.instance)).asInstanceOf[ObjectScope]

  val idTokenField = Identifier("owner")
  val idTokenValue = Identifier("BYZ")
  val fieldScope = objectScope.nextScope(idTokenField)._1
  val assignmentScope = fieldScope.nextScope(Equals)._1

  val strToken = StringT("a string token")
  val decimalToken = Number("0.5", BigDecimal("0.5"))
  val boolFalseToken = Bool("no", false)
  val boolTrueToken = Bool("yes", true)

  val dateTokenYear = 1732
  val dateTokenMonth = 11
  val dateTokenDay = 2
  val dateTokenLexeme = s"${dateTokenYear}.${dateTokenMonth}.${dateTokenDay}"
  val dateToken = Date(dateTokenLexeme, dateTokenYear, dateTokenMonth, dateTokenDay)

  "ObjectScope#nextScope" should {
    "advance to FieldScope with Identifier token" >> {
      val (scope, obj) = objectScope.nextScope(idTokenField)
      val isFieldScope = scope.isInstanceOf[FieldScope]
      isFieldScope must_== true
    }

    "translate a Date token to 'events' field and construct an object with a 'date' field" >> {
      val (scope, _) = objectScope.nextScope(dateToken)
      val obj = scope.parsedObject
      val hasDate = obj.has(DATE_FIELD)
      hasDate must_== true

      val date = obj.get(DATE_FIELD)
      val year = date.get(YEAR_FIELD).asInt()
      val month = date.get(MONTH_FIELD).asInt()
      val day = date.get(DAY_FIELD).asInt()
      year must_== dateTokenYear
      month must_== dateTokenMonth
      day must_== dateTokenDay

      val rootObj = scope.rootScope.obj
      val dateParent = rootObj.get(objectKey).asInstanceOf[ObjectNode]
      val hasEvents = dateParent.has(EVENTS_FIELD)
      hasEvents must_== true

      val isObjectScope = scope.isInstanceOf[ObjectScope]
      isObjectScope must_== true

      val scopeKey = scope.key
      scopeKey must_== EVENTS_FIELD

      val path = scope.scopePath
      path must_== Seq(rootKey, objectKey, EVENTS_FIELD)
    }

    "move up object scope stack and copy parsing errors if any upon receiving a '}' token" >> {
      val tokens = Seq(OpenBrace, Equals)
      def rec(s: ParsingScope, ts: Stream[Token]): ParsingScope = ts match {
        case h #:: t      => rec(s.addParsingError(h)._1, t)
        case Stream.Empty => s
      }

      val scope = rec(objectScope, tokens.toStream)
      val errseq = scope.scopeErrors
      errseq.length must_== tokens.length

      val (nextScope, _) = scope.nextScope(CloseBrace)
      val isObjectScope = nextScope.isInstanceOf[ObjectScope]
      isObjectScope must_== true

      val rootErrs = nextScope.scopeErrors 
      rootErrs.length must_== tokens.length
      rootErrs must_== errseq
    }

    "return root scope and root object upon receiving EOF token" >> {
      val (scope, obj) = objectScope.nextScope(EOF)
      scope must_== rootScope

      obj must_== rootScope.obj
    }

    "generate errors for tokens other than Identifier, Date, EOF, }" >> {
      val tokens = Seq(OpenBrace, boolFalseToken, boolTrueToken, strToken)
      def rec(s: ParsingScope, ts: Stream[Token]): ParsingScope = ts match {
        case h #:: t      => rec(s.nextScope(h)._1, t)
        case Stream.Empty => s
      }
      val errs = rec(objectScope, tokens.toStream).scopeErrors
      errs.length must_== tokens.length
    }
  }


}