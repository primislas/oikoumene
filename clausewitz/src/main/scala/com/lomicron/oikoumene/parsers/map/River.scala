package com.lomicron.oikoumene.parsers.map

import java.awt.Point

case class River(path: Seq[RiverSegment] = Seq.empty) {

  def isEmpty: Boolean = path.isEmpty

  def reverse: River = River(path.map(_.reverse))

  def addStartingPoint(p: Point): River = {
    val updated = path.headOption
      .map(_.withStartingPoint(p))
      .map(s => s +: path.drop(1))
      .getOrElse(path)

    River(updated)
  }

}
