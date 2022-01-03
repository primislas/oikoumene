package com.lomicron.imperator.repository.inmemory

import com.lomicron.imperator.repository.api.ProvinceTypesRepository

import scala.collection.mutable

object InMemoryProvinceTypeRepository extends ProvinceTypesRepository { self =>

  private val entities: mutable.Map[Int, String] = mutable.TreeMap[Int, String]()

  override def typeOfProvince(id: Int): Option[String] = entities.get(id)

  override def add(provId: Int, provType: String): ProvinceTypesRepository = {
    entities.put(provId, provType)
    self
  }

}
