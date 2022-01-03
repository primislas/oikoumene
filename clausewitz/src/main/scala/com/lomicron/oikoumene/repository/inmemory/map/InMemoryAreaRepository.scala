package com.lomicron.oikoumene.repository.inmemory.map

import com.lomicron.oikoumene.model.provinces.Area
import com.lomicron.oikoumene.repository.api.map.AreaRepository
import com.lomicron.oikoumene.repository.inmemory.InMemoryEntityRepository

import scala.collection.mutable

object InMemoryAreaRepository
  extends InMemoryEntityRepository[Area]
    with AreaRepository {

  private val areasByProvince: mutable.Map[Int, Area] = mutable.TreeMap()

  override def create(entity: Area): Area = {
    val area = super.create(entity)
    area.provinceIds.foreach(areasByProvince.put(_, area))
    area
  }

  override def areaOfProvince(provinceId: Int): Option[Area] =
    areasByProvince.get(provinceId)

  override def setId(entity: Area, id: String): Area = entity.copy(id = id)

}
