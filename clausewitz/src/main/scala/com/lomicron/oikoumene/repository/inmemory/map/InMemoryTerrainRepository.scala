package com.lomicron.oikoumene.repository.inmemory.map

import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.oikoumene.repository.api.map.TerrainRepository
import com.lomicron.oikoumene.repository.inmemory.InMemoryObjectNodeRepository
import com.lomicron.utils.json.JsonMapper._

import scala.collection.mutable
import scala.util.Try

object InMemoryTerrainRepository
  extends InMemoryObjectNodeRepository
    with TerrainRepository {

  private val terrainByProvince = mutable.TreeMap[Int, ObjectNode]()

  override def create(entity: ObjectNode): Try[ObjectNode] = {
    super.create(entity).map(terrain => {
      terrain.getArray("province_ids")
        .map(_.toSeq)
        .getOrElse(Seq.empty)
        .map(_.asInt())
        .foreach(terrainByProvince.put(_, terrain))
      terrain
    })
  }

}
