package com.lomicron.oikoumene.model.map

case class Tile(argb: Int,
                pixels: Seq[Pixel] = Seq.empty,
                terrain: Option[Int] = Option.empty,
                height: Seq[Pixel] = Seq.empty) {

  override def toString: String = argb.toString
}