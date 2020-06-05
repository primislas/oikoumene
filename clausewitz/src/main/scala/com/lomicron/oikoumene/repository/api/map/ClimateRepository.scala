package com.lomicron.oikoumene.repository.api.map

import com.lomicron.oikoumene.model.provinces.Climate
import com.lomicron.oikoumene.repository.api.AbstractRepository

trait ClimateRepository extends AbstractRepository[String, Climate] {

  def ofProvince(provinceId: Int): Seq[String] =
    findAll.filter(_.hasProvince(provinceId)).map(_.id)

}
