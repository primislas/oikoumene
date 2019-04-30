package com.lomicron.oikoumene.model.diplomacy

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.oikoumene.model.Entity
import com.lomicron.oikoumene.model.events.{ProvinceCondition, TagCondition}
import com.lomicron.oikoumene.model.localisation.Localisation
import com.lomicron.utils.json.FromJson

case class CasusBelli
(
  // hits = 59, isOptional = false, sample = "cb_restore_personal_union"
  id: String = Entity.UNDEFINED,
  // hits = 59, isOptional = false, sample = {"name":"Restoration of Union"}
  localisation: Localisation = Localisation.empty,
  // hits = 59, isOptional = false, sample = "00_cb_types.txt"
  sourceFile: String = Entity.UNDEFINED,
  // hits = 59, isOptional = false, sample = "take_capital_personal_union"
  warGoal: String = Entity.UNDEFINED,
  // hits = 47, isOptional = true, sample = false
  validForSubject: Boolean = true,
  // hits = 43, isOptional = true, sample = {"OR":{"government":"monarchy","has_reform":"dutch_republic"},"is_revolution_target":false,"FROM":{"government":"monarchy","is_subject":false}}
  prerequisites: Option[TagCondition] = None,
  // hits = 19, isOptional = true, sample = 240
  months: Option[Int] = None,
  // hits = 18, isOptional = true, sample = true
  isTriggeredOnly: Boolean = false,
  // hits = 6, isOptional = true, sample = ["po_demand_provinces","po_revoke_cores","po_release_vassals","po_release_annexed","po_return_cores","po_become_vassal","po_become_tributary_state","po_form_personal_union","po_transfer_vassals"]
  attackerDisabledPo: Seq[String] = Seq.empty,
  // hits = 4, isOptional = true, sample = true
  exclusive: Boolean = false,
  // hits = 3, isOptional = true, sample = true
  independence: Boolean = false,
  // hits = 2, isOptional = true, sample = -50
  aiPeaceDesire: Option[BigDecimal] = None,
  // hits = 1, isOptional = true, sample = {"always":true}
  allowedProvinces: Option[ProvinceCondition] = None,
  // hits = 1, isOptional = true, sample = true
  coalition: Boolean = false,
  // hits = 1, isOptional = true, sample = true
  league: Boolean = false,
  // hits = 1, isOptional = true, sample = true
  noOpinionHit: Boolean = false,
  // hits = 1, isOptional = true, sample = true
  supportRebels: Boolean = false,
) extends Entity {
  @JsonCreator def this() = this(Entity.UNDEFINED)
}

object CasusBelli extends FromJson[CasusBelli]