package com.lomicron.oikoumene.model.politics

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.utils.json.FromJson
import com.lomicron.utils.parsing.tokenizer.Date

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
  subjectType: Option[String] = None,
  sourceFile: Option[String] = None,
) {

  @JsonCreator def this() = this(None, DiploRelationType.undefined, Tag.undefined, Date.zero)

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

  val celestialEmperor = "celestialEmperor"
  val hreEmperor = "emperor"

  val undefined = "undefined"
}