package com.lomicron.oikoumene.repository.api.map

import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.oikoumene.repository.api.AbstractObjectNodeRepository

trait ContinentRepository extends AbstractObjectNodeRepository {

  def continentOfSuperRegion(areaId: String): Option[ObjectNode]

}