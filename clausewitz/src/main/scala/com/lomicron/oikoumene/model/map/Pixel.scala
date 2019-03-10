package com.lomicron.oikoumene.model.map

case class Pixel(x: Int,
                 y: Int,
                 color: Option[Int] = Option.empty,
                 height: Option[Int] = Option.empty,
                 terrain: Option[Int] = Option.empty)

object Pixel {

  def apply(x: Int, y: Int, color: Int): Pixel = Pixel(x, y, Option(color))

  def apply(x: Int, y: Int, color: Int, height: Int, terrain: Int): Pixel =
    Pixel(x, y, Option(color), Option(height), Option(terrain))

}