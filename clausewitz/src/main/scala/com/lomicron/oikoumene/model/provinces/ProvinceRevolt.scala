package com.lomicron.oikoumene.model.provinces

import com.fasterxml.jackson.annotation.JsonCreator

case class ProvinceRevolt
(`type`: Option[String] = None,
 name: Option[String] = None,
 size: Option[Int] = None,
 // TODO check if it's optional or required
 leader: Option[String] = None) {

  @JsonCreator def this() = this(None)

  def isEmpty: Boolean = `type`.isEmpty

}