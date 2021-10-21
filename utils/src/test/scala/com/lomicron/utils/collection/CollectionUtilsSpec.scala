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

  "OptionEx#contentsEqual" should {

    "return true if non-empty Options carry logically equal contents" >> {
      val int1 = Option(123456789)
      val int2 = Option(123456789)
      int1.contentsEqual(int2) mustEqual true
    }

    "return false if Option contents are not logically equal" >> {
      val o1: Option[Any] = Some(false)
      val o2: Option[Any] = Some("str")
      o1.contentsEqual(o2) mustEqual false
    }

    "return false if both Options are empty" >> {
      Option.empty.contentsEqual(Option.empty) mustEqual false
    }

    "return false if one Option is empty and the other is not" >> {
      val eo: Option[Any] = Option.empty
      val into: Option[Any] = Some(4)
      val stro: Option[Any] = Some("str")
      eo.contentsEqual(into) mustEqual false
      stro.contentsEqual(eo) mustEqual false
    }

  }

}
