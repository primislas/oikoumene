package com.lomicron.oikoumene.model.map

import com.lomicron.oikoumene.model.Color

import scala.util.Try

case class Tile(color: Color, pixels: Seq[Pixel] = Seq.empty) {

  def terrainColor: Option[Color] = {
    val byColor = pixels.filter(_.terrain.isDefined).groupBy(_.terrain.get)
    Try(byColor.maxBy(_._2.size)._1).toOption.map(Color(_))
  }

  override def toString: String = color.toString

}

object Tile {
  def apply(argb: Int, pixels: Seq[Pixel]): Tile = Tile(Color(argb), pixels)
}
