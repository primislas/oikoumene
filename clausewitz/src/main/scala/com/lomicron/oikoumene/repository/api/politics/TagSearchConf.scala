package com.lomicron.oikoumene.repository.api.politics

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.oikoumene.repository.api.SearchConf

case class TagSearchConf
(
  override val page: Int = 0,
  override val size: Int = 10,
  override val withDictionary: Boolean = false,

  id: Option[String] = None,
  name: Option[String] = None,
  primaryCulture: Option[String] = None,
  religion: Option[String] = None,

) extends SearchConf {
  @JsonCreator def this() = this(0)
}

object TagSearchConf {
  def empty: TagSearchConf = new TagSearchConf()
}
