package com.lomicron.utils.collection

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.{JsonNodeType, ObjectNode}
import com.lomicron.utils.collection.CollectionUtils._
import com.lomicron.utils.json.JsonMapper
import org.specs2.mutable.Specification

class CollectionUtilsSpec extends Specification {

  "OptionEx#cast" should {

    "correctly cast an object that matches expected type" >> {
      val jsonNode: JsonNode = JsonMapper.objectNode
      val objOpt = Option(jsonNode).cast[ObjectNode]
      objOpt.isEmpty mustEqual false
      objOpt.get.getNodeType must be equalTo JsonNodeType.OBJECT
    }

    "return an empty option if contained object does not match expected type" >> {
      val str = "string"
      val o = Option(str).cast[Int]
      o.isEmpty mustEqual true
    }


  }

}
