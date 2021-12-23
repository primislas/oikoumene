package com.lomicron.eu4.model.map

case class Pixel(x: Int,
                 y: Int,
                 color: Option[Int] = Option.empty,
                 terrain: Option[Int] = Option.empty,
                 height: Option[Int] = Option.empty) {

  def withColor(v: Int): Pixel = copy(color = Option(v))

  def witHeight(v: Int): Pixel = copy(height = Option(v))

  def withTerrain(v: Int): Pixel = copy(terrain = Option(v))

}

object Pixel {

  def apply(x: Int, y: Int, color: Int): Pixel = Pixel(x, y, Option(color))

  def apply(x: Int, y: Int, color: Int, height: Int, terrain: Int): Pixel =
    Pixel(x, y, Option(color), Option(height), Option(terrain))

}
