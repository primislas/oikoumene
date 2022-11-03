package com.lomicron.vicky.repository.inmemory

import com.lomicron.oikoumene.model.Color
import com.lomicron.oikoumene.repository.inmemory.InMemoryIntRepository
import com.lomicron.vicky.model.province.Province
import com.lomicron.vicky.repository.api.ProvinceRepository

import scala.util.Try

object InMemoryProvinceRepository
  extends InMemoryIntRepository[Province](p => Option(p.id))
    with ProvinceRepository {

  private var byColor: Map[Int, Province] = Map.empty

  override def create(entity: Province): Try[Province] = {
    byColor = byColor + (entity.color.toInt -> entity)
    super.create(entity)
  }

  override def setId(entity: Province, id: Int): Province =
    entity.copy(id = id)

  override def findByColor(c: Color): Option[Province] = findByColor(c.toInt)

  override def findByColor(c: Int): Option[Province] = byColor.get(c)

}
