package com.lomicron.utils.parsing.scopes

import com.lomicron.utils.parsing.Token

case class ParsingError(path: Seq[String], expected: Seq[String], encountered: Token) {
  override def toString: String = {
    val sb = new StringBuilder("Parsing error at: ")

    @annotation.tailrec
    def rec(ss: Seq[String], sep: String): Unit = ss match {
      case h :: Nil => sb.append(h)
      case h :: t =>
        sb.append(h).append(sep)
        rec(t, sep)
      case _ => ()
    }

    rec(path, " => ")
    sb.append(". Expected: ")
    rec(expected, ", ")
    sb.append(". Encountered: ").append(encountered)
    sb.toString
  }
}
