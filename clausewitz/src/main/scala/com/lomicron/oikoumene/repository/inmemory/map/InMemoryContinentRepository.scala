package com.lomicron.oikoumene.repository.inmemory.map

import com.lomicron.oikoumene.model.provinces.Continent
import com.lomicron.oikoumene.repository.api.map.ContinentRepository
import com.lomicron.oikoumene.repository.inmemory.InMemoryEntityRepository

import scala.collection.mutable
import scala.util.Try

object InMemoryContinentRepository
  extends InMemoryEntityRepository[Continent]
    with ContinentRepository {

  private val continentByArea = mutable.TreeMap[Int, Continent]()

  override def create(entity: Continent): Try[Continent] = {
    super.create(entity).map(continent => {
      continent.provinceIds.foreach(continentByArea.put(_, continent))
      continent
    })
  }

  override def continentOfProvince(provinceId: Int): Option[Continent] =
    continentByArea.get(provinceId)


}
