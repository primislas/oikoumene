package com.lomicron.oikoumene.repository.api.map

import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.oikoumene.repository.api.AbstractObjectNodeRepository

trait AreaRepository extends AbstractObjectNodeRepository {

  def areaOfProvince(provinceId: Int): Option[ObjectNode]

}
