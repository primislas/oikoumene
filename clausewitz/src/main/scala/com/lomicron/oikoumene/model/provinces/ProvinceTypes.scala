package com.lomicron.oikoumene.model.provinces

case class ProvinceTypes
(width: Int,
 height: Int,
 maxProvinces: Int,
 seaStarts: Set[Int] = Set.empty,
 onlyUsedForRandom: Set[Int] = Set.empty,
 lakes: Set[Int] = Set.empty,
 forceCoastal: Set[Int] = Set.empty,
 canalDefinition: Seq[CanalDefinition] = Seq.empty) {

  def identifyType(provinceId: Int): String =
    if (seaStarts.contains(provinceId)) ProvinceTypes.sea
    else if (lakes.contains(provinceId)) ProvinceTypes.lake
    else if (onlyUsedForRandom.contains(provinceId)) ProvinceTypes.random
    else ProvinceTypes.province
}

case class CanalDefinition(name: String, x: Int, y: Int)

object ProvinceTypes {
  val province = "province"
  val sea = "sea"
  val lake = "lake"
  val random = "random"
}