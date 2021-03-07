package com.lomicron.utils.parsing.tokenizer

import scalaz.State

import scala.util.matching.Regex

object Tokenizer {
  type Chars = LazyList[Char]
  type ParsingState = State[Chars, Token]

  val newLine = '\n'
  val comments = Map('#' -> '\n')
  val commentEnds: Set[Char] = comments.values.toSet
  val whiteSpaces = Set(' ', '\t', '\n', '\r')
  val operators = Set('=')
  val brackets = Set('(', ')', '{', '}', '[', ']')
  val stringLiterals = Set('\'', '\"')
  val separators: Set[Char] = whiteSpaces ++ operators ++ brackets ++ comments.keySet

  // TODO - remove escaped quotation marks from these patterns?
  val stringPat: Regex = """"(.*)"""".r
  val charPat: Regex = """'(.*)'""".r
  val identifierPat: Regex = """(\w+[_\d\w\u00C0-\u00FF]*)""".r
  val numberPat: Regex = """(-*\d+(?:.\d*)?)""".r
  val datePat: Regex = """(\d+)\.(\d+)\.(\d+)""".r
  val booleanPat: Regex = """(yes|no)""".r

  def tokenize(s: String): Seq[Token] = tokenize(s.to(LazyList))

  def tokenize(s: LazyList[Char]): Seq[Token] = {
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

  val readComment: LazyList[Char] => (Chars, Token) = s => {
    val notCommentEnd: Char => Boolean = !commentEnds.contains(_)
    val c = s.takeWhile(notCommentEnd).mkString
    (s.drop(c.length), Comment(c.dropWhile(_ == '#').trim))
  }

  val readWord: LazyList[Char] => (Chars, Token) = cs => {
    val word = StringAccumulator(cs).mkString
    (cs.drop(word.length), parseWord(word))
  }

  def isTrue(s: String): Boolean = s == "yes"
  def getBoolean (s: String): Bool = Bool(s, isTrue(s))

  def parseWord(s: String): Token = s match {
    case booleanPat(_)             => getBoolean(s)
    case stringPat(i)              => Identifier(i)
    //case charPat(i)                => Identifier(i)
    case datePat(year, month, day) => Date(s, year.toInt, month.toInt, day.toInt)
    case numberPat(n)              => Number(s, BigDecimal(n))
//    case identifierPat(i)          => Identifier(i)
//    case _                         => InvalidIdentifier(s)
    case _ => Identifier(s)
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
        case LazyList() => (upds, EOF)
      }
    }
  }

  class StringAccumulator(separator: Char) {

    private val cs = scala.collection.mutable.ArrayBuffer[Char]()
    private var opened = false
    private var closed = false

    def next(c: Char): Boolean = {
      if (closed) return false

      if (c == separator) {
        if (!opened) opened = true
        else closed = true
      }
      cs append c

      true
    }

    def chars: Chars = cs.to(LazyList)
  }

  object StringAccumulator {
    def apply(cs: Chars): Chars = {
//      if (cs.head == ''') {
//        val acc = new StringAccumulator(''')
//        cs.takeWhile(acc.next)
//      } else
      if (cs.head == '"') {
        val acc = new StringAccumulator('"')
        cs.takeWhile(acc.next)
      } else cs.takeWhile(!separators.contains(_))
    }
  }

}
