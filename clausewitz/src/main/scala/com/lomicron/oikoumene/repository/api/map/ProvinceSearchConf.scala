package com.lomicron.oikoumene.repository.api.map

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.oikoumene.repository.api.SearchConf

case class ProvinceSearchConf
(
  override val page: Int = 0,
  override val size: Int = 10,
  override val withDictionary: Boolean = false,

  owner: Option[String] = None,
  controller: Option[String] = None,
  core: Option[String] = None,

  religion: Option[String] = None,
  religionGroup: Option[String] = None,
  culture: Option[String] = None,
  cultureGroup: Option[String] = None,

  area: Option[String] = None,
  region: Option[String] = None,
  superRegion: Option[String] = None,
  continent: Option[String] = None,

  tradeGood: Option[String] = None,
  tradeNode: Option[String] = None,

  /**
    * If set, only province fields listed here
    * will be included in returned provinces.
    */
  includeFields: Set[String] = Set.empty,
  /**
    * If set, all province fields will be included
    * with the exception pf excluded fields.
    */
  excludeFields: Set[String] = Set.empty,
) extends SearchConf {
  @JsonCreator def this() = this(0)
}

object ProvinceSearchConf {
  def empty: ProvinceSearchConf = ProvinceSearchConf()
}
