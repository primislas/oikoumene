package com.lomicron.utils.collection

import scala.collection.JavaConverters._

object CollectionUtils {

  implicit class MapEx[K, V](m: Map[K, V]) {

    def mapKVtoValue[R](f: (K, V) => R): Map[K, R] =
      m.map(kv => (kv._1, f(kv._1, kv._2)))

    def mapValuesEx[R](f: V => R): Map[K, R] =
      m.map(kv => (kv._1, f(kv._2)))

    def mapKeys[R](f: K => R): Map[R, V] =
      m.map(kv => (f(kv._1), kv._2))

    def mapKeyToValue[R](f: K => R): Map[K, R] =
      m.map(kv => (kv._1, f(kv._1)))

    def filterKeyValue(p: (K, V) => Boolean): Map[K, V] =
      m.filter(kv => p(kv._1, kv._2))

    def filterValues(p: V => Boolean): Map[K, V] =
      m.filter(kv => p(kv._2))

  }

  implicit class IteratorEx[T](it: java.util.Iterator[T]) {
    def toStream: Stream[T] = it.asScala.toStream
    def toSeq: Seq[T] = it.asScala.toSeq
  }

}
