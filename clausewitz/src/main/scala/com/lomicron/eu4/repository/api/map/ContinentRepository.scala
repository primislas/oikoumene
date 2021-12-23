package com.lomicron.eu4.repository.api.map

import com.lomicron.eu4.model.provinces.Continent
import com.lomicron.oikoumene.repository.api.AbstractRepository

trait ContinentRepository extends AbstractRepository[String, Continent] {

  def continentOfProvince(provinceId: Int): Option[Continent]

}
