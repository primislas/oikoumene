package com.lomicron.oikoumene.repository.inmemory

import scala.collection.immutable.SortedMap

abstract class InMemoryIntRepository[T](f: T => Option[Int]) extends InMemoryCrudRepository[Int, T](f) {

  private var idSeq = 0

  override def nextId: Option[Int] = {
    idSeq = idSeq + 1
    Some(idSeq)
  }

  override def findNames(keys: Seq[Int]): SortedMap[Int, String] =
    SortedMap[Int, String]() ++ keys.map(k => (k, k.toString))

}
