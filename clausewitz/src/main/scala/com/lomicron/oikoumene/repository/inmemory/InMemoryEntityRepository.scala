package com.lomicron.oikoumene.repository.inmemory

import com.lomicron.oikoumene.model.Entity

import scala.collection.immutable.SortedMap

abstract class InMemoryEntityRepository[V <: Entity] extends InMemoryCrudRepository[String, V](e => Option(e.id)) {

  override def findNames(keys: Seq[String]): SortedMap[String, String] = {
    val m = find(keys)
      .filter(keyOf(_).isDefined)
      .groupBy(keyOf(_).get)
      .mapValues(_.head)
      .mapValues(v => v.localisation.name.getOrElse(v.id))
    SortedMap[String, String]() ++ m
  }


}
