package com.lomicron.imperator.repository.inmemory

import com.lomicron.imperator.model.provinces.Province
import com.lomicron.imperator.repository.api.ProvinceRepository
import com.lomicron.oikoumene.repository.inmemory.InMemoryCrudRepository

import scala.collection.immutable.SortedMap

object InMemoryProvinceRepository
  extends InMemoryCrudRepository[Int, Province](p => Option(p.id))
    with ProvinceRepository {

  override def setId(entity: Province, id: Int): Province = entity.copy(id = id)

  override def findNames(keys: Seq[Int]): SortedMap[Int, String] = ???

}
