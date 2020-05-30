package com.lomicron.oikoumene.serializers

import com.fasterxml.jackson.databind.node.BooleanNode
import com.lomicron.oikoumene.model.provinces.{ProvinceHistory, ProvinceUpdate}
import com.lomicron.utils.parsing.tokenizer.Date
import org.specs2.mutable.Specification

class ClausewitzSerializerSpec extends Specification {

  "ClausewitzSerializer" should {

    "serialize any object to clausewitz-like format" in {
      val filename = "1 - Kyiv.txt"

      val init = ProvinceUpdate(owner = Some("LIT"))
      val events = Seq(ProvinceUpdate(owner = Some("KYI"), date = Some(Date(1490, 1, 1))))
      val provHistory = ProvinceHistory(init, events, sourceFile = Some(filename))

      val strs = ClausewitzSerializer.serializeHistory(provHistory)
      val bracketParity = strs.foldLeft(0)((acc, c) => if (c == '{') acc + 1 else if (c == '}') acc - 1 else acc)

      strs must not be empty
      strs must contain("owner = LIT")
      strs must contain("1490.1.1 = {")
      strs must contain("\towner = KYI")
      bracketParity mustEqual 0
    }

  }

  "ClausewitzSerializer#wrapInQuotesIfWithWhitespace" should {

    def verifyDoubleQuotes(s: String) = {
      val serialized = ClausewitzSerializer.wrapInQuotesIfWithWhitespace(s)
      serialized.length must beGreaterThan(1)
      serialized must startWith("\"")
      serialized must endWith("\"")
    }

    "wrap with double quotes strings with spaces" in {
      verifyDoubleQuotes("File name with spaces.txt")
    }

    "wrap with double quotes strings with tabs" in {
      verifyDoubleQuotes("File\tname\twith\ttabs.txt")
    }

  }

  "ClausewitzSerializer#nodeToText" should {

    "return boolean true as 'yes'" in {
      val bTrue = BooleanNode.TRUE
      val serialized = ClausewitzSerializer.nodeToText(bTrue)
      serialized must beSome("yes")
    }

    "return boolean false as 'no'" in {
      val bFalse = BooleanNode.FALSE
      val serialized = ClausewitzSerializer.nodeToText(bFalse)
      serialized must beSome("no")
    }

  }

}
