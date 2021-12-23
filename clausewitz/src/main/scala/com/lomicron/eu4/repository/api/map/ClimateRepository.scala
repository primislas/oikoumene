package com.lomicron.eu4.repository.api.map

import com.lomicron.eu4.model.provinces.Climate
import com.lomicron.oikoumene.repository.api.AbstractRepository

trait ClimateRepository extends AbstractRepository[String, Climate] { self =>

  def ofProvince(provinceId: Int): Seq[String] =
    findAll.filter(_.hasProvince(provinceId)).map(_.id)

  def equatorYOnProvinceImage(provId: Int): ClimateRepository
  def equatorYOnProvinceImage: Option[Int]

}
