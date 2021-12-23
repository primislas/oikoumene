package com.lomicron.eu4.repository.inmemory.map

import com.lomicron.eu4.model.provinces.ColonialRegion
import com.lomicron.eu4.repository.api.map.ColonialRegionRepository
import com.lomicron.oikoumene.repository.inmemory.InMemoryEntityRepository

import scala.collection.mutable

object InMemoryColonialRegionRepository
  extends InMemoryEntityRepository[ColonialRegion]
    with ColonialRegionRepository {

  private val cRegionsByProvince = mutable.TreeMap[Int, ColonialRegion]()

  override def create(entity: ColonialRegion): ColonialRegion = {
    val region = super.create(entity)
    region.provinceIds.foreach(cRegionsByProvince.put(_, region))
    region
  }

  override def colonialRegionOfProvince(provinceId: Int): Option[ColonialRegion] =
    cRegionsByProvince.get(provinceId)

  override def setId(entity: ColonialRegion, id: String): ColonialRegion = entity.copy(id = id)

}
