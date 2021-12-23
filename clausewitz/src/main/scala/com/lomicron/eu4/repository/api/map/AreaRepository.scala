package com.lomicron.eu4.repository.api.map

import com.lomicron.eu4.model.provinces.Area
import com.lomicron.oikoumene.repository.api.AbstractRepository

trait AreaRepository extends AbstractRepository[String, Area] {

  def areaOfProvince(provinceId: Int): Option[Area]

}
