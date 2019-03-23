package com.lomicron.utils.json

import com.fasterxml.jackson.databind.JsonNode

trait FromJson[T] {

  def fromJson(json: String)(implicit m: Manifest[T]): T =
    JsonMapper.fromJson[T](json)

  def fromJson(json: JsonNode)(implicit m: Manifest[T]): T =
    fromJson(JsonMapper.toJson(json))

}
