package com.lomicron.oikoumene.repository.api.map

import com.lomicron.oikoumene.model.provinces.Region
import com.lomicron.oikoumene.repository.api.AbstractRepository

trait RegionRepository extends AbstractRepository[String, Region] {

  def regionOfArea(areaId: String): Option[Region]

}