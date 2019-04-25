package com.lomicron.oikoumene.repository.inmemory.map

import com.lomicron.oikoumene.model.provinces.Terrain
import com.lomicron.oikoumene.repository.api.map.TerrainRepository
import com.lomicron.oikoumene.repository.inmemory.InMemoryEntityRepository

import scala.collection.mutable
import scala.util.Try

object InMemoryTerrainRepository
  extends InMemoryEntityRepository[Terrain]
    with TerrainRepository {

  private val terrainByProvince = mutable.TreeMap[Int, Terrain]()

  override def create(entity: Terrain): Try[Terrain] = {
    super.create(entity).map(terrain => {
      terrain.provinceIds.foreach(terrainByProvince.put(_, terrain))
      terrain
    })
  }

  override def setId(entity: Terrain, id: String): Terrain = entity.copy(id = id)
}
