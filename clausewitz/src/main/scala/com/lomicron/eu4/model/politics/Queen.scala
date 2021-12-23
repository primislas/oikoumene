package com.lomicron.eu4.model.politics

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.oikoumene.model.Entity
import com.lomicron.utils.json.FromJson
import com.lomicron.utils.parsing.tokenizer.Date

@JsonCreator
case class Queen
(
  // hits = 274, isOptional = false, sample = "Tenagne"
  name: String = Entity.UNDEFINED,
  // hits = 274, isOptional = false, sample = 3
  adm: Int = 0,
  // hits = 274, isOptional = false, sample = 2
  dip: Int = 0,
  // hits = 274, isOptional = false, sample = 2
  mil: Int = 0,
  // hits = 274, isOptional = false, sample = "Solomonid"
  dynasty: String = Entity.UNDEFINED,
  // hits = 273, isOptional = true, sample = "1420.1.1"
  birthDate: Option[Date] = None,
  // hits = 273, isOptional = true, sample = "1460.1.1"
  deathDate: Option[Date] = None,
  // hits = 272, isOptional = true, sample = "ETH"
  countryOfOrigin: Option[String] = None,
  // hits = 269, isOptional = true, sample = true
  female: Option[Boolean] = None,
  // hits = 1, isOptional = true, sample = "persian"
  culture: Option[String] = None,
  // hits = 1, isOptional = true, sample = "sunni"
  religion: Option[String] = None,

  personalities: Seq[String] = Seq.empty
) {

  def clearPersonalities: Queen = copy(personalities = Seq.empty)

}

object Queen extends FromJson[Queen]
