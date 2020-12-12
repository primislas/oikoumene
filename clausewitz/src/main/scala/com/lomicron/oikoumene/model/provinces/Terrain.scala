package com.lomicron.oikoumene.model.provinces

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.oikoumene.model.localisation.Localisation
import com.lomicron.oikoumene.model.modifiers.Modifier
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
 // hits = 19, isOptional = true, sample = "desert"
 soundType: Option[String] = None,
 // hits = 14, isOptional = true, sample = "desert"
 `type`: Option[String] = None,
 // hits = 2, isOptional = true, sample = true
 isWater: Boolean = false,
 // hits = 1, isOptional = true, sample = true
 inlandSea: Boolean = false,

 modifier: Option[Modifier] = None
) extends Entity {
  @JsonCreator def this() = this(Entity.UNDEFINED)

  def hasProvince(provinceId: Int): Boolean = provinceIds.contains(provinceId)
}

object Terrain extends FromJson[Terrain]
