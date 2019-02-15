package com.lomicron.utils.parsing.scopes

import com.lomicron.utils.parsing.tokenizer.Token

case class ParsingError(message: String) {
  override def toString: String = message
}

object ParsingError {
  def apply(path: Seq[String], expected: Seq[String], encountered: Token): ParsingError  = {
    val sb = new StringBuilder("Parsing error at: ")

    @annotation.tailrec
    def rec(ss: Seq[String], sep: String): Unit = ss match {
      case h :: Nil => sb.append(h)
      case h :: t =>
        sb.append(h).append(sep)
        rec(t, sep)
      case _ => ()
    }

    rec(path, " -> ")
    sb.append(". Expected: [ ")
    rec(expected, ", ")
    sb.append(" ]. Encountered: ").append(encountered)
    val msg = sb.toString
    ParsingError(msg)
  }

}
