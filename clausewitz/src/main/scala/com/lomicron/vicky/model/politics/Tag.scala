package com.lomicron.vicky.model.politics

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.oikoumene.model.localisation.Localisation
import com.lomicron.oikoumene.model.{Color, Entity}
import com.lomicron.utils.json.{FromJson, JsonMapper}

@JsonCreator
case class Tag
(
  // hits = 271, isOptional = false, sample = "ABU"
  id: String = Entity.UNDEFINED,
  // hits = 271, isOptional = false, sample = [113,112,96]
  color: Color = Color.black,
  localisation: Localisation = Localisation.empty,
  // hits = 271, isOptional = false, sample = "MiddleEasternGC"
  graphicalCulture: String = Entity.UNDEFINED,
  // hits = 271, isOptional = false, sample = {"init":{"capital":1167,"primary_culture":"bedouin","religion":"shiite","government":"absolute_monarchy","plurality":0.0,"nationalvalue":"nv_order","literacy":0.3,"ruling_party":"ABU_conservative","upper_house":{"fascist":0,"liberal":10,"conservative":75,"reactionary":15,"anarcho_liberal":0,"socialist":0,"communist":0},"consciousness":0,"nonstate_consciousness":0,"oob":"ABU_oob.txt","slavery":"no_slavery","upper_house_composition":"appointed","vote_franschise":"none_voting","public_meetings":"no
  history: ObjectNode = JsonMapper.objectNode,
  // hits = 270, isOptional = true, sample = [{"name":"ABU_conservative","start_date":"1820.1.1","end_date":"1892.1.1","ideology":"conservative","economic_policy":"interventionism","trade_policy":"protectionism","religious_policy":"moralism","citizenship_policy":"residency","war_policy":"anti_military"},{"name":"ABU_conservative_2","start_date":"1892.1.1","end_date":"1935.1.1","ideology":"conservative","economic_policy":"laissez_faire","trade_policy":"protectionism","religious_policy":"moralism","citizenship_policy":"residency","war_policy
  party: Seq[Party] = Seq.empty,
  // hits = 252, isOptional = true, sample = {"dreadnought":["Al Khalifa","Al Bin Ali","Al Thani","Al Nahyan","Al Maktum","Al Qawasim"],"ironclad":["Ala al-Din","Husam al-Din","Izz al-Din","Jalal al-Din","Khayr al-Din","Majid al-Din","Nasir al-Din","Nur al-Din","Safi al-Din","Salah al-Din","Siraj al-Din"],"manowar":["Zubara","Muharraq","Manama","Dawha","Abu Dhabi","Dubayy","Al-Shariqa","Ajman"],"cruiser":["Abd al-Aziz","Abd al-Hadi","Abd al-Haqq","Abd al-Jalil","Abd al-Latif","Abd al-Hafiz","Abd al-Hakim","Abd al-Nasir","Abd al-Quddus","Ab
  unitNames: Seq[ObjectNode] = Seq.empty,
  // hits = 1, isOptional = true, sample = [40,40,40]
  fascistDictatorship: Option[Color] = None,
  // hits = 1, isOptional = true, sample = [200,10,10]
  proletarianDictatorship: Option[Color] = None,
) extends Entity { self =>

  @JsonCreator def this() = this(Entity.UNDEFINED)

//  def state: TagState = history.state
//  def withState(ts: TagState): Tag = self.modify(_.history.state).setTo(ts)
//
//  def addModifier(m: Modifier): Tag = addModifier(ActiveModifier.of(m))
//  def addModifier(am: ActiveModifier): Tag = {
//    val s = state.addModifier(am)
//    self.modify(_.history.state).setTo(s)
//  }
//
//  def atStart: Tag = at(startDate)
//
//  def atTheEnd: Tag = copy(history = history.atTheEnd())
//
//  def at(year: Int, month: Int, day: Int): Tag = at(Date(year, month, day))
//
//  def at(date: Date): Tag = copy(history = history.at(date))
}

object Tag extends FromJson[Tag]
