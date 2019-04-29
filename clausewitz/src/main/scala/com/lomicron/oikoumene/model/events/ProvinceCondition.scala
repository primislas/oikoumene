package com.lomicron.oikoumene.model.events

import com.fasterxml.jackson.annotation.JsonCreator

class ProvinceCondition
(
  not: Option[ProvinceCondition] = None,
  or: Option[ProvinceCondition] = None,
  and: Option[ProvinceCondition] = None,

  // hits = 6, isOptional = true, sample = "ROOT"
  isClaim: Option[String] = None,
  // hits = 4, isOptional = true, sample = "ROOT"
  isCore: Option[String] = None,
  // hits = 4, isOptional = true, sample = {"is_excommunicated":true,"religion":"catholic"}
  owner: Option[TagCondition] = None,
  // hits = 3, isOptional = true, sample = true
  always: Option[Boolean] = None,
  // hits = 3, isOptional = true, sample = {"owned_by":"ROOT"}
  anyNeighborProvince: Option[ProvinceCondition] = None,
  // hits = 2, isOptional = true, sample = true
  isPartOfHre: Option[Boolean] = None,
  // hits = 1, isOptional = true, sample = "unlawful_territory"
  hasProvinceModifier: Option[String] = None,
  // hits = 1, isOptional = true, sample = false
  isCapital: Option[Boolean] = None,
  // hits = 1, isOptional = true, sample = 12
  monthsSinceDefection: Option[Int] = None,
  // hits = 1, isOptional = true, sample = "ROOT"
  previousOwner: Option[String] = None,

) {
  @JsonCreator def this() = this(None)
}
