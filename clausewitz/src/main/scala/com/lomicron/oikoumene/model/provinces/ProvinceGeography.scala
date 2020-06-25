package com.lomicron.oikoumene.model.provinces

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.oikoumene.model.map.ProvincePositions
import com.lomicron.utils.json.FromJson

case class ProvinceGeography
(
`type`: Option[String] = None,
terrain: Option[String] = None,
climate: Seq[String] = Seq.empty,
area: Option[String] = None,
region: Option[String] = None,
superRegion: Option[String] = None,
continent: Option[String] = None,
adjacencies: Set[Int] = Set.empty,
crossings: Set[Int] = Set.empty,
landlocked: Boolean = true,
tradeNode: Option[String] = None,
positions: Option[ProvincePositions] = None,
) {
  @JsonCreator def this() = this(None)
  def isImpassable: Boolean = climate.contains("impassable")
  def provinceType: String = `type`.getOrElse(ProvinceTypes.wasteland)
}

object ProvinceGeography extends FromJson[ProvinceGeography] {
  val empty: ProvinceGeography = ProvinceGeography()
}
