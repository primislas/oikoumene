package com.lomicron.oikoumene.parsers.map

import java.awt.Point

case class River(path: Seq[Point2D] = Seq.empty, width: Int = RiverTypes.NARROWEST)

object River {

  def ofInts(ps: Seq[Point], width: Int): River =
    new River(ps.map(Point2D(_)), width)

}
