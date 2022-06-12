package com.lomicron.imperator.repository.inmemory

import com.lomicron.imperator.model.provinces.Province
import com.lomicron.imperator.repository.api.ProvinceRepository
import com.lomicron.oikoumene.model.Color
import com.lomicron.oikoumene.repository.inmemory.InMemoryCrudRepository

import scala.collection.immutable.SortedMap

object InMemoryProvinceRepository
  extends InMemoryCrudRepository[Int, Province](p => Option(p.id))
    with ProvinceRepository {

  private var byColor: Map[Int, Province] = Map.empty

  override def create(entity: Province): Province = {
    byColor = byColor + (entity.color.toInt -> entity)
    super.create(entity)
  }

  override def setId(entity: Province, id: Int): Province = entity.copy(id = id)

  override def findNames(keys: Seq[Int]): SortedMap[Int, String] = ???

  override def findByColor(c: Color): Option[Province] = findByColor(c.toInt)

  override def findByColor(c: Int): Option[Province] = byColor.get(c)

}
