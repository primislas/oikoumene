package com.lomicron.oikoumene.repository.api

import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.eu4.repository.api.AbstractRepository

/**
  * Most common Oikoumene-Clausewitz repo type
  * parameterized with [String, ObjectNode].
  *
  */
trait AbstractObjectNodeRepository
  extends AbstractRepository[String, ObjectNode] {

}
