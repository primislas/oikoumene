package com.lomicron.oikoumene.repository.api.map

import com.lomicron.oikoumene.model.provinces.SuperRegion
import com.lomicron.oikoumene.repository.api.AbstractRepository

trait SuperRegionRepository extends AbstractRepository[String, SuperRegion] {

  def superRegionOfRegion(regionId: String): Option[SuperRegion]

}