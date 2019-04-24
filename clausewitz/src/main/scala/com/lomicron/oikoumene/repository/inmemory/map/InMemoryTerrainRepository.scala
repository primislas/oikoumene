package com.lomicron.oikoumene.repository.inmemory.map

import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.oikoumene.model.provinces.Terrain
import com.lomicron.oikoumene.repository.api.map.TerrainRepository
import com.lomicron.oikoumene.repository.inmemory.{InMemoryEntityRepository, InMemoryObjectNodeRepository}
import com.lomicron.utils.json.JsonMapper._

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

}
