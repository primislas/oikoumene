package com.lomicron.oikoumene.repository.inmemory.map

import com.lomicron.oikoumene.model.provinces.Region
import com.lomicron.oikoumene.repository.api.map.RegionRepository
import com.lomicron.oikoumene.repository.inmemory.InMemoryEntityRepository

import scala.collection.mutable
import scala.util.Try

object InMemoryRegionRepository
  extends InMemoryEntityRepository[Region]
    with RegionRepository {

  private val regionsByArea = mutable.TreeMap[String, Region]()

  override def create(entity: Region): Try[Region] = {
    super.create(entity).map(region => {
      region.areas.foreach(regionsByArea.put(_, region))
      region
    })
  }

  override def regionOfArea(areaId: String): Option[Region] =
    regionsByArea.get(areaId)

  override def setId(entity: Region, id: String): Region = entity.copy(id = id)

}
