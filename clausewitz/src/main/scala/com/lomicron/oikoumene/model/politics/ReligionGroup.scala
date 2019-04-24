package com.lomicron.oikoumene.model.politics

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.oikoumene.model.Entity
import com.lomicron.oikoumene.model.localisation.Localisation
import com.lomicron.utils.json.FromJson
import com.lomicron.utils.json.JsonMapper.JsonMap

case class ReligionGroup
(// hits = 7, isOptional = false, sample = "christian"
 id: String = Entity.UNDEFINED,
 // hits = 7, isOptional = false, sample = "CRUSADE"
 crusadeName: String = Entity.UNDEFINED,
 // hits = 7, isOptional = false, sample = [1,57]
 flagEmblemIndexRange: Seq[Int] = Seq.empty,
 // hits = 7, isOptional = false, sample = {"name":"Christian"}
 localisation: Localisation = Localisation.empty,
 // hits = 7, isOptional = false, sample = ["anglican","reformed","catholic","protestant","orthodox","coptic"]
 religionIds: Seq[String] = Seq.empty,
 // hits = 6, isOptional = true, sample = 33
 flagsWithEmblemPercentage: Option[Int] = None,
 // hits = 6, isOptional = true, sample = "harmonized_christian"
 harmonizedModifier: Option[String] = None,
 // hits = 2, isOptional = true, sample = 118
 centerOfReligion: Option[Int] = None,
 // hits = 2, isOptional = true, sample = true
 defenderOfFaith: Option[Boolean] = None,
 // hits = 1, isOptional = true, sample = true
 aiWillPropagateThroughTrade: Option[Boolean] = None,
 // hits = 1, isOptional = true, sample = true
 canFormPersonalUnions: Option[Boolean] = None,
 // hits = 1, isOptional = true, sample = {"hanafi_school":{"potential_invite_scholar":{"knows_of_scholar_country_capital_trigger":true},"can_invite_scholar":{"adm_power":50,"if":{"limit":{"NOT":[{"religion":"sunni"},{"religion":"ibadi"}]},"NOT":{"piety":-0.5}},"reverse_has_opinion":{"who":"FROM","value":150},"NOT":{"has_country_modifier":"hanafi_scholar_modifier"},"OR":{"alliance_with":"FROM","is_subject_of":"FROM","overlord_of":"FROM"},"hidden_progressive_opinion_for_scholar_trigger":true},"on_invite_scholar":{"add_adm_power":-50,"clear_religious_scholar_modifiers_effect":true,"custom_tooltip":"INVITE_SCHOLAR_COUNTRY_TEXT","add_country_modifier":{"name":"hanafi_scholar_modifier","duration":7300}},"invite_scholar_modifier_display":"hanafi_scholar_modifier","picture":"GFX_icon_muslim_school_hanafi","adm_tech_cost_modifier":-0.05},"hanbali_school":{"potential_invite_scholar":{"knows_of_scholar_country_capital_trigger":true},"can_invite_scholar":{"adm_power":50,"if":{"limit":{"NOT":[{"religion":"sunni"},{"religion":"ibadi"}]},"NOT":{"piety":-0.5}},"reverse_has_opinion":{"who":"FROM","value":150},"OR":{"alliance_with":"FROM","is_subject_of":"FROM","overlord_of":"FROM"},"hidden_progressive_opinion_for_scholar_trigger":true,"NOT":{"has_country_modifier":"hanbali_scholar_modifier"}},"on_invite_scholar":{"add_adm_power":-50,"clear_religious_scholar_modifiers_effect":true,"custom_tooltip":"INVITE_SCHOLAR_COUNTRY_TEXT","add_country_modifier":{"name":"hanbali_scholar_modifier","duration":7300}},"invite_scholar_modifier_display":"hanbali_scholar_modifier","picture":"GFX_icon_muslim_school_hanbali","ae_impact":-0.1},"maliki_school":{"potential_invite_scholar":{"knows_of_scholar_country_capital_trigger":true},"can_invite_scholar":{"adm_power":50,"if":{"limit":{"NOT":[{"religion":"sunni"},{"religion":"ibadi"}]},"NOT":{"piety":-0.5}},"reverse_has_opinion":{"who":"FROM","value":150},"OR":{"alliance_with":"FROM","is_subject_of":"FROM","overlord_of":"FROM"},"hidden_progressive_opinion_for_scholar_trigger":true,"NOT":{"has_country_modifier":"maliki_scholar_modifier"}},"on_invite_scholar":{"add_adm_power":-50,"clear_religious_scholar_modifiers_effect":true,"custom_tooltip":"INVITE_SCHOLAR_COUNTRY_TEXT","add_country_modifier":{"name":"maliki_scholar_modifier","duration":7300}},"invite_scholar_modifier_display":"maliki_scholar_modifier","picture":"GFX_icon_muslim_school_maliki","development_cost":-0.1},"shafii_school":{"potential_invite_scholar":{"knows_of_scholar_country_capital_trigger":true},"can_invite_scholar":{"adm_power":50,"if":{"limit":{"NOT":[{"religion":"sunni"},{"religion":"ibadi"}]},"NOT":{"piety":-0.5}},"reverse_has_opinion":{"who":"FROM","value":150},"OR":{"alliance_with":"FROM","is_subject_of":"FROM","overlord_of":"FROM"},"hidden_progressive_opinion_for_scholar_trigger":true,"NOT":{"has_country_modifier":"shafii_scholar_modifier"}},"on_invite_scholar":{"add_adm_power":-50,"clear_religious_scholar_modifiers_effect":true,"custom_tooltip":"INVITE_SCHOLAR_COUNTRY_TEXT","add_country_modifier":{"name":"shafii_scholar_modifier","duration":7300}},"invite_scholar_modifier_display":"shafii_scholar_modifier","picture":"GFX_icon_muslim_school_shafii","merchants":1},"ismaili_school":{"potential_invite_scholar":{"knows_of_scholar_country_capital_trigger":true},"can_invite_scholar":{"adm_power":50,"if":{"limit":{"NOT":[{"religion":"shiite"},{"religion":"ibadi"}]},"NOT":{"piety":-0.5}},"reverse_has_opinion":{"who":"FROM","value":150},"OR":{"alliance_with":"FROM","is_subject_of":"FROM","overlord_of":"FROM"},"hidden_progressive_opinion_for_scholar_trigger":true,"NOT":{"has_country_modifier":"ismaili_scholar_modifier"}},"on_invite_scholar":{"add_adm_power":-50,"clear_religious_scholar_modifiers_effect":true,"custom_tooltip":"INVITE_SCHOLAR_COUNTRY_TEXT","add_country_modifier":{"name":"ismaili_scholar_modifier","duration":7300}},"invite_scholar_modifier_display":"ismaili_scholar_modifier","picture":"GFX_icon_muslim_school_ismaili","horde_unity":1,"legitimacy":1,"republican_tradition":0.5,"devotion":1},"jafari_school":{"potential_invite_scholar":{"knows_of_scholar_country_capital_trigger":true},"can_invite_scholar":{"adm_power":50,"if":{"limit":{"NOT":[{"religion":"shiite"},{"religion":"ibadi"}]},"NOT":{"piety":-0.5}},"reverse_has_opinion":{"who":"FROM","value":150},"OR":{"alliance_with":"FROM","is_subject_of":"FROM","overlord_of":"FROM"},"hidden_progressive_opinion_for_scholar_trigger":true,"NOT":{"has_country_modifier":"jafari_scholar_modifier"}},"on_invite_scholar":{"add_adm_power":-50,"clear_religious_scholar_modifiers_effect":true,"custom_tooltip":"INVITE_SCHOLAR_COUNTRY_TEXT","add_country_modifier":{"name":"jafari_scholar_modifier","duration":7300}},"invite_scholar_modifier_display":"jafari_scholar_modifier","picture":"GFX_icon_muslim_school_jafari","shock_damage":0.1},"zaidi_school":{"potential_invite_scholar":{"knows_of_scholar_country_capital_trigger":true},"can_invite_scholar":{"adm_power":50,"if":{"limit":{"NOT":[{"religion":"shiite"},{"religion":"ibadi"}]},"NOT":{"piety":-0.5}},"reverse_has_opinion":{"who":"FROM","value":150},"OR":{"alliance_with":"FROM","is_subject_of":"FROM","overlord_of":"FROM"},"hidden_progressive_opinion_for_scholar_trigger":true,"NOT":{"has_country_modifier":"zaidi_scholar_modifier"}},"on_invite_scholar":{"add_adm_power":-50,"clear_religious_scholar_modifiers_effect":true,"custom_tooltip":"INVITE_SCHOLAR_COUNTRY_TEXT","add_country_modifier":{"name":"zaidi_scholar_modifier","duration":7300}},"invite_scholar_modifier_display":"zaidi_scholar_modifier","picture":"GFX_icon_muslim_school_zaidi","shock_damage_received":-0.1}}
 religiousSchools: Option[JsonMap] = None,
) extends Entity {
  @JsonCreator def this() = this(Entity.UNDEFINED)
}

object ReligionGroup extends FromJson[ReligionGroup]
