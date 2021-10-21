package com.lomicron.utils.parsing.tokenizer

import com.lomicron.utils.parsing.tokenizer.Tokenizer._
import org.specs2.mutable.Specification

class TokenizerSpec extends Specification {

  "Tokenizer#isWhiteSpace" should {
    "- return true for white space characters" >> {
      val whitespaces = whiteSpaces.map(isWhiteSpace).reduce(_ && _)
      whitespaces must_== true
    }

    "- return false for non white space characters" >> {
      val nonWhiteSpaces = List('a', '0', 'Z', '(', '-', '.')
      val result = nonWhiteSpaces.map(isWhiteSpace).reduce(_ && _)
      result must_== false
    }
  }

  "Tokenizer#readComment" should {
    val validInput = "###this is a comment string\n".to(LazyList)

    "- return a comment token from a stream" >> {
      val (_, comment) = Tokenizer.readComment(validInput)
      comment.isInstanceOf[Comment] must_== true
    }

    "- return a trimmed string with no leading '#'" >> {
      val (_, comment) = Tokenizer.readComment(validInput)
      val str = comment match {
        case Comment(s) => s
        case _ => ""
      }
      str must beEqualTo(validInput.dropWhile(_ == '#').mkString.trim)
    }

    "- return a stream that follows the comment token" >> {
      val (stream, _) = Tokenizer.readComment(validInput)
      stream.head must beEqualTo('\n')
    }

    "- verify that brackets are balanced" >> {
      val brackets = "([{}[]])"
      def bracketsMatch(a: Char, b: Char): Boolean =
        (a == '(' && b == ')') || (a == '{' && b == '}') || (a == '[' && b == ']')

      val res = brackets
        .foldLeft(List.empty[Char])((stack, c) => c match {
          case ob @ ('(' | '[' | '{') => ob :: stack
          case cb @ (')' | ']' | '}') => stack match {
            case br :: tail => if (bracketsMatch(br, cb)) tail else stack
            case Nil => cb :: stack
          }
        })

      res.length mustEqual(0)
    }
  }

}
