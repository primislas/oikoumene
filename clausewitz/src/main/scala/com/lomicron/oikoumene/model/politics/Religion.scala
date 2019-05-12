package com.lomicron.oikoumene.model.politics

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.oikoumene.model.{Color, Entity}
import com.lomicron.oikoumene.model.localisation.Localisation
import com.lomicron.utils.json.FromJson
import com.lomicron.utils.json.JsonMapper.JsonMap

case class Religion
(// hits = 26, isOptional = false, sample = "anglican"
 id: String = Entity.UNDEFINED,
 // hits = 26, isOptional = false, sample = {"name":"Anglican"}
 localisation: Localisation = Localisation.empty,
 // hits = 26, isOptional = false, sample = "christian"
 religionGroup: String = Entity.UNDEFINED,
 // hits = 26, isOptional = false, sample = [135,77,255]
 color: Color = Color.black,
 // hits = 26, isOptional = false, sample = {"development_cost":-0.1,"innovativeness_gain":0.5}
 country: JsonMap = Map.empty,
 // hits = 26, isOptional = false, sample = ["PENTECOSTAL","PURITAN","CONGREGATIONALIST"]
 heretic: Seq[String] = Seq.empty,
 // hits = 26, isOptional = false, sample = 28
 icon: Int = 0,
 // hits = 25, isOptional = true, sample = {"idea_cost":-0.1}
 countryAsSecondary: Option[JsonMap] = None,
 // hits = 16, isOptional = true, sample = {"local_missionary_strength":0.02}
 province: Option[JsonMap] = None,
 // hits = 8, isOptional = true, sample = ["vajrayana","mahayana"]
 allowedConversion: Seq[String] = Seq.empty,
 // hits = 8, isOptional = true, sample = {"add_prestige":-100,"add_stability":-1,"add_country_modifier":{"name":"conversion_zeal","duration":3650}}
 onConvert: Option[JsonMap] = None,
 // hits = 4, isOptional = true, sample = "1534.11.3"
 date: Option[String] = None,
 // hits = 4, isOptional = true, sample = "harmonized_buddhism"
 harmonizedModifier: Option[String] = None,
 // hits = 3, isOptional = true, sample = ["catholic"]
 allowedCenterConversion: Seq[String] = Seq.empty,
 // hits = 3, isOptional = true, sample = "INTI_REFORM_TOOLTIP"
 reformTooltip: Option[String] = None,
 // hits = 3, isOptional = true, sample = true
 religiousReforms: Option[Boolean] = None,
 // hits = 3, isOptional = true, sample = true
 usesKarma: Option[Boolean] = None,
 // hits = 3, isOptional = true, sample = true
 usesPiety: Option[Boolean] = None,
 // hits = 2, isOptional = true, sample = ["divorce_consort_aspect","gain_consort_aspect","dissolve_monasteries_aspect","monopoly_aspect","stability_aspect"]
 aspects: Seq[String] = Seq.empty,
 // hits = 2, isOptional = true, sample = "ANGLICAN_ASPECTS"
 aspectsName: Option[String] = None,
 // hits = 2, isOptional = true, sample = true
 misguidedHeretic: Option[Boolean] = None,
 // hits = 2, isOptional = true, sample = true
 personalDeity: Option[Boolean] = None,
 // hits = 2, isOptional = true, sample = {"any_owned_province":{"can_have_center_of_reformation_trigger":{"RELIGION":"protestant"}}}
 willGetCenter: Option[JsonMap] = None,
 // hits = 1, isOptional = true, sample = true
 authority: Option[Boolean] = None,
 // hits = 1, isOptional = true, sample = ["legitimize_government","encourage_warriors_of_the_faith","send_monks_to_establish_monasteries","promote_territorial_rights","will_of_the_martyrs"]
 blessings: Seq[String] = Seq.empty,
 // hits = 1, isOptional = true, sample = true
 canHaveSecondaryReligion: Option[Boolean] = None,
 // hits = 1, isOptional = true, sample = true
 declareWarInRegency: Option[Boolean] = None,
 // hits = 1, isOptional = true, sample = true
 doom: Option[Boolean] = None,
 // hits = 1, isOptional = true, sample = true
 fervor: Option[Boolean] = None,
 // hits = 1, isOptional = true, sample = true
 fetishistCult: Option[Boolean] = None,
 // hits = 1, isOptional = true, sample = [1,4]
 flagEmblemIndexRange: Seq[Int] = Seq.empty,
 // hits = 1, isOptional = true, sample = 20
 flagsWithEmblemPercentage: Option[Int] = None,
 // hits = 1, isOptional = true, sample = true
 hasPatriarchs: Option[Boolean] = None,
 // hits = 1, isOptional = true, sample = [358,1227,2313,1234,419]
 holySites: Seq[Int] = Seq.empty,
 // hits = 1, isOptional = true, sample = true
 hreHereticReligion: Option[Boolean] = None,
 // hits = 1, isOptional = true, sample = true
 hreReligion: Option[Boolean] = None,
 // hits = 1, isOptional = true, sample = {"icon_michael":{"discipline":0.05,"manpower_recovery_speed":0.1,"allow":{"always":true},"ai_will_do":{"factor":1,"modifier":[{"factor":0,"is_at_war":false},{"factor":3,"is_in_important_war":true}]}},"icon_eleusa":{"global_unrest":-3,"harsh_treatment_cost":-0.25,"allow":{"always":true},"ai_will_do":{"factor":1,"modifier":[{"factor":0,"NOT":{"calc_true_if":{"all_owned_province":{"unrest":1},"amount":2}}},{"factor":3,"calc_true_if":{"all_owned_province":{"unrest":1},"amount":5}}]}},"icon_pancreator":{"development_cost":-0.10,"build_cost":-0.1,"allow":{"always":true},"ai_will_do":{"factor":0}},"icon_nicholas":{"improve_relation_modifier":0.25,"ae_impact":-0.1,"allow":{"always":true},"ai_will_do":{"factor":0.5}},"icon_climacus":{"global_institution_spread":0.25,"embracement_cost":-0.2,"allow":{"always":true},"ai_will_do":{"factor":0.8,"modifier":[{"factor":0,"NOT":{"calc_true_if":{"all_owned_province":{"current_institution":1,"NOT":{"current_institution":95}},"amount":1}}},{"factor":3,"calc_true_if":{"all_owned_province":{"current_institution":1,"NOT":{"current_institution":95}},"amount":5}}]}}}
 orthodoxIcons: Option[JsonMap] = None,
 // hits = 1, isOptional = true, sample = {"papal_tag":"PAP","election_cost":5,"seat_of_papacy":118,"levy_church_tax":{"cost":50,"potential":{"NOT":{"tag":"PAP"}},"allow":{"NOT":[{"war_with":"PAP"},{"has_country_modifier":"papal_sanction_for_church_taxes"}]},"effect":{"add_country_modifier":{"name":"papal_sanction_for_church_taxes","duration":7300}},"ai_will_do":{"factor":1}},"bless_monarch":{"cost":50,"potential":{"NOT":{"tag":"PAP"}},"allow":{"NOT":[{"war_with":"PAP"},{"prestige":100},{"has_country_modifier":"papal_blessing"}]},"effect":{"add_country_modifier":{"name":"papal_blessing","duration":7300}},"ai_will_do":{"factor":1}},"indulgence_for_sins":{"cost":50,"potential":{"government":"monarchy","NOT":{"tag":"PAP"}},"allow":{"NOT":[{"war_with":"PAP"},{"legitimacy":100},{"has_country_modifier":"papal_indulgence"}]},"effect":{"add_country_modifier":{"name":"papal_indulgence","duration":7300}},"ai_will_do":{"factor":1}},"local_saint":{"cost":100,"potential":{"NOT":{"tag":"PAP"}},"allow":{"NOT":[{"war_with":"PAP"},{"stability":3}]},"effect":{"add_stability":1},"ai_will_do":{"factor":1,"modifier":{"factor":2,"NOT":{"stability":0}}}},"forgiveness_for_usury":{"cost":50,"potential":{"NOT":{"tag":"PAP"}},"allow":{"NOT":[{"war_with":"PAP"},{"has_country_modifier":"usury_forgiven"}]},"effect":{"add_country_modifier":{"name":"usury_forgiven","duration":7300}},"ai_will_do":{"factor":1,"modifier":{"factor":0,"NOT":{"num_of_loans":1}}}},"proclaim_holy_war":{"cost":50,"potential":{"NOT":{"tag":"PAP"}},"allow":{"is_at_war":true,"NOT":[{"war_with":"PAP"},{"has_country_modifier":"papal_sanction_for_holy_war"}]},"effect":{"add_country_modifier":{"name":"papal_sanction_for_holy_war","duration":7300}},"ai_will_do":{"factor":1,"modifier":{"factor":0,"manpower_percentage":0.5}}},"send_papal_legate":{"cost":50,"potential":{"NOT":{"tag":"PAP"}},"allow":{"NOT":[{"war_with":"PAP"},{"has_country_modifier":"papal_legate"}]},"effect":{"add_country_modifier":{"name":"papal_legate","duration":7300}},"ai_will_do":{"factor":1}},"sanction_commercial_monopoly":{"cost":50,"potential":{"NOT":{"tag":"PAP"}},"allow":{"NOT":[{"war_with":"PAP"},{"mercantilism":100}]},"effect":{"add_mercantilism":1},"ai_will_do":{"factor":1,"modifier":{"factor":0,"NOT":{"has_idea_group":"trade_ideas"}}}}}
 papacy: Option[JsonMap] = None,
 // hits = 1, isOptional = true, sample = true
 usesAnglicanPower: Option[Boolean] = None,
 // hits = 1, isOptional = true, sample = true
 usesChurchPower: Option[Boolean] = None,
 // hits = 1, isOptional = true, sample = true
 usesHarmony: Option[Boolean] = None,
 // hits = 1, isOptional = true, sample = true
 usesIsolationism: Option[Boolean] = None,
) extends Entity {
  @JsonCreator def this() = this(Entity.UNDEFINED)
}

object Religion extends FromJson[Religion]
