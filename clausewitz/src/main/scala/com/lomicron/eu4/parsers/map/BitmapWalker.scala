package com.lomicron.eu4.parsers.map

import java.awt.Point
import java.awt.image.BufferedImage

import com.lomicron.eu4.parsers.map.MapParser.getRGB

trait BitmapWalker {

  val img: BufferedImage
  val labels: Array[Array[Int]]

  def nextDirection(p: Point, d: Direction, g: Int): Direction =
    if (neighborRegionMatches(p, d.rBackward, g)) d.rBackward
    else if (neighborRegionMatches(p, d, g)) d
    else if (diagNeighborRegionMatches(p, d, g)) d.rBackward
    else d.rForward

  def diagNeighborRegionMatches(p: Point, d: Direction, g: Int): Boolean =
    diagNeighborRegion(p, d).contains(g)

  def neighborRegionMatches(p: Point, d: Direction, g: Int): Boolean =
    neighborRegion(p, d).contains(g)

  def neighborRegion(p: Point, d: Direction): Option[Int] =
    neighbor(p, d).map(regionOf)

  def neighborColorMatches(p: Point, d: Direction, c: Int): Boolean =
    neighbor(p, d).map(colorOf).contains(c)

  def neighborColor(p: Point, d: Direction): Option[Int] =
    neighbor(p, d).map(colorOf)

  def diagNeighborRegion(p: Point, d: Direction): Option[Int] =
    diagNeighbor(p, d).map(regionOf)

  def diagNeighbor(p: Point, d: Direction): Option[Point] =
    neighbor(p, d).flatMap(neighbor(_, d.rBackward))

  def neighbor(p: Point, d: Direction): Option[Point] = d match {
    case Up => if (p.y == 0) None else Some(new Point(p.x, p.y - 1))
    case Right => if (p.x == img.getWidth - 1) None else Some(new Point(p.x + 1, p.y))
    case Down => if (p.y == img.getHeight - 1) None else Some(new Point(p.x, p.y + 1))
    case Left => if (p.x == 0) None else Some(new Point(p.x - 1, p.y))
    case _ => None
  }

  def regionOf(p: Point): Int = labels(p.x)(p.y)
  def colorOf(p: Point): Int = getRGB(img, p.x, p.y).get

}
