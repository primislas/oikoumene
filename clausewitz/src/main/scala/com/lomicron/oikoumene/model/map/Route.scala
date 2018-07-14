package com.lomicron.oikoumene.model.map

case class Route(source: Int, target: Int) {
  override def equals(o: Any): Boolean = o match {
    case that: Route =>
      (that.source == source && that.target == target) || (that.target == source && that.source == target)
    case _ => false
  }
}