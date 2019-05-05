package com.lomicron.oikoumene.model.provinces

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.utils.json.FromJson

case class ProvinceGeography
(
`type`: Option[String] = None,
terrain: Option[String] = None,
climate: Option[String] = None,
area: Option[String] = None,
region: Option[String] = None,
superRegion: Option[String] = None,
continent: Option[String] = None,
adjacencies: Set[Int] = Set.empty,
crossings: Set[Int] = Set.empty,
landlocked: Boolean = true,
tradeNode: Option[String] = None,
) {
  @JsonCreator def this() = this(None)
}

object ProvinceGeography extends FromJson[ProvinceGeography] {
  val empty = ProvinceGeography()
}