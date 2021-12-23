package com.lomicron.eu4.repository.inmemory.map

import com.lomicron.eu4.model.provinces.Terrain
import com.lomicron.eu4.repository.api.map.TerrainRepository
import com.lomicron.oikoumene.repository.inmemory.InMemoryEntityRepository

import scala.collection.mutable

object InMemoryTerrainRepository
  extends InMemoryEntityRepository[Terrain]
    with TerrainRepository {

  private val terrainByProvince = mutable.TreeMap[Int, Terrain]()

  override def create(entity: Terrain): Terrain = {
    val terrain = super.create(entity)
    terrain.provinceIds.foreach(terrainByProvince.put(_, terrain))
    terrain
  }

  override def setId(entity: Terrain, id: String): Terrain = entity.copy(id = id)
}
