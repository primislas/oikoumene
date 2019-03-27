package com.lomicron.oikoumene.repository.inmemory.map

import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.oikoumene.repository.api.map.ContinentRepository
import com.lomicron.oikoumene.repository.inmemory.InMemoryObjectNodeRepository
import com.lomicron.utils.collection.CollectionUtils._
import com.lomicron.utils.json.JsonMapper._

import scala.collection.mutable
import scala.util.Try

object InMemoryContinentRepository
  extends InMemoryObjectNodeRepository
    with ContinentRepository {

  private val continentByProvince = mutable.TreeMap[String, ObjectNode]()

  override def create(entity: ObjectNode): Try[ObjectNode] = {
    super.create(entity).map(region => {
      region.getArray("province_ids")
        .map(_.elements().toSeq)
        .getOrElse(Seq.empty)
        .map(_.asText())
        .foreach(continentByProvince.put(_, region))
      region
    })
  }

  override def continentOfSuperRegion(areaId: String): Option[ObjectNode] =
    continentByProvince.get(areaId)


}
