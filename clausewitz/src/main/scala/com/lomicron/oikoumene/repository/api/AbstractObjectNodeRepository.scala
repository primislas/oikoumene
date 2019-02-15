package com.lomicron.oikoumene.repository.api

import com.fasterxml.jackson.databind.node.ObjectNode

/**
  * Most common Oikoumene-Clausewitz repo type
  * parameterized with [String, ObjectNode].
  *
  */
trait AbstractObjectNodeRepository
  extends AbstractRepository[String, ObjectNode] {

}
