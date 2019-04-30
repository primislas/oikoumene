package com.lomicron.oikoumene.repository.inmemory

abstract class InMemoryIntRepository[T](f: T => Option[Int]) extends InMemoryCrudRepository[Int, T](f) {

  private var idSeq = 0

  override def nextId: Option[Int] = {
    idSeq = idSeq + 1
    Some(idSeq)
  }

}
