package com.lomicron.oikoumene.repository.api.map

import com.lomicron.oikoumene.model.provinces.Terrain
import com.lomicron.oikoumene.repository.api.AbstractRepository

trait TerrainRepository extends AbstractRepository[String, Terrain] {

  def ofProvince(provinceId: Int): Option[String] =
    findAll.find(_.hasProvince(provinceId)).map(_.id)

}
