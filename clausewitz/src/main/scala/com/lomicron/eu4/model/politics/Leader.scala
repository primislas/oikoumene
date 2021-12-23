package com.lomicron.eu4.model.politics

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.oikoumene.model.Entity
import com.lomicron.utils.parsing.tokenizer.Date

@JsonCreator
case class Leader
(
  // hits = 844, isOptional = false, sample = "Zheng He"
  name: String = Entity.UNDEFINED,
  // hits = 844, isOptional = false, sample = "explorer"
  `type`: String = Entity.UNDEFINED,
  // hits = 844, isOptional = false, sample = 1
  fire: Int = 0,
  // hits = 844, isOptional = false, sample = 1
  shock: Int = 0,
  // hits = 844, isOptional = false, sample = 6
  manuever: Int = 0,
  // hits = 844, isOptional = false, sample = 0
  siege: Int = 0,
  // hits = 843, isOptional = true, sample = "1433.1.1"
  deathDate: Option[Date] = None,
  // hits = 2, isOptional = true, sample = "inspirational_leader_general_personality"
  personality: Option[String] = None,
  // hits = 1, isOptional = true, sample = true
  female: Boolean = false,
) {

  def clearPersonalities: Leader = copy(personality = None)

}
