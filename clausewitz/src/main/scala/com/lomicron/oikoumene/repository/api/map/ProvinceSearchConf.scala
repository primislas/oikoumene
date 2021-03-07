package com.lomicron.oikoumene.repository.api.map

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.oikoumene.repository.api.search.SearchConf

@JsonCreator
case class ProvinceSearchConf
(
  override val page: Int = 0,
  override val size: Int = 10,
  override val withDictionary: Boolean = false,

  name: Option[String] = None,

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
  def ofName(name: String): ProvinceSearchConf = copy(name = Option(name))
  def ofOwner(tag: String): ProvinceSearchConf = copy(owner = Option(tag))
  def ofCulture(culture: String): ProvinceSearchConf = copy(culture = Option(culture))
  def ofCultureGroup(cultureGroup: String): ProvinceSearchConf = copy(cultureGroup = Option(cultureGroup))
  def ofArea(area: String): ProvinceSearchConf = copy(area = Option(area))
  def ofRegion(region: String): ProvinceSearchConf = copy(region = Option(region))
  def withSize(size: Int): ProvinceSearchConf = copy(size = size)
  def all: ProvinceSearchConf = withSize(Int.MaxValue)
}

object ProvinceSearchConf {
  private val EMPTY = ProvinceSearchConf()
  def empty: ProvinceSearchConf = EMPTY
  def ofName(name: String): ProvinceSearchConf = EMPTY.ofName(name)
  def ofOwner(tag: String): ProvinceSearchConf = EMPTY.ofOwner(tag)
  def ofCulture(culture: String): ProvinceSearchConf = EMPTY.ofCulture(culture)
  def ofCultureGroup(cultureGroup: String): ProvinceSearchConf = EMPTY.ofCultureGroup(cultureGroup)
  def ofArea(area: String): ProvinceSearchConf = EMPTY.ofArea(area)
  def ofRegion(region: String): ProvinceSearchConf = EMPTY.ofRegion(region)

}
