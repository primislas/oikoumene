package com.lomicron.oikoumene.repository.inmemory

import com.fasterxml.jackson.databind.node.ObjectNode

class InMemoryObjectNodeRepository
extends InMemoryCrudRepository[String, ObjectNode](o => o.get("id").asText()) {
}
