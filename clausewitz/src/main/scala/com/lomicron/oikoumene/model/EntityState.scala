package com.lomicron.oikoumene.model

// TODO see if you can rewrite it so that next was returning E
trait EntityState[E, U] {

  def next(update: U): EntityState[E, U]

  def next(updates: Seq[U]): EntityState[E, U] =
    updates.foldLeft(this)(_ + _)

  def +(update: U): EntityState[E, U] = next(update)

}