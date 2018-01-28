package com.lomicron.utils.parsing

sealed trait Token {
  val lexeme: String
}

case class Identifier(lexeme: String) extends Token
case class StringT(lexeme: String) extends Token
case class InvalidIdentifier(lexeme: String) extends Token
case class Bool(lexeme: String, asBoolean: Boolean) extends Token
case class Number(lexeme: String, asBigDecimal: BigDecimal) extends Token
case class Date(lexeme: String, year: Int, month: Int, day: Int) extends Token
case object Equals extends Token { val lexeme = "=" }
case object OpenParentheses extends Token { val lexeme = "{" }
case object CloseParentheses extends Token { val lexeme = "}" }
case object OpenBrace extends Token { val lexeme = "(" }
case object CloseBrace extends Token { val lexeme = ")" }
case object Comma extends Token { val lexeme = "," }
case class Comment(lexeme: String) extends Token
case class InvalidToken(lexeme: String, expected: String, encountered: Token) extends Token
case object EOF extends Token { val lexeme = "" }

