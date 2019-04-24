package com.lomicron.oikoumene.repository.api.map

import com.lomicron.oikoumene.model.provinces.Area
import com.lomicron.oikoumene.repository.api.AbstractRepository

trait AreaRepository extends AbstractRepository[String, Area] {

  def areaOfProvince(provinceId: Int): Option[Area]

}
