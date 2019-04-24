package com.lomicron.oikoumene.repository.api.map

import com.lomicron.oikoumene.model.provinces.Continent
import com.lomicron.oikoumene.repository.api.AbstractRepository

trait ContinentRepository extends AbstractRepository[String, Continent] {

  def continentOfProvince(provinceId: Int): Option[Continent]

}