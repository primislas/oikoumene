package com.lomicron.oikoumene.parsers.map

import java.awt.Point

sealed trait Direction {
  self =>

  val directions: Seq[Direction] = Seq(Up, Right, Down, Left)

  def directionsAfter(d: Direction = self): Seq[Direction] = d match {
    case Up => Seq(Right, Down, Left)
    case Right => Seq(Down, Left, Up)
    case Down => Seq(Left, Up, Right)
    case Left => Seq(Up, Right, Down)
  }

  def directionsForward(d: Direction = self): Seq[Direction] = d match {
    case Up => Seq(Left, Up, Right)
    case Right => Seq(Up, Right, Down)
    case Down => Seq(Right, Down, Left)
    case Left => Seq(Down, Left, Up)
  }

  def isReverse(d: Direction): Boolean = self match {
    case Right => d == Left
    case Left => d == Right
    case Up => d == Down
    case Down => d == Up
  }

  def svgPoint(p: Point): Point = self match {
    case Right => p
    case Down => new Point(p.x + 1, p.y)
    case Left => new Point(p.x + 1, p.y + 1)
    case Up => new Point(p.x, p.y + 1)
  }

  def turnPoints(p: Point, smoothing: Int = 0, from: Direction = rBackward): Seq[Point2D] = {
    val startPoint =
      if (smoothing == 0) Point2D(svgPoint(p))
      else {
        val offsetStart = smoothing - 0.5
        val offsetDir = if (from == rBackward) 1 else if (from == rForward) -1 else 0
        val offset = offsetDir * offsetStart
        self match {
          case Right => Point2D(p.x, p.y + offset)
          case Down => Point2D(p.x + 1 - offset, p.y)
          case Left => Point2D(p.x + 1, p.y + 1 - offset)
          case Up => Point2D(p.x + offset, p.y + 1)
        }
      }

    val endPoint = Option(smoothing)
      .filter(_ > 0)
      .map(_ => self match {
        case Right => Point2D(p.x + 0.5, p.y)
        case Down => Point2D(p.x + 1, p.y + 0.5)
        case Left => Point2D(p.x + 0.5, p.y + 1)
        case Up => Point2D(p.x, p.y + 0.5)
      })

    Seq(startPoint) ++ endPoint.toSeq
  }

  def rForward: Direction = self match {
    case Right => Down
    case Down => Left
    case Left => Up
    case Up => Right
  }

  def rBackward: Direction = self match {
    case Right => Up
    case Up => Left
    case Left => Down
    case Down => Right
  }

}

object Right extends Direction
object Down extends Direction
object Left extends Direction
object Up extends Direction
