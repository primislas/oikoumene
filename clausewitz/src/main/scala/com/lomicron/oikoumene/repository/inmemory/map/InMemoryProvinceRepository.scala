package com.lomicron.oikoumene.repository.inmemory.map

import com.lomicron.oikoumene.model.provinces.Province
import com.lomicron.oikoumene.repository.api.map.ProvinceRepository
import com.lomicron.oikoumene.repository.inmemory.InMemoryCrudRepository

case class InMemoryProvinceRepository()
  extends InMemoryCrudRepository[Int, Province](p => Option(p.id))
  with ProvinceRepository {

  override def setId(entity: Province, id: Int): Province =
    entity.copy(id = id)

}
