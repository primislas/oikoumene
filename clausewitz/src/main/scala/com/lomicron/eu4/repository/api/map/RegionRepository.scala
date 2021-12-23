package com.lomicron.eu4.repository.api.map

import com.lomicron.eu4.model.provinces.Region
import com.lomicron.oikoumene.repository.api.AbstractRepository

trait RegionRepository extends AbstractRepository[String, Region] {

  def regionOfArea(areaId: String): Option[Region]

}
