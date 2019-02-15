package com.lomicron.oikoumene.repository.inmemory

import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.oikoumene.repository.api.TagRepository

case class InMemoryTagRepository()
  extends InMemoryCrudRepository[String, ObjectNode](o => o.get("id").asText())
  with TagRepository {

}
