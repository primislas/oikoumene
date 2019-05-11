package com.lomicron.oikoumene.repository.api

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.utils.json.FromJson

import scala.collection.immutable.SortedMap

case class SearchDictionary
(
  tag: SortedMap[String, String] = SortedMap.empty,
  province: SortedMap[String, String] = SortedMap.empty,
  tradeGood: SortedMap[String, String] = SortedMap.empty,

  religion: SortedMap[String, String] = SortedMap.empty,
  religionGroup: SortedMap[String, String] = SortedMap.empty,
  culture: SortedMap[String, String] = SortedMap.empty,
  cultureGroup: SortedMap[String, String] = SortedMap.empty,

  climate: SortedMap[String, String] = SortedMap.empty,
  terrain: SortedMap[String, String] = SortedMap.empty,
  area: SortedMap[String, String] = SortedMap.empty,
  region: SortedMap[String, String] = SortedMap.empty,
  superRegion: SortedMap[String, String] = SortedMap.empty,
  continent: SortedMap[String, String] = SortedMap.empty,
) {
  @JsonCreator def this() = this(SortedMap.empty)
}

object SearchDictionary extends FromJson[SearchDictionary] {
  val empty = SearchDictionary()
}
