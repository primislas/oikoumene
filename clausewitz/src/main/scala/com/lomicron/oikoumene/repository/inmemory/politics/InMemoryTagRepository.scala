package com.lomicron.oikoumene.repository.inmemory.politics

import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.oikoumene.repository.api.politics.TagRepository
import com.lomicron.oikoumene.repository.inmemory.InMemoryCrudRepository

case class InMemoryTagRepository()
  extends InMemoryCrudRepository[String, ObjectNode](o => o.get("id").asText())
  with TagRepository {

}
