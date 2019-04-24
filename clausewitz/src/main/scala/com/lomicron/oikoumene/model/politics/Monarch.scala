package com.lomicron.oikoumene.model.politics

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.oikoumene.model.Entity
import com.lomicron.utils.json.FromJson
import com.lomicron.utils.parsing.tokenizer.Date

case class Monarch
(
  // hits = 8023, isOptional = false, sample = 2
  adm: Int = 0,
  // hits = 8023, isOptional = false, sample = 3
  dip: Int = 0,
  // hits = 8023, isOptional = false, sample = 1
  mil: Int = 0,
  // hits = 8023, isOptional = false, sample = "Yusuf"
  name: String = Entity.UNDEFINED,
  // hits = 7048, isOptional = true, sample = "Jaladi"
  dynasty: Option[String] = None,
  // hits = 1883, isOptional = true, sample = "1422.6.7"
  birthDate: Option[Date] = None,
  // hits = 319, isOptional = true, sample = "cosmopolitan_french"
  culture: Seq[String] = Seq.empty,
  // hits = 192, isOptional = true, sample = {"name":"Nurhachi","type":"general","fire":2,"shock":5,"manuever":4,"siege":2}
  leader: Option[Leader] = None,
  // hits = 114, isOptional = true, sample = true
  regent: Boolean = false,
  // hits = 104, isOptional = true, sample = true
  female: Boolean = false,
  // hits = 19, isOptional = true, sample = "sunni"
  religion: Option[String] = None,
  // hits = 9, isOptional = true, sample = "1457.11.24"
  deathDate: Option[Date] = None,
  // hits = 2, isOptional = true, sample = 50
  claim: Option[Int] = None,

  personalities: Seq[String] = Seq.empty
) {
  @JsonCreator def this() = this(0)

  def clearPersonalities: Monarch = copy(personalities = Seq.empty)
}

object Monarch extends FromJson[Monarch]