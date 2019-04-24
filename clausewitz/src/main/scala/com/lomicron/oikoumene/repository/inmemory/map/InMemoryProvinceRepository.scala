package com.lomicron.oikoumene.repository.inmemory.map

import com.lomicron.oikoumene.model.provinces.Province
import com.lomicron.oikoumene.repository.api.map.ProvinceRepository
import com.lomicron.oikoumene.repository.inmemory.InMemoryCrudRepository

case class InMemoryProvinceRepository()
  extends InMemoryCrudRepository[Int, Province](_.id)
  with ProvinceRepository
