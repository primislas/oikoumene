package com.lomicron.oikoumene.model.politics

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.oikoumene.model.Entity
import com.lomicron.utils.json.FromJson
import com.lomicron.utils.parsing.tokenizer.Date

@JsonCreator
case class Heir
(
  // hits = 3723, isOptional = false, sample = 1
  adm: Int = 0,
  // hits = 3723, isOptional = false, sample = "1389.1.1"
  birthDate: Date = Date.zero,
  // hits = 3723, isOptional = false, sample = 3
  dip: Int = 0,
  // hits = 3723, isOptional = false, sample = 2
  mil: Int = 0,
  // hits = 3723, isOptional = false, sample = "Ali Shah"
  name: String = Entity.UNDEFINED,
  // hits = 3721, isOptional = true, sample = "1420.6.1"
  deathDate: Option[Date] = None,
  // hits = 3720, isOptional = true, sample = "Ali Shah I"
  monarchName: Option[String] = None,
  // hits = 3719, isOptional = true, sample = 95
  claim: Option[Int] = None,
  // hits = 3705, isOptional = true, sample = "Shah Miri"
  dynasty: Option[String] = None,
  // hits = 76, isOptional = true, sample = {"name":"Nurhachi","type":"general","fire":2,"shock":5,"manuever":4,"siege":2}
  leader: Option[Leader] = None,
  // hits = 64, isOptional = true, sample = true
  female: Boolean = false,
  // hits = 13, isOptional = true, sample = "scottish"
  culture: Option[String] = None,
  // hits = 5, isOptional = true, sample = "sunni"
  religion: Option[String] = None,

  personalities: Seq[String] = Seq.empty
) {

  def clearPersonalities: Heir = copy(personalities = Seq.empty)

}

object Heir extends FromJson[Heir]
