package com.lomicron.oikoumene.repository.api

/**
  * A group of entities produced by groupBy request.
  *
  * @tparam E entity type
  */
trait EntityGroup[E] {
  val value: AnyRef
  val entities: Seq[E] = Seq.empty
}
