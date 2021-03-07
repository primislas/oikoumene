package com.lomicron.utils.parsing

import com.lomicron.utils.json.JsonMapper.objectNode
import com.lomicron.utils.parsing.scopes.{FieldScope, ObjectScope, ParsingScope}
import com.lomicron.utils.parsing.tokenizer._
import org.specs2.mutable.Specification

import scala.annotation.tailrec

class ParsingScopeSpec extends Specification {
  private val rootKey = "ROOT"
  private val rootScope = ObjectScope(rootKey, None)
  private val objectKey = "obj_field"
  private val objectScope: ObjectScope = rootScope.addField(objectKey, objectNode).asInstanceOf[ObjectScope]

  private val idTokenField = Identifier("owner")
  private val idTokenValue = Identifier("BYZ")
  private val fieldScope: ParsingScope = objectScope.nextScope(idTokenField)._1
  private val assignmentScope: ParsingScope = fieldScope.nextScope(Equals)._1

  private val strToken = StringT("a string token")
  private val decimalToken = Number("0.5", BigDecimal("0.5"))
  private val boolFalseToken = Bool("no", asBoolean = false)
  private val boolTrueToken = Bool("yes", asBoolean = true)

  private val dateTokenYear = 1732
  private val dateTokenMonth = 11
  private val dateTokenDay = 2
  private val dateTokenLexeme = s"$dateTokenYear.$dateTokenMonth.$dateTokenDay"
  private val dateToken = Date(dateTokenLexeme, dateTokenYear, dateTokenMonth, dateTokenDay)

  "ObjectScope#nextScope" should {

    "- advance to FieldScope with Identifier token" >> {
      val (scope, _) = objectScope.nextScope(idTokenField)
      val isFieldScope = scope.isInstanceOf[FieldScope]
      isFieldScope must_== true
    }

    "- move up object scope stack and copy parsing errors if any upon receiving a '}' token" >> {
      val tokens = Seq(OpenBrace, Equals)
      @tailrec
      def rec(s: ParsingScope, ts: LazyList[Token]): ParsingScope = ts match {
        case h #:: t      => rec(s.addParsingError(h)._1, t)
        case LazyList() => s
      }

      val scope = rec(objectScope, tokens.to(LazyList))
      val errseq = scope.scopeErrors
      errseq.length must_== tokens.length

      val (nextScope, _) = scope.nextScope(CloseBrace)
      val isObjectScope = nextScope.isInstanceOf[ObjectScope]
      isObjectScope must_== true

      val rootErrs = nextScope.scopeErrors 
      rootErrs.length must_== tokens.length
      rootErrs must_== errseq
    }

    "- return root scope and root object upon receiving EOF token" >> {
      val (scope, obj) = objectScope.nextScope(EOF)
      scope must_== rootScope

      obj must_== rootScope.obj
    }

  }

}
