package com.lomicron.eu4.repository.api.map

import com.lomicron.eu4.model.provinces.Terrain
import com.lomicron.oikoumene.repository.api.AbstractRepository

trait TerrainRepository extends AbstractRepository[String, Terrain] {

  def ofProvince(provinceId: Int): Option[String] =
    findAll.find(_.hasProvince(provinceId)).map(_.id)

}
