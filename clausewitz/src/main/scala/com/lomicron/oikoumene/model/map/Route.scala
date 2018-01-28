package com.lomicron.oikoumene.model.map

case class Route(source: Int, target: Int) {
  override def equals(o: Any) = o match {
    case that: Route => {
      if ((that.source == source && that.target == target) || (that.target == source && that.source == target)) true
      else false
    } 
    case _ => false
  }
}