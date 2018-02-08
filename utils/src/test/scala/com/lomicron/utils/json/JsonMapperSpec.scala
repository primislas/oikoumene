package com.lomicron.utils.json

import org.specs2.mutable.Specification
import com.lomicron.utils.io.IO

class JsonMapperSpec extends Specification {

  "JsonMapper#fromJson" should {

    "parse simple JSON" >> {
      val json = IO.readTextResource("json/simple.json")
      val fields = JsonMapper.fromJson[JsonFields](json)
      val cloned = JsonMapper.clone(fields)
      val reserialized = JsonMapper.toJson(cloned)
      println(reserialized)
      reserialized must have size 1414
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