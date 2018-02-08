package com.lomicron.utils.parsing

import org.specs2.mutable.Specification
import com.lomicron.utils.io.IO

class ParserSpec extends Specification {
  val provinceFile = "151 - Constantinople.txt"

  "JsonParser#parse" should {
    "" >> {
      val content = IO.readTextResource(provinceFile)
      val tokens = Tokenizer.tokenize(content.toStream)
      val node = JsonParser.parse(tokens)._1
      val json = node.toString
      json.length must be_>(0)
    }
  }

}