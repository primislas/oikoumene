package com.lomicron.oikoumene.repository.inmemory.map

import com.lomicron.oikoumene.model.provinces.ColonialRegion
import com.lomicron.oikoumene.repository.api.map.ColonialRegionRepository
import com.lomicron.oikoumene.repository.inmemory.InMemoryEntityRepository

import scala.collection.mutable
import scala.util.Try

object InMemoryColonialRegionRepository
  extends InMemoryEntityRepository[ColonialRegion]
    with ColonialRegionRepository {

  private val cRegionsByProvince = mutable.TreeMap[Int, ColonialRegion]()

  override def create(entity: ColonialRegion): Try[ColonialRegion] = {
    super.create(entity).map(region => {
      region.provinceIds.foreach(cRegionsByProvince.put(_, region))
      region
    })
  }

  override def colonialRegionOfProvince(provinceId: Int): Option[ColonialRegion] =
    cRegionsByProvince.get(provinceId)


}
