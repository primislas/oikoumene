package com.lomicron.oikoumene.repository.api.map

import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.oikoumene.repository.api.AbstractObjectNodeRepository

trait ColonialRegionRepository extends AbstractObjectNodeRepository {

  def colonialRegionOfProvince(provinceId: Int): Option[ObjectNode]

}
