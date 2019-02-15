package com.lomicron.utils.parsing.tokenizer

import java.math.BigDecimal

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.{DecimalNode, IntNode}

sealed trait Token {
  val lexeme: String
}

case class Identifier(lexeme: String) extends Token
case class StringT(lexeme: String) extends Token
case class InvalidIdentifier(lexeme: String) extends Token
case class Bool(lexeme: String, asBoolean: Boolean) extends Token
case class Number(lexeme: String, asBigDecimal: scala.BigDecimal) extends Token {
  override def toString: String = s"Number($lexeme)"
  def isInt: Boolean = lexeme.matches("""^(\d+)$""")
  def toJsonNode: JsonNode =
    if (isInt) IntNode.valueOf(asBigDecimal.intValue)
    else DecimalNode.valueOf(new BigDecimal(lexeme))
}
case class Date(lexeme: String, year: Int, month: Int, day: Int) extends Token with Ordered[Date] {
  override def toString: String = Date.toString(year, month, day)

  override def compare(that: Date): Int = {
    val lt = -1
    val gt = 1
    val eq = 0

    if (year < that.year) lt
    else if (year > that.year) gt
    else if (month < that.month) lt
    else if (month > that.month) gt
    else if (day < that.day) lt
    else if (day > that.day) gt
    else eq
  }
}
object Date {
  def toString(year: Int, month: Int, day: Int): String =
    s"${f"$year%02d"}.${f"$month%02d"}.${f"$day%02d"}"

  def apply(lexeme: String, year: Int, month: Int, day: Int): Date =
    new Date(lexeme, year, month, day)

  def apply(year: String, month: String, day: String): Date =
    apply(year.toInt, month.toInt, day.toInt)

  def apply(year: Int, month: Int, day: Int): Date = {
    val lexeme = toString(year, month, day)
    apply(lexeme , year, month, day)
  }
}
case object Equals extends Token { val lexeme = "=" }
case object OpenParentheses extends Token { val lexeme = "{" }
case object CloseParentheses extends Token { val lexeme = "}" }
case object OpenBrace extends Token { val lexeme = "(" }
case object CloseBrace extends Token { val lexeme = ")" }
case object OpenArray extends Token { val lexeme = "[" }
case object CloseArray extends Token { val lexeme = "]" }
case object Comma extends Token { val lexeme = "," }
case class Comment(lexeme: String) extends Token
case class InvalidToken(lexeme: String, expected: String, encountered: Token) extends Token
case object EOF extends Token { val lexeme = "" }

