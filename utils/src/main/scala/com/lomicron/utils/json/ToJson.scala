package com.lomicron.utils.json

trait ToJson {
  def toJson: String = JsonMapper.toJson(this)
}
