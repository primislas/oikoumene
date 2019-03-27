package com.lomicron.oikoumene.repository.inmemory.map



import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.oikoumene.repository.api.map.ColonialRegionRepository
import com.lomicron.oikoumene.repository.inmemory.InMemoryObjectNodeRepository
import com.lomicron.utils.json.JsonMapper._

import scala.collection.mutable
import scala.util.Try

object InMemoryColonialRegionRepository
  extends InMemoryObjectNodeRepository
    with ColonialRegionRepository {

  private val cRegionsByProvince = mutable.TreeMap[Int, ObjectNode]()

  override def create(entity: ObjectNode): Try[ObjectNode] = {
    super.create(entity).map(region => {
      region
        .getArray("province_ids")
        .toSeq
          .map(_.asInt)
          .foreach(cRegionsByProvince.put(_, region))
      region
    })
  }

  override def colonialRegionOfProvince(provinceId: Int): Option[ObjectNode] =
    cRegionsByProvince.get(provinceId)


}
