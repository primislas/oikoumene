package com.lomicron.oikoumene.repository.inmemory

import com.lomicron.oikoumene.repository.api.AbstractRepository

import scala.collection.mutable

abstract class InMemoryCrudRepository[K: Ordering, V](val keyOf: V => Option[K])
  extends AbstractRepository[K, V] {

  private val entities: mutable.Map[K, V] = mutable.TreeMap[K, V]()

  def nextId: Option[K] = None

  def setId(entity: V, id: K): V

  override def create(entity: V): V = {
    val id = keyOf(entity)
    val withId = if (id.isEmpty) nextId.map(setId(entity, _)) else Option(entity)
    for {entity <- withId; id <- withId.flatMap(keyOf)} entities.put(id, entity)

    withId.getOrElse(entity)
  }

  override def update(entity: V): V =
    create(entity)

  override def find(key: K): Option[V] =
    entities.get(key)

  override def findAll: Seq[V] =
    entities.values.to(Seq)

  override def remove(key: K): Option[V] =
    entities.remove(key)

  def size: Int = entities.size

}
