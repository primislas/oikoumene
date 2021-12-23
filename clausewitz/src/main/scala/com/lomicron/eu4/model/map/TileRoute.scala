package com.lomicron.eu4.model.map

case class TileRoute(source: Int, target: Int) {

  def inverse: TileRoute = TileRoute(target, source)

  override def hashCode(): Int = {
    val a = if (source < target) source.hashCode() else target.hashCode()
    val b = if (source < target) target.hashCode() else source.hashCode()
    31 * a + b
  }

  override def equals(o: Any): Boolean = o match {
    case that: TileRoute =>
      (that.source == source && that.target == target) || (that.target == source && that.source == target)
    case _ => false
  }

}
