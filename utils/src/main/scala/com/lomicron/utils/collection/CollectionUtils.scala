package com.lomicron.utils.collection

import scala.collection.JavaConverters._
import scala.util.Try

object CollectionUtils {

  implicit class MapEx[K, V](m: Map[K, V]) {

    def mapKVtoValue[R](f: (K, V) => R): Map[K, R] =
      m.map(kv => (kv._1, f(kv._1, kv._2)))

    def mapValuesEx[R](f: V => R): Map[K, R] =
      m.map(kv => (kv._1, f(kv._2)))

    def mapKeys[R](f: K => R): Map[R, V] =
      m.map(kv => (f(kv._1), kv._2))

    def flatMapValues[R](f: V => Option[R]): Map[K, R] =
      m.mapValuesEx(f).filterValues(_.isDefined).mapValuesEx(_.get)

    def mapKeyToValue[R](f: K => R): Map[K, R] =
      m.map(kv => (kv._1, f(kv._1)))

    def filterKeyValue(p: (K, V) => Boolean): Map[K, V] =
      m.filter(kv => p(kv._1, kv._2))

    def filterValues(p: V => Boolean): Map[K, V] =
      m.filter(kv => p(kv._2))

    def foreachKV[U](f: (K, V) => U): Map[K, V] = {
      m.foreach{ case (k, v) => f(k, v)}
      m
    }
    


  }

  implicit class IteratorEx[T](it: java.util.Iterator[T]) {
    def toStream: Stream[T] = it.asScala.toStream
    def toSeq: Seq[T] = it.asScala.toList
  }

  implicit class SeqEx[T](seq: Seq[T]) {

    def flatMap[R](f: T => Try[R]): Seq[R] =
      seq.map(f).filter(_.isSuccess).map(_.get)

    def toMapEx[K, V](f: T => (K,V)): Map[K, V] =
      seq.map(f).toMap
  }

  implicit class OptionEx[T](o: Option[T]) {
    def cast[R: Manifest]: Option[R] = o.filter(_.isInstanceOf[R]).map(_.asInstanceOf[R])
  }

}
