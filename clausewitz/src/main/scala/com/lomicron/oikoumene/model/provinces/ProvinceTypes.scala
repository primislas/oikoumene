package com.lomicron.oikoumene.model.provinces

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.utils.json.FromJson

import scala.collection.immutable.ListSet

case class ProvinceTypes
(width: Int = 0,
 height: Int = 0,
 maxProvinces: Int = 0,
 seaStarts: Set[Int] = Set.empty,
 onlyUsedForRandom: Set[Int] = Set.empty,
 lakes: Set[Int] = Set.empty,
 forceCoastal: Set[Int] = Set.empty,
 canalDefinition: Seq[CanalDefinition] = Seq.empty) {

  @JsonCreator def this() = this(0)

  def identifyType(provinceId: Int): String =
    if (seaStarts.contains(provinceId)) ProvinceTypes.sea
    else if (lakes.contains(provinceId)) ProvinceTypes.lake
    else if (onlyUsedForRandom.contains(provinceId)) ProvinceTypes.random
    else ProvinceTypes.province
}

case class CanalDefinition(name: String, x: Int, y: Int)

object ProvinceTypes extends FromJson[ProvinceTypes] {
  val province = "province"
  val sea = "sea"
  val lake = "lake"
  val random = "random"
  val wasteland = "wasteland"
  val uncolonized = "uncolonized"

  val empty: ProvinceTypes = ProvinceTypes()

  val landTypes: Set[String] = ListSet(province, wasteland, random, uncolonized)
  val list: Set[String] = landTypes ++ Seq(sea, lake)
}
