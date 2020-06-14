package com.lomicron.oikoumene.parsers.map

import java.awt.Point
import java.awt.image.BufferedImage

import com.lomicron.oikoumene.parsers.map.MapParser.getRGB

trait BitmapWalker {

  val img: BufferedImage

  def nextDirection(p: Point, d: Direction, c: Int): Direction =
    if (neighborColorMatches(p, d.rBackward, c)) d.rBackward
    else if (neighborColorMatches(p, d, c)) d
    else d.rForward

  def neighborColorMatches(p: Point, d: Direction, c: Int): Boolean =
    neighbor(p, d).map(colorOf).contains(c)

  def neighborColor(p: Point, d: Direction): Option[Int] =
    neighbor(p, d).map(colorOf)

  def neighbor(p: Point, d: Direction): Option[Point] = d match {
    case Up => if (p.y == 0) None else Some(new Point(p.x, p.y - 1))
    case Right => if (p.x == img.getWidth - 1) None else Some(new Point(p.x + 1, p.y))
    case Down => if (p.y == img.getHeight - 1) None else Some(new Point(p.x, p.y + 1))
    case Left => if (p.x == 0) None else Some(new Point(p.x - 1, p.y))
    case _ => None
  }

  def colorOf(p: Point): Int = getRGB(img, p.x, p.y).get

}
