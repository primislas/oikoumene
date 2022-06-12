package com.lomicron.imperator.model.politics

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.oikoumene.model.Entity
import com.lomicron.oikoumene.model.localisation.Localisation
import com.lomicron.utils.json.FromJson

import scala.collection.immutable.ListSet

case class TagUpdate
(
  // hits = 573, isOptional = false, sample = "BAR"
  id: String = Entity.UNDEFINED,
  // hits = 573, isOptional = false, sample = {"name":"Barbarians"}
  localisation: Localisation = Localisation.empty,
  // hits = 573, isOptional = false, sample = "tribal_chiefdom"
  government: String = Entity.UNDEFINED,
  // hits = 572, isOptional = true, sample = 1
  capital: Option[Int] = None,
  // hits = 572, isOptional = true, sample = [1,2,3,4,5,6,7,8,15,16,18,19,20,24,25,26,27,31,37,40,36,39,50]
  ownControlCore: ListSet[Int] = ListSet.empty,
  // hits = 572, isOptional = true, sample = "roman"
  primaryCulture: Option[String] = None,
  // hits = 572, isOptional = true, sample = "roman_pantheon"
  religion: Option[String] = None,
  // hits = 269, isOptional = true, sample = 10
  centralization: Option[BigDecimal] = None,
  // hits = 198, isOptional = true, sample = {"military_tech":{"level":2,"progress":0},"civic_tech":{"level":2,"progress":0},"oratory_tech":{"level":2,"progress":0},"religious_tech":{"level":2,"progress":0}}
  technology: Seq[ObjectNode] = Seq.empty,
  // hits = 78, isOptional = true, sample = "warmongering_stance"
  diplomaticStance: Option[String] = None,
  // hits = 59, isOptional = true, sample = 0
  family: Option[Int] = None,
  // hits = 43, isOptional = true, sample = [201,61,39]
  treasures: Seq[Int] = Seq.empty,
  // hits = 35, isOptional = true, sample = [{"deity":1},{"deity":2},{"deity":6},{"deity":4}]
  pantheon: Seq[ObjectNode] = Seq.empty,
  // hits = 13, isOptional = true, sample = {"arrayElements":{"culture":"lepontic","type":"citizen"}}
  poptypeRights: Seq[ObjectNode] = Seq.empty,
  // hits = 11, isOptional = true, sample = {"Gelon":1,"Dionysios":2,"Dion":1,"Timoleon":1}
  historicalRegnalNumbers: Option[ObjectNode] = None,
  // hits = 8, isOptional = true, sample = "work_for_the_best"
  monarchyMilitaryReforms: Option[String] = None,
  // hits = 5, isOptional = true, sample = true
  isAntagonist: Boolean = false,
  // hits = 3, isOptional = true, sample = "foreign_law_for_minorities"
  monarchyCitizenLaw: Option[String] = None,
  // hits = 1, isOptional = true, sample = {"aggressive_expansion":10.000}
  currencyData: Option[ObjectNode] = None,
  // hits = 1, isOptional = true, sample = "republican_recruitment_1"
  republicMilitaryRecruitmentLaws: Option[String] = None,
  // hits = 1, isOptional = true, sample = "old_egyptian_succession"
  succession: Option[String] = None,
  // hits = 1, isOptional = true, sample = "egyption_succession_law"
  successionLaw: Option[String] = None,
) extends Entity {
  @JsonCreator def this() = this(Entity.UNDEFINED)
  def withId(id: String): TagUpdate = copy(id = id)
}

object TagUpdate extends FromJson[TagUpdate]
