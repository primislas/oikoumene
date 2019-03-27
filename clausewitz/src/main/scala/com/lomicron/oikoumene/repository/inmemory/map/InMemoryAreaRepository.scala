package com.lomicron.oikoumene.repository.inmemory.map

import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.oikoumene.repository.api.map.AreaRepository
import com.lomicron.oikoumene.repository.inmemory.InMemoryObjectNodeRepository
import com.lomicron.utils.collection.CollectionUtils._
import com.lomicron.utils.json.JsonMapper._

import scala.collection.mutable
import scala.util.Try

object InMemoryAreaRepository
  extends InMemoryObjectNodeRepository
    with AreaRepository {

  private val areasByProvince: mutable.Map[Int, ObjectNode] = mutable.TreeMap()

  override def create(entity: ObjectNode): Try[ObjectNode] = {
    super.create(entity).map(area => {
      area.getArray("province_ids")
        .map(_.iterator().toSeq)
        .getOrElse(Seq.empty)
        .map(_.asInt())
        .foreach(areasByProvince.put(_, area))
      area
    })
  }

  override def areaOfProvince(provinceId: Int): Option[ObjectNode] =
    areasByProvince.get(provinceId)


}
