package com.lomicron.oikoumene.repository.api.map

import com.lomicron.oikoumene.model.provinces.ColonialRegion
import com.lomicron.oikoumene.repository.api.AbstractRepository

trait ColonialRegionRepository extends AbstractRepository[String, ColonialRegion] {

  def colonialRegionOfProvince(provinceId: Int): Option[ColonialRegion]

}
