package com.lomicron.imperator.repository.api

import com.lomicron.imperator.model.provinces.Province
import com.lomicron.oikoumene.model.Color
import com.lomicron.oikoumene.repository.api.AbstractRepository

trait ProvinceRepository extends AbstractRepository[Int, Province] {
  def findByColor(c: Color): Option[Province]
  def findByColor(c: Int): Option[Province]
}
