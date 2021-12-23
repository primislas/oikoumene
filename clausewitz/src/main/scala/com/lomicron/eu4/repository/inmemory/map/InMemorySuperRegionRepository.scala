package com.lomicron.eu4.repository.inmemory.map

import com.lomicron.eu4.model.provinces.SuperRegion
import com.lomicron.eu4.repository.api.map.SuperRegionRepository
import com.lomicron.oikoumene.repository.inmemory.InMemoryEntityRepository

import scala.collection.mutable

object InMemorySuperRegionRepository
  extends InMemoryEntityRepository[SuperRegion]
    with SuperRegionRepository {

  private val sRegionsByRegion = mutable.TreeMap[String, SuperRegion]()

  override def create(entity: SuperRegion): SuperRegion = {
    val sr = super.create(entity)
    sr.regionIds.foreach(sRegionsByRegion.put(_, sr))
    sr
  }

  override def superRegionOfRegion(areaId: String): Option[SuperRegion] =
    sRegionsByRegion.get(areaId)

  override def setId(entity: SuperRegion, id: String): SuperRegion = entity.copy(id = id)

}
