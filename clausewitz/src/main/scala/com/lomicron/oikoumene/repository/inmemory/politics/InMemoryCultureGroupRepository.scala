package com.lomicron.oikoumene.repository.inmemory.politics

import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.oikoumene.repository.api.politics.CultureGroupRepository
import com.lomicron.oikoumene.repository.inmemory.InMemoryCrudRepository

case class InMemoryCultureGroupRepository()
  extends InMemoryCrudRepository[String, ObjectNode](o => o.get("id").asText())
    with CultureGroupRepository {

}
