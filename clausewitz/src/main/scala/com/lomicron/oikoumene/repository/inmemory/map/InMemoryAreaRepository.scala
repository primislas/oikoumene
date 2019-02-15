package com.lomicron.oikoumene.repository.inmemory.map

import com.fasterxml.jackson.databind.node.{ArrayNode, ObjectNode}
import com.lomicron.oikoumene.repository.api.map.AreaRepository
import com.lomicron.oikoumene.repository.inmemory.InMemoryObjectNodeRepository

import scala.collection.mutable
import scala.util.Try
import scala.collection.JavaConverters._

object InMemoryAreaRepository
  extends InMemoryObjectNodeRepository
    with AreaRepository {

  private val areasByProvince: mutable.Map[Int, ObjectNode] = mutable.TreeMap()

  override def create(entity: ObjectNode): Try[ObjectNode] = {
    super.create(entity).map(area => {
      Option(area.get("province_ids"))
        .filter(_.isInstanceOf[ArrayNode])
        .map(_.asInstanceOf[ArrayNode].iterator().asScala.to[Seq])
        .getOrElse(Seq.empty)
        .map(_.asInt())
        .foreach(areasByProvince.put(_, area))
      area
    })
  }

  override def areaOfProvince(provinceId: Int): Option[ObjectNode] =
    areasByProvince.get(provinceId)


}
