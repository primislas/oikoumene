package com.lomicron.eu4.repository.inmemory.map

import com.lomicron.eu4.model.provinces.Continent
import com.lomicron.eu4.repository.api.map.ContinentRepository
import com.lomicron.oikoumene.repository.inmemory.InMemoryEntityRepository

import scala.collection.mutable

object InMemoryContinentRepository
  extends InMemoryEntityRepository[Continent]
    with ContinentRepository {

  private val continentByArea = mutable.TreeMap[Int, Continent]()

  override def create(entity: Continent): Continent = {
    val continent = super.create(entity)
    continent.provinceIds.foreach(continentByArea.put(_, continent))
    continent
  }

  override def continentOfProvince(provinceId: Int): Option[Continent] =
    continentByArea.get(provinceId)

  override def setId(entity: Continent, id: String): Continent = entity.copy(id = id)

}
