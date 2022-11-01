package com.lomicron.vicky.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.oikoumene.model.Entity
import com.lomicron.oikoumene.model.localisation.Localisation
import com.lomicron.utils.json.FromJson

case class Building
(
  // hits = 33, isOptional = false, sample = "aeroplane_factory"
  id: String = Entity.UNDEFINED,
  localisation: Localisation = Localisation.empty,
  // hits = 33, isOptional = false, sample = {"machine_parts":200,"electric_gear":600,"steel":600,"cement":600}
  goodsCost: Map[String, Int] = Map.empty,
  // hits = 33, isOptional = false, sample = 99
  maxLevel: Int = 0,
  // hits = 33, isOptional = false, sample = false
  onmap: Boolean = true,
  // hits = 33, isOptional = false, sample = 730
  time: Int = 0,
  // hits = 33, isOptional = false, sample = "factory"
  `type`: String = Entity.UNDEFINED,
  // hits = 33, isOptional = false, sample = true
  visibility: Boolean = false,
  // hits = 31, isOptional = true, sample = true
  popBuildFactory: Boolean = false,
  // hits = 30, isOptional = true, sample = 0.2
  completionSize: Option[BigDecimal] = None,
  // hits = 30, isOptional = true, sample = "factory"
  onCompletion: Option[String] = None,
  // hits = 30, isOptional = true, sample = "aeroplane_factory"
  productionType: Option[String] = None,
  // hits = 5, isOptional = true, sample = true
  advancedFactory: Boolean = false,
  // hits = 4, isOptional = true, sample = true
  strategicFactory: Boolean = false,
  // hits = 3, isOptional = true, sample = true
  defaultEnabled: Boolean = false,
  // hits = 3, isOptional = true, sample = true
  province: Boolean = false,
  // hits = 1, isOptional = true, sample = true
  capital: Boolean = false,
  // hits = 1, isOptional = true, sample = [30,50,70,90,110,130]
  colonialPoints: Seq[Int] = Seq.empty,
  // hits = 1, isOptional = true, sample = 50
  colonialRange: Option[Int] = None,
  // hits = 1, isOptional = true, sample = 15000
  cost: Option[Int] = None,
  // hits = 1, isOptional = true, sample = 1
  fortLevel: Option[Int] = None,
  // hits = 1, isOptional = true, sample = 0.16
  infrastructure: Option[BigDecimal] = None,
  // hits = 1, isOptional = true, sample = -0.10
  localShipBuild: Option[BigDecimal] = None,
  // hits = 1, isOptional = true, sample = -0.05
  movementCost: Option[BigDecimal] = None,
  // hits = 1, isOptional = true, sample = 1
  navalCapacity: Option[Int] = None,
  // hits = 1, isOptional = true, sample = true
  onePerState: Boolean = false,
  // hits = 1, isOptional = true, sample = true
  port: Boolean = false,
  // hits = 1, isOptional = true, sample = true
  sail: Boolean = false,
  // hits = 1, isOptional = true, sample = true
  spawnRailwayTrack: Boolean = false,
  // hits = 1, isOptional = true, sample = true
  steam: Boolean = false,
) extends Entity {
  @JsonCreator def this() = this(Entity.UNDEFINED)
}

object Building extends FromJson[Building]
