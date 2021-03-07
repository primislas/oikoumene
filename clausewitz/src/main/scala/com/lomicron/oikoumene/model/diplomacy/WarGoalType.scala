package com.lomicron.oikoumene.model.diplomacy

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.oikoumene.model.Entity
import com.lomicron.oikoumene.model.events.{ProvinceCondition, TagCondition}
import com.lomicron.oikoumene.model.localisation.Localisation
import com.lomicron.utils.json.FromJson

@JsonCreator
case class WarGoalType
(
  // hits = 57, isOptional = false, sample = "fallback_wargoal"
  id: String = Entity.UNDEFINED,
  // hits = 49, isOptional = true, sample = {"name":"Revoke Elector"}
  localisation: Localisation = Localisation.empty,
  // hits = 57, isOptional = false, sample = "00_wargoal_types.txt"
  sourceFile: String = Entity.UNDEFINED,
  // hits = 57, isOptional = false, sample = "superiority"
  `type`: String = Entity.UNDEFINED,
  // hits = 54, isOptional = true, sample = "REVOKE_ELECTOR_WAR_NAME"
  warName: Option[String] = None,
  // hits = 36, isOptional = true, sample = 1
  badboyFactor: Option[BigDecimal] = None,
  // hits = 36, isOptional = true, sample = 0.75
  peaceCostFactor: Option[BigDecimal] = None,
  // hits = 36, isOptional = true, sample = 1
  prestigeFactor: Option[BigDecimal] = None,
  // hits = 34, isOptional = true, sample = ["po_gold","po_concede_defeat","po_trade_power","po_steer_trade"]
  peaceOptions: Seq[String] = Seq.empty,
  // hits = 23, isOptional = true, sample = {"badboy_factor":0.5,"prestige_factor":1.5,"peace_cost_factor":0.5,"peace_options":["po_become_vassal","po_demand_provinces","po_revoke_cores","po_release_vassals","po_release_annexed","po_change_religion","po_gold","po_annul_treaties"]}
  attacker: Option[PeaceDealModifiers] = None,
  // hits = 21, isOptional = true, sample = {"badboy_factor":0.5,"prestige_factor":1.5,"peace_cost_factor":0.5,"peace_options":["po_become_vassal","po_demand_provinces","po_revoke_cores","po_release_vassals","po_release_annexed","po_change_religion","po_gold","po_annul_treaties","po_dismantle_revolution"]}
  defender: Option[PeaceDealModifiers] = None,
  // hits = 17, isOptional = true, sample = {"NOT":{"months_since_defection":12},"previous_owner":"ROOT"}
  allowedProvinces: Option[ProvinceCondition] = None,
  // hits = 12, isOptional = true, sample = "ALL_CLAIMS"
  provDesc: Option[String] = None,
  // hits = 7, isOptional = true, sample = "ALL_OPPRESSORS"
  countryDesc: Option[String] = None,
  // hits = 3, isOptional = true, sample = true
  denyAnnex: Boolean = false,
  // hits = 2, isOptional = true, sample = true
  allowedProvincesAreEligible: Boolean = false,
  // hits = 1, isOptional = true, sample = true
  allowAnnex: Boolean = false,
  // hits = 1, isOptional = true, sample = {"religion":"ROOT"}
  allowedCountries: Option[TagCondition] = None,
  // hits = 1, isOptional = true, sample = "revoke_elector"
  electorRelation: Option[String] = None,
  // hits = 1, isOptional = true, sample = 0.5
  transferTradeCostFactor: Option[BigDecimal] = None,
) extends Entity

object WarGoalType extends FromJson[WarGoalType]
