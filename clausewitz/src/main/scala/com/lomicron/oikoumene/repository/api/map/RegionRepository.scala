package com.lomicron.oikoumene.repository.api.map

import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.oikoumene.repository.api.AbstractObjectNodeRepository

trait RegionRepository extends AbstractObjectNodeRepository {

  def regionOfArea(areaId: String): Option[ObjectNode]

}