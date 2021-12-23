package com.lomicron.oikoumene.model

trait Id[T] {
  val id: T
}

trait StringId extends Id[String]
