package com.lomicron.oikoumene.repository.inmemory.map

import com.fasterxml.jackson.databind.node.{ArrayNode, ObjectNode}
import com.lomicron.oikoumene.repository.api.map.RegionRepository
import com.lomicron.oikoumene.repository.inmemory.InMemoryObjectNodeRepository

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.util.Try

object InMemoryRegionRepository
  extends InMemoryObjectNodeRepository
    with RegionRepository {

  private val regionsByArea = mutable.TreeMap[String, ObjectNode]()

  override def create(entity: ObjectNode): Try[ObjectNode] = {
    super.create(entity).map(region => {
      Option(region.get("areas"))
        .map(_.asInstanceOf[ArrayNode])
        .map(_.elements().asScala.to[Seq])
        .getOrElse(Seq.empty)
        .map(_.asText())
        .foreach(regionsByArea.put(_, region))
      region
    })
  }

  override def regionOfArea(areaId: String): Option[ObjectNode] =
    regionsByArea.get(areaId)


}
