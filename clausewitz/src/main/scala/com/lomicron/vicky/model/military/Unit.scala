package com.lomicron.vicky.model.military

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.oikoumene.model.Entity
import com.lomicron.oikoumene.model.localisation.Localisation
import com.lomicron.utils.json.FromJson

import scala.collection.immutable.ListMap

case class Unit
(
  // hits = 21, isOptional = false, sample = "artillery"
  id: String = Entity.UNDEFINED,
  // hits = 21, isOptional = false, sample = {"name":"Artillery"}
  localisation: Localisation = Localisation.empty,
  // hits = 21, isOptional = false, sample = {"artillery":10,"canned_food":10,"liquor":10,"wine":10}
  buildCost: ListMap[String, Int] = ListMap.empty,
  // hits = 21, isOptional = false, sample = 120
  buildTime: Int = 0,
  // hits = 21, isOptional = false, sample = 30
  defaultOrganisation: Int = 0,
  // hits = 21, isOptional = false, sample = false
  floatingFlag: Option[Boolean] = None,
  // hits = 21, isOptional = false, sample = 3
  icon: Int = 0,
  // hits = 21, isOptional = false, sample = 3
  maxStrength: Int = 0,
  // hits = 21, isOptional = false, sample = 4.00
  maximumSpeed: BigDecimal = BigDecimal(0),
  // hits = 21, isOptional = false, sample = 20
  priority: Int = 0,
  // hits = 21, isOptional = false, sample = "Artillery"
  sprite: String = Entity.UNDEFINED,
  // hits = 21, isOptional = false, sample = 1.0
  supplyConsumption: BigDecimal = BigDecimal(0),
  // hits = 21, isOptional = false, sample = {"artillery":0.1,"canned_food":0.06}
  supplyCost: ListMap[String, BigDecimal] = ListMap.empty,
  // hits = 21, isOptional = false, sample = "land"
  `type`: String = Entity.UNDEFINED,
  // hits = 21, isOptional = false, sample = "support"
  unitType: String = Entity.UNDEFINED,
  // hits = 21, isOptional = false, sample = 5.0
  weightedValue: BigDecimal = BigDecimal(0),
  // hits = 18, isOptional = true, sample = false
  active: Option[Boolean] = None,
  // hits = 11, isOptional = true, sample = 1.5
  attack: Option[BigDecimal] = None,
  // hits = 11, isOptional = true, sample = 3
  defence: Option[Int] = None,
  // hits = 11, isOptional = true, sample = 1.0
  discipline: Option[BigDecimal] = None,
  // hits = 11, isOptional = true, sample = 1
  maneuver: Option[Int] = None,
  // hits = 11, isOptional = true, sample = 0
  reconnaissance: Option[Int] = None,
  // hits = 11, isOptional = true, sample = 2
  support: Option[BigDecimal] = None,
  // hits = 10, isOptional = true, sample = 0
  evasion: Option[BigDecimal] = None,
  // hits = 10, isOptional = true, sample = 0.75
  fireRange: Option[BigDecimal] = None,
  // hits = 10, isOptional = true, sample = 50
  gunPower: Option[Int] = None,
  // hits = 10, isOptional = true, sample = 70
  hull: Option[Int] = None,
  // hits = 10, isOptional = true, sample = -1
  limitPerPort: Option[BigDecimal] = None,
  // hits = 10, isOptional = true, sample = 4
  minPortLevel: Option[Int] = None,
  // hits = 10, isOptional = true, sample = 10
  navalIcon: Option[Int] = None,
  // hits = 10, isOptional = true, sample = 50
  supplyConsumptionScore: Option[Int] = None,
  // hits = 9, isOptional = true, sample = false
  canBuildOverseas: Boolean = true,
  // hits = 8, isOptional = true, sample = 15
  colonialPoints: Option[Int] = None,
  // hits = 7, isOptional = true, sample = "steam_move"
  moveSound: Option[String] = None,
  // hits = 7, isOptional = true, sample = "steam_selected"
  selectSound: Option[String] = None,
  // hits = 4, isOptional = true, sample = true
  capital: Boolean = false,
  // hits = 4, isOptional = true, sample = "GenericMount"
  spriteMount: Option[String] = None,
  // hits = 4, isOptional = true, sample = "Saddle_Node"
  spriteMountAttachNode: Option[String] = None,
  // hits = 4, isOptional = true, sample = 0
  torpedoAttack: Option[Int] = None,
  // hits = 3, isOptional = true, sample = true
  sail: Boolean = false,
  // hits = 3, isOptional = true, sample = "Cavalry"
  spriteOverride: Option[String] = None,
  // hits = 2, isOptional = true, sample = 1
  siege: Option[Int] = None,
  // hits = 2, isOptional = true, sample = true
  transport: Boolean = false,
  // hits = 1, isOptional = true, sample = true
  primaryCulture: Boolean = false,
) extends Entity {
  @JsonCreator def this() = this(Entity.UNDEFINED)
}

object Unit extends FromJson[Unit]
