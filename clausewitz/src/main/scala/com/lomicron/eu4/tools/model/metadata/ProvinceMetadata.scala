package com.lomicron.eu4.tools.model.metadata

import com.lomicron.eu4.model.provinces.Province
import com.lomicron.oikoumene.model.Color

import scala.collection.immutable.ListSet

case class ProvinceMetadata
(
  id: Int,
  color: Color,
  name: Option[String],

  tax: Int = 0,
  production: Int = 0,
  manpower: Int = 0,
  isCity: Boolean = false,

  tradeGood: Option[String] = None,
  culture: Option[String] = None,
  cultureGroup: Option[String] = None,
  religion: Option[String] = None,
  religionGroup: Option[String] = None,

  owner: Option[String] = None,
  controller: Option[String] = None,
  cores: Set[String] = ListSet.empty,
  claims: Set[String] = ListSet.empty,

  `type`: Option[String] = None,
  terrain: Option[String] = None,
  climate: Seq[String] = Seq.empty,
  area: Option[String] = None,
  region: Option[String] = None,
  superRegion: Option[String] = None,
  continent: Option[String] = None,
  adjacencies: Set[Int] = Set.empty,
  crossings: Set[Int] = Set.empty,
  tradeNode: Option[String] = None,

)

object ProvinceMetadata {
  def apply(p: Province): ProvinceMetadata =
    ProvinceMetadata(
      p.id,
      p.color,
      p.localisation.name,

      p.state.baseTax,
      p.state.baseProduction,
      p.state.baseManpower,
      p.state.isCity,

      p.state.tradeGood,
      p.state.culture,
      p.state.cultureGroup,
      p.state.religion,
      p.state.religionGroup,

      p.state.owner,
      p.state.controller,
      p.state.cores,
      p.state.claims,

      p.geography.`type`,
      p.geography.terrain,
      p.geography.climate,
      p.geography.area,
      p.geography.region,
      p.geography.superRegion,
      p.geography.continent,
      p.geography.adjacencies,
      p.geography.crossings,
      p.geography.tradeNode,
    )
}
