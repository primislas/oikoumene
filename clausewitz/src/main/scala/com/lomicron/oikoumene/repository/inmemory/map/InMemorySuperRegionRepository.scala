package com.lomicron.oikoumene.repository.inmemory.map

import com.lomicron.oikoumene.model.provinces.SuperRegion
import com.lomicron.oikoumene.repository.api.map.SuperRegionRepository
import com.lomicron.oikoumene.repository.inmemory.InMemoryEntityRepository

import scala.collection.mutable
import scala.util.Try

object InMemorySuperRegionRepository
  extends InMemoryEntityRepository[SuperRegion]
    with SuperRegionRepository {

  private val sRegionsByRegion = mutable.TreeMap[String, SuperRegion]()

  override def create(entity: SuperRegion): Try[SuperRegion] = {
    super.create(entity).map(sr => {
      sr.regionIds.foreach(sRegionsByRegion.put(_, sr))
      sr
    })
  }

  override def superRegionOfRegion(areaId: String): Option[SuperRegion] =
    sRegionsByRegion.get(areaId)


}
