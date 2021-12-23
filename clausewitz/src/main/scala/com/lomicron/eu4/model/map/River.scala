package com.lomicron.eu4.model.map

import java.awt.Point

import com.lomicron.utils.collection.Emptiable
import com.lomicron.utils.geometry.Point2D

case class River(path: Seq[RiverSegment] = Seq.empty)
extends Emptiable
{

  def isEmpty: Boolean = !path.exists(_.nonEmpty)

  def reverse: River = River(path.map(_.reverse))

  def addStartingPoint(p: Point): River = {
    val updated = path.headOption
      .map(_.withStartingPoint(p))
      .map(s => s +: path.drop(1))
      .getOrElse(path)

    River(updated)
  }

  def smooth: River = {
    val smoothed = path.map(_.smooth)
    River(smoothed)
  }

  def offset(diff: Point2D): River = copy(path = path.map(_.offset(diff)))

}
