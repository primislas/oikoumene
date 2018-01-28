package com.lomicron.utils.parsing

import scalaz.State

object Tokenizer {
  type Chars = Stream[Char]
  type ParsingState = State[Chars, Token]
  
  val newLine = '\n'
  val comments = Map('#' -> '\n')
  val commentEnds = comments.values.toSet
  val whiteSpaces = Set(' ', '\t', '\n', '\r')
  val operators = Set('=')
  val brackets = Set('(', ')', '{', '}')
  val stringLiterals = ('\'', '\"')
  val separators = whiteSpaces ++ operators ++ brackets ++ comments.keySet

  val stringPat = """"([^\s]*)"""".r
  val charPat = """'([^\s]*)'""".r
  val identifierPat = """([a-zA-Z]+[_\da-zA-Z]*)""".r
  val numberPat = """(-*\d+(?:.\d*)?)""".r
  val datePat = """(\d+)\.(\d+)\.(\d+)""".r
  val booleanPat = """(yes|no)""".r
  
  def tokenize(s: String): Seq[Token] = tokenize(s.toStream)

  def tokenize(s: Stream[Char]): Seq[Token] = {
    @annotation.tailrec
    def parseAll(cs: Chars, ts: Vector[Token]): (Chars, Seq[Token]) = {
      val (ucs, t) = nextToken(cs)
      val uts: Vector[Token] = ts :+ t
      t match {
        case EOF => (ucs, uts)
        case _   => parseAll(ucs, uts)
      }
    }
    parseAll(s, Vector())._2
  }

  val isWhiteSpace: Char => Boolean = whiteSpaces.contains(_)

  val isNewLine: Char => Boolean = _ == newLine

  val readOperator: Chars => (Chars, Token) = s => s.headOption match {
    case Some('=') => (s.drop(1), Equals)
    case Some(c)   => throw new RuntimeException(s"Parsing exception: encountered unknown operator ${c}")
    case None      => throw new RuntimeException(s"Parsing exception: encountered an empty stream")
  }

  val readBracket: Chars => (Chars, Token) = s => s.headOption.getOrElse('0') match {
    case '{'     => (s.drop(1), OpenBrace)
    case '}'     => (s.drop(1), CloseBrace)
    case '('     => (s.drop(1), OpenParentheses)
    case ')'     => (s.drop(1), CloseParentheses)
    case default => throw new RuntimeException(s"Parsing exception: encountered unknown bracket ${default}")
  }

  val readComment: Stream[Char] => (Chars, Token) = s => {
    val notCommentEnd: Char => Boolean = !commentEnds.contains(_)
    val c = s.takeWhile(notCommentEnd).mkString;
    (s.drop(c.length), Comment(c.dropWhile(_ == '#').trim))
  }

  val readWord: Stream[Char] => (Chars, Token) = s => {
    val w = s.takeWhile(!separators.contains(_)).mkString;
    (s.drop(w.length), parseWord(w))
  }

  val isTrue = (s: String) => s == "yes"
  val getBoolean = (s: String) => if (isTrue(s)) Bool(s, true) else Bool(s, false)

  val parseWord: String => Token = s => s match {
    case booleanPat(_)             => getBoolean(s)
    case stringPat(i)              => Identifier(i)
    case charPat(i)                => Identifier(i)
    case datePat(year, month, day) => Date(s, year.toInt, month.toInt, day.toInt)
    case numberPat(n)              => Number(s, BigDecimal(n))
    case identifierPat(i)          => Identifier(i)
    case _                         => InvalidIdentifier(s)
  }

  val nextToken = State[Chars, Token] { s =>
    {
      var upds = s.dropWhile(isWhiteSpace)

      def readBasedOnHead(c: Char) =
        if (operators.contains(c)) readOperator(upds)
        else if (brackets.contains(c)) readBracket(upds)
        else if (comments.keySet.contains(c)) readComment(upds)
        else readWord(upds)

      upds match {
        case c #:: cs     => readBasedOnHead(c)
        case Stream.Empty => (upds, EOF)
      }
    }
  }

}