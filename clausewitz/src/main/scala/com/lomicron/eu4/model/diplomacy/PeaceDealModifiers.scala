package com.lomicron.eu4.model.diplomacy

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.eu4.model.events.ProvinceCondition
import com.lomicron.utils.json.FromJson

@JsonCreator
case class PeaceDealModifiers
(
  // hits = 44, isOptional = false, sample = ["po_become_vassal","po_demand_provinces","po_revoke_cores","po_release_vassals","po_release_annexed","po_change_religion","po_gold","po_annul_treaties"]
  peaceOptions: Seq[String] = Seq.empty,
  // hits = 42, isOptional = true, sample = 0.5
  badboyFactor: Option[BigDecimal] = None,
  // hits = 42, isOptional = true, sample = 0.5
  peaceCostFactor: Option[BigDecimal] = None,
  // hits = 42, isOptional = true, sample = 1.5
  prestigeFactor: Option[BigDecimal] = None,
  // hits = 29, isOptional = true, sample = {"is_core":"ROOT"}
  allowedProvinces: Option[ProvinceCondition] = None,
  // hits = 26, isOptional = true, sample = "ALL_CORES"
  provDesc: Option[String] = None,
  // hits = 16, isOptional = true, sample = "ALL_CGROUP_COUNTRIES"
  countryDesc: Option[String] = None,
)

object PeaceDealModifiers extends FromJson[PeaceDealModifiers]
