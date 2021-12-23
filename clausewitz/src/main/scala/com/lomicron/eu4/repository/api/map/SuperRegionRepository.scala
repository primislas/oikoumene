package com.lomicron.eu4.repository.api.map

import com.lomicron.eu4.model.provinces.SuperRegion
import com.lomicron.oikoumene.repository.api.AbstractRepository

trait SuperRegionRepository extends AbstractRepository[String, SuperRegion] {

  def superRegionOfRegion(regionId: String): Option[SuperRegion]

}
