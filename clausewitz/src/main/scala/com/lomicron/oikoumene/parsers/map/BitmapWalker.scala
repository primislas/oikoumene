package com.lomicron.oikoumene.parsers.map

import java.awt.Point
import java.awt.image.BufferedImage

import com.lomicron.oikoumene.parsers.map.MapParser.getRGB

trait BitmapWalker {

  val img: BufferedImage
  val groups: Array[Array[Int]]

  def nextDirection(p: Point, d: Direction, g: Int): Direction =
    if (neighborGroupMatches(p, d.rBackward, g)) d.rBackward
    else if (neighborGroupMatches(p, d, g)) d
    else if (diagNeighborGroupMatches(p, d, g)) d.rBackward
    else d.rForward

  def diagNeighborGroupMatches(p: Point, d: Direction, g: Int): Boolean =
    diagNeighborGroup(p, d).contains(g)

  def neighborGroupMatches(p: Point, d: Direction, g: Int): Boolean =
    neighborGroup(p, d).contains(g)

  def neighborGroup(p: Point, d: Direction): Option[Int] =
    neighbor(p, d).map(groupOf)

  def neighborColorMatches(p: Point, d: Direction, c: Int): Boolean =
    neighbor(p, d).map(colorOf).contains(c)

  def neighborColor(p: Point, d: Direction): Option[Int] =
    neighbor(p, d).map(colorOf)

  def diagNeighborGroup(p: Point, d: Direction): Option[Int] =
    diagNeighbor(p, d).map(groupOf)

  def diagNeighbor(p: Point, d: Direction): Option[Point] =
    neighbor(p, d).flatMap(neighbor(_, d.rBackward))

  def neighbor(p: Point, d: Direction): Option[Point] = d match {
    case Up => if (p.y == 0) None else Some(new Point(p.x, p.y - 1))
    case Right => if (p.x == img.getWidth - 1) None else Some(new Point(p.x + 1, p.y))
    case Down => if (p.y == img.getHeight - 1) None else Some(new Point(p.x, p.y + 1))
    case Left => if (p.x == 0) None else Some(new Point(p.x - 1, p.y))
    case _ => None
  }

  def groupOf(p: Point): Int = groups(p.x)(p.y)
  def colorOf(p: Point): Int = getRGB(img, p.x, p.y).get

}
