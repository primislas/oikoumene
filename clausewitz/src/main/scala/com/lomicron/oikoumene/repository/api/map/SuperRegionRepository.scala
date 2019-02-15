package com.lomicron.oikoumene.repository.api.map

import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.oikoumene.repository.api.AbstractObjectNodeRepository

trait SuperRegionRepository extends AbstractObjectNodeRepository {

  def superRegionOfRegion(regionId: String): Option[ObjectNode]

}