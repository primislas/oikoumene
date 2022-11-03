package com.lomicron.vicky.repository.api

import com.lomicron.oikoumene.model.Color
import com.lomicron.oikoumene.repository.api.AbstractRepository
import com.lomicron.vicky.model.province.Province

trait ProvinceRepository extends AbstractRepository[Int, Province] {
  def findByColor(c: Color): Option[Province]
  def findByColor(c: Int): Option[Province]
}
