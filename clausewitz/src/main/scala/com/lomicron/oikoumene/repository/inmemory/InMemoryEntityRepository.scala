package com.lomicron.oikoumene.repository.inmemory

import com.lomicron.oikoumene.model.Entity

class InMemoryEntityRepository[V <: Entity] extends InMemoryCrudRepository[String, V](_.id)
