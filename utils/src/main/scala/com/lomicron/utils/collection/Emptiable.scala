package com.lomicron.utils.collection

trait Emptiable {
  def isEmpty: Boolean
  def nonEmpty: Boolean = !isEmpty
}
