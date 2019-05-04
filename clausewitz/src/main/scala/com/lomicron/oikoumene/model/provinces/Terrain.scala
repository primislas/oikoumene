package com.lomicron.oikoumene.model.provinces

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.oikoumene.model.localisation.Localisation
import com.lomicron.oikoumene.model.{Color, Entity}
import com.lomicron.utils.json.FromJson

import scala.collection.immutable.ListSet

case class Terrain
(// hits = 20, isOptional = false, sample = "coastal_desert"
 id: String = Entity.UNDEFINED,
 // hits = 20, isOptional = false, sample = {"name":"Coastal Desert"}
 localisation: Localisation = Localisation.empty,
 // hits = 19, isOptional = true, sample = [255,211,110]
 color: Option[Color] = None,
 provinceIds: ListSet[Int] = ListSet.empty,
 // hits = 19, isOptional = true, sample = 1.0
 movementCost: BigDecimal = 1,
 // hits = 19, isOptional = true, sample = "desert"
 soundType: Option[String] = None,
 // hits = 16, isOptional = true, sample = 0.35
 localDevelopmentCost: BigDecimal = 0,
 // hits = 16, isOptional = true, sample = 0.9
 nationDesignerCostMultiplier: BigDecimal = 0,
 // hits = 16, isOptional = true, sample = 4
 supplyLimit: Int = 0,
 // hits = 14, isOptional = true, sample = "desert"
 `type`: Option[String] = None,
 // hits = 9, isOptional = true, sample = 1
 defence: Int = 0,
 // hits = 3, isOptional = true, sample = 1
 allowedNumOfBuildings: Int = 0,
 // hits = 3, isOptional = true, sample = 0.1
 localDefensiveness: Option[BigDecimal] = None,
 // hits = 2, isOptional = true, sample = true
 isWater: Boolean = false,
 // hits = 1, isOptional = true, sample = true
 inlandSea: Boolean = false,
) extends Entity {
  @JsonCreator def this() = this(Entity.UNDEFINED)

  def hasProvince(provinceId: Int): Boolean = provinceIds.contains(provinceId)
}

object Terrain extends FromJson[Terrain]
