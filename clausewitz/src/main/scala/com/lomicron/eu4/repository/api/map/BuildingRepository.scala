package com.lomicron.eu4.repository.api.map

import com.lomicron.eu4.model.provinces.Building
import com.lomicron.oikoumene.repository.api.AbstractRepository

trait BuildingRepository extends AbstractRepository[String, Building] {
  def ids: Set[String] = findAll.map(_.id).toSet
}
