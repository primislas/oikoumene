package com.lomicron.utils.json

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.databind.node.{ArrayNode, BooleanNode, TextNode}
import com.lomicron.utils.io.IO
import org.specs2.mutable.Specification

class JsonMapperSpec extends Specification {

  "JsonMapper#fromJson" should {

    "- parse simple JSON" >> {
      val json = IO.readTextResource("json/simple.json")
      val fields = JsonMapper.fromJson[JsonFields](json)
      val cloned = JsonMapper.clone(fields)
      val reSerialized = JsonMapper.toJson(cloned)
      val expected = IO.readTextResource("json/simple-expected-result.json")
      reSerialized mustEqual expected
    }

    "- parse JSON setting default case class values" >> {
      val defVals = JsonMapper.fromJson[DefaultValuesTest]("{}")
      defVals.int mustEqual 0
      defVals.str mustEqual ""
      defVals.seq mustEqual Seq.empty
    }

  }

  "JsonMapper#toJsonNode" should {

    "- convert a string to TextNode" >> {
      val arg = "ordinary string"
      val jsonNode = JsonMapper.toJsonNode(arg)
      jsonNode must beAnInstanceOf[TextNode]
    }

    "- convert a Seq to ArrayNode" >> {
      val arg = Seq("12", 125, false)
      val jsonNode = JsonMapper.toJsonNode(arg)
      jsonNode must beAnInstanceOf[ArrayNode]

      val arrayNode = jsonNode.asInstanceOf[ArrayNode]
      arrayNode.get(2) must beAnInstanceOf[BooleanNode]
      arrayNode.size() mustEqual 3
    }
  }

}

case class JsonFields(
  string: String,
  integer: Int,
  decimal: BigDecimal,
  boolean: Boolean,
  nullField: String,
  emptyOption: Option[Int],
  intOption: Option[Int])

case class DefaultValuesTest
(int: Int = 0,
 str: String = "",
 seq: Seq[String] = Seq.empty) {

  // NOTE! Overriding default constructor with
  // @JsonCreator annotation is pretty much the only way
  // to make sure that case class default values are used
  // due to the way case classes are constructed by scala compiler -
  // i.e. not setting field values directly but rather
  // always through generated constructors.
  @JsonCreator def this() = this(0)

}