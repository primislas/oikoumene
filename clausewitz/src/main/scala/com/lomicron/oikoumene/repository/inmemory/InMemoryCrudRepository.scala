package com.lomicron.oikoumene.repository.inmemory

import com.lomicron.oikoumene.repository.api.{AbstractRepository, RepositoryException}

import scala.collection.mutable
import scala.util.{Failure, Success, Try}

class InMemoryCrudRepository[K: Ordering, V](f: V => K)
  extends AbstractRepository[K, V] {

  private val entities: mutable.Map[K, V] = mutable.TreeMap[K, V]()

  private def toTry[T](o: Option[T], msg: String): Try[T] =
    o.map(Success(_)).getOrElse(Failure(new RepositoryException(msg)))

  override def create(entity: V): Try[V] =
    Try(f(entity)).map(entities.put(_, entity)).flatMap(toTry(_, "Failed to create the entity."))

  override def update(entity: V): Try[V] =
    create(entity)

  override def find(key: K): Try[V] =
    toTry(entities.get(key), s"No entity with key $key found.")

  override def findAll: Seq[V] =
    entities.values.to[Seq]

  override def remove(key: K): Try[V] =
    toTry(entities.remove(key), s"Failed to remove an entity with key $key.")
}
