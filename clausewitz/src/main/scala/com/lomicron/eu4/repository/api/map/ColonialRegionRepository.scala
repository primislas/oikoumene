package com.lomicron.eu4.repository.api.map

import com.lomicron.eu4.model.provinces.ColonialRegion
import com.lomicron.oikoumene.repository.api.AbstractRepository

trait ColonialRegionRepository extends AbstractRepository[String, ColonialRegion] {

  def colonialRegionOfProvince(provinceId: Int): Option[ColonialRegion]

}
