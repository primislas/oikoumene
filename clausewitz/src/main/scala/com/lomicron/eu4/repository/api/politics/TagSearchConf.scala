package com.lomicron.eu4.repository.api.politics

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.oikoumene.repository.api.search.SearchConf

@JsonCreator
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
  def ofTag(tag: String): TagSearchConf = copy(id = Some(tag))
  def ofName(name: String): TagSearchConf = copy(name = Option(name))
}

object TagSearchConf {
  private val EMPTY = new TagSearchConf()
  def empty: TagSearchConf = EMPTY
  def ofTag(tag: String): TagSearchConf = EMPTY.ofTag(tag)
  def ofName(name: String): TagSearchConf = EMPTY.ofName(name)
}
