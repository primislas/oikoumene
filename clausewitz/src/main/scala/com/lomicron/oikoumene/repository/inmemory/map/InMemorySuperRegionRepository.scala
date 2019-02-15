package com.lomicron.oikoumene.repository.inmemory.map

import com.fasterxml.jackson.databind.node.{ArrayNode, ObjectNode}
import com.lomicron.oikoumene.repository.api.map.SuperRegionRepository
import com.lomicron.oikoumene.repository.inmemory.InMemoryObjectNodeRepository

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.util.Try

object InMemorySuperRegionRepository
  extends InMemoryObjectNodeRepository
    with SuperRegionRepository {

  private val sRegionsByRegion = mutable.TreeMap[String, ObjectNode]()

  override def create(entity: ObjectNode): Try[ObjectNode] = {
    super.create(entity).map(region => {
      Option(region.get("region_ids"))
        .map(_.asInstanceOf[ArrayNode])
        .map(_.elements().asScala.to[Seq])
        .getOrElse(Seq.empty)
        .map(_.asText())
        .foreach(sRegionsByRegion.put(_, region))
      region
    })
  }

  override def superRegionOfRegion(areaId: String): Option[ObjectNode] =
    sRegionsByRegion.get(areaId)


}
