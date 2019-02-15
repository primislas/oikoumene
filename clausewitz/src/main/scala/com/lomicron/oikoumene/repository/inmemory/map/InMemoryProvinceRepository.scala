package com.lomicron.oikoumene.repository.inmemory.map

import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.oikoumene.repository.api.map.ProvinceRepository
import com.lomicron.oikoumene.repository.inmemory.{InMemoryCrudRepository, InMemoryObjectNodeRepository}

case class InMemoryProvinceRepository()
  extends InMemoryCrudRepository[Int, ObjectNode](o => o.get("id").asInt())
  with ProvinceRepository {

}
