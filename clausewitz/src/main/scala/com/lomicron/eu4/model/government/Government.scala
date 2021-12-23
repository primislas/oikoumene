package com.lomicron.eu4.model.government

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.oikoumene.model.{Color, Entity}
import com.lomicron.oikoumene.model.localisation.Localisation
import com.lomicron.utils.json.FromJson

import scala.collection.immutable.ListSet

@JsonCreator
case class Government
(
  // hits = 5, isOptional = false, sample = "monarchy"
  id: String = Entity.UNDEFINED,
  // hits = 1, isOptional = true, sample = {"name":"Native American Tribe"}
  localisation: Localisation = Localisation.empty,
  // hits = 5, isOptional = false, sample = "00_governments.txt"
  sourceFile: String = Entity.UNDEFINED,
  // hits = 5, isOptional = false, sample = "monarchy_mechanic"
  basicReform: String = Entity.UNDEFINED,
  // hits = 5, isOptional = false, sample = [179,25,25]
  color: Color = Color.black,
  // hits = 5, isOptional = false, sample = ["despotic_monarchy","feudal_monarchy","administrative_monarchy","constitutional_monarchy","enlightened_despotism","revolutionary_empire","english_monarchy_legacy","mamluk_government_legacy","feudal_theocracy_legacy","elective_monarchy_legacy","celestial_empire_legacy","ottoman_government_legacy","prussian_monarchy_legacy","iqta_legacy","daimyo_legacy","shogunate_legacy","indep_daimyo_legacy","principality_legacy","tsardom_legacy","holy_state_legacy"]
  legacyGovernment: ListSet[String] = ListSet.empty,
  // hits = 4, isOptional = true, sample = {"feudalism_vs_autocracy":{"reforms":["feudalism_reform","autocracy_reform","plutocratic_reform","indian_sultanate_reform","nayankara_reform","misl_confederacy_reform","rajput_kingdom","grand_duchy_reform","daimyo","indep_daimyo","elective_monarchy","iqta","ottoman_government","prussian_monarchy","austrian_dual_monarchy","principality","tsardom","mamluk_government","feudal_theocracy","celestial_empire","shogunate","english_monarchy","mandala_reform","revolutionary_empire_reform","holy_state_refo
  reformLevels: IndexedSeq[ReformLevel] = IndexedSeq.empty,
  // hits = 2, isOptional = true, sample = ["iqta","indian_sultanate_reform","mamluk_government","tsardom","principality","parliamentary_reform","english_monarchy","states_general_reform","military_dictatorship_reform","protectorate_parliament_reform","prussian_republic_reform","admiralty_reform","nepotism_reform","lottery_reform","dutch_republic","mamluk_government","feudal_theocracy","states_general_reform","mughal_government","ottoman_government","revolutionary_empire_reform","prussian_monarchy","elective_monarchy","celestial_empire",
  exclusiveReforms: Seq[ListSet[String]] = Seq.empty,
) extends Entity

object Government extends FromJson[Government]
