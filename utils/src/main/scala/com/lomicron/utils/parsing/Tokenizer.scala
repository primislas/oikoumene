package com.lomicron.utils.parsing

import scalaz.State

import scala.util.matching.Regex

object Tokenizer {
  type Chars = Stream[Char]
  type ParsingState = State[Chars, Token]

  val newLine = '\n'
  val comments = Map('#' -> '\n')
  val commentEnds: Set[Char] = comments.values.toSet
  val whiteSpaces = Set(' ', '\t', '\n', '\r')
  val operators = Set('=')
  val brackets = Set('(', ')', '{', '}')
  val stringLiterals = Set('\'', '\"')
  val separators: Set[Char] = whiteSpaces ++ operators ++ brackets ++ comments.keySet

  val stringPat: Regex = """"([^\s]*)"""".r
  val charPat: Regex = """'([^\s]*)'""".r
  val identifierPat: Regex = """([a-zA-Z]+[_\da-zA-Z]*)""".r
  val numberPat: Regex = """(-*\d+(?:.\d*)?)""".r
  val datePat: Regex = """(\d+)\.(\d+)\.(\d+)""".r
  val booleanPat: Regex = """(yes|no)""".r

  def tokenize(s: String): Seq[Token] = tokenize(s.toStream)

  def tokenize(s: Stream[Char]): Seq[Token] = {
    @annotation.tailrec
    def parseAll(chars: Chars, tokens: Vector[Token]): (Chars, Seq[Token]) = {
      val (nextChars, token) = nextToken(chars)
      val combinedTokens: Vector[Token] = tokens :+ token
      token match {
        case EOF => (nextChars, combinedTokens)
        case _   => parseAll(nextChars, combinedTokens)
      }
    }
    parseAll(s, Vector())._2
  }

  val isWhiteSpace: Char => Boolean = whiteSpaces.contains

  val isNewLine: Char => Boolean = _ == newLine

  val readOperator: Chars => (Chars, Token) = s => s.headOption match {
    case Some('=') => (s.drop(1), Equals)
    case Some(c)   => throw new RuntimeException(s"Parsing exception: encountered unknown operator $c")
    case None      => throw new RuntimeException(s"Parsing exception: encountered an empty stream")
  }

  val readBracket: Chars => (Chars, Token) = s => s.headOption.getOrElse('0') match {
    case '{'     => (s.drop(1), OpenBrace)
    case '}'     => (s.drop(1), CloseBrace)
    case '('     => (s.drop(1), OpenParentheses)
    case ')'     => (s.drop(1), CloseParentheses)
    case default => throw new RuntimeException(s"Parsing exception: encountered unknown bracket $default")
  }

  val readComment: Stream[Char] => (Chars, Token) = s => {
    val notCommentEnd: Char => Boolean = !commentEnds.contains(_)
    val c = s.takeWhile(notCommentEnd).mkString
    (s.drop(c.length), Comment(c.dropWhile(_ == '#').trim))
  }

  val readWord: Stream[Char] => (Chars, Token) = s => {
    val w = s.takeWhile(!separators.contains(_)).mkString
    (s.drop(w.length), parseWord(w))
  }

  def isTrue(s: String): Boolean = s == "yes"
  def getBoolean (s: String): Bool = Bool(s, isTrue(s))

  def parseWord(s: String): Token = s match {
    case booleanPat(_)             => getBoolean(s)
    case stringPat(i)              => Identifier(i)
    case charPat(i)                => Identifier(i)
    case datePat(year, month, day) => Date(s, year.toInt, month.toInt, day.toInt)
    case numberPat(n)              => Number(s, BigDecimal(n))
    case identifierPat(i)          => Identifier(i)
    case _                         => InvalidIdentifier(s)
  }

  def nextToken: State[Chars, Token] = State[Chars, Token] { s =>
    {
      val upds = s.dropWhile(isWhiteSpace)

      def readBasedOnHead(c: Char) =
        if (operators.contains(c)) readOperator(upds)
        else if (brackets.contains(c)) readBracket(upds)
        else if (comments.keySet.contains(c)) readComment(upds)
        else readWord(upds)

      upds match {
        case c #:: _     => readBasedOnHead(c)
        case Stream.Empty => (upds, EOF)
      }
    }
  }

}