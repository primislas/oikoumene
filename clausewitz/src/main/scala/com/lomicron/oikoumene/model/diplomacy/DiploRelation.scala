package com.lomicron.oikoumene.model.diplomacy

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.oikoumene.model.politics.Tag
import com.lomicron.utils.json.FromJson
import com.lomicron.utils.parsing.tokenizer.Date

@JsonCreator
case class DiploRelation
(
  id: Option[Int] = None,
  // alliance, royal_marriage, etc
  `type`: String,
  // first partner or overlord tag
  first: String,
  // relation start date
  startDate: Date,
  // second partner or subject tag
  second: Option[String] = None,
  // relation end date
  endDate: Option[Date] = None,
  // only tributary_state in vanilla
  subjectType: Option[String] = None,
  tradeLeague: Option[Int] = None,
  sourceFile: Option[String] = None,
) {

  def isActiveAt(date: Date): Boolean = startDate <= date && (endDate.exists(_ > date) || endDate.isEmpty)

}

object DiploRelation extends FromJson[DiploRelation]

object DiploRelationType {
  val alliance = "alliance"
  val royal_marriage = "royal_marriage"
  val guarantee = "guarantee"
  val warning = "warning"
  val vassal = "vassal"
  val march = "march"
  val union = "union"
  val dependency = "dependency"

  val celestialEmperor = "celestial_emperor"
  val hreEmperor = "emperor"

  val undefined = "undefined"
}
