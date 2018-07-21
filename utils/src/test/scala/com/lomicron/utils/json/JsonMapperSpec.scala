package com.lomicron.utils.json

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.{ArrayNode, BooleanNode, TextNode}
import org.specs2.mutable.Specification
import com.lomicron.utils.io.IO

class JsonMapperSpec extends Specification {

  "JsonMapper#fromJson" should {

    "- parse simple JSON" >> {
      val json = IO.readTextResource("json/simple.json")
      val fields = JsonMapper.fromJson[JsonFields](json)
      val cloned = JsonMapper.clone(fields)
      val reserialized = JsonMapper.toJson(cloned)
      reserialized must have size 1414
    }

  }

  "JsonMapper.toJsonNode()" should {

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