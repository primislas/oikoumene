package com.lomicron.oikoumene.repository.inmemory

import com.lomicron.oikoumene.model.Entity

abstract class InMemoryEntityRepository[V <: Entity] extends InMemoryCrudRepository[String, V](e => Option(e.id))
