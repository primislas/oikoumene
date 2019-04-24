package com.lomicron.oikoumene.model

trait EntityState[E <: EntityState[E, U], U] {

  def next(update: U): E

  def next(updates: Seq[U]): E =
    updates.foldLeft(this)(_ + _).asInstanceOf[E]

  def +(update: U): E = next(update)

}