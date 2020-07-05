package com.lomicron.utils.geometry

import org.specs2.mutable.Specification

class BorderSpec extends Specification {

  "Border" should {

    "- recognize identical border by neighbors and points" >> {
      val i1 = Some(1)
      val i2 = Some(2)

      val aps = Seq(Point2D(0,0), Point2D(1,0), Point2D(2,0))
      val a = Border(aps, i1, i2)

      val bps = aps.reverse
      val b = Border(bps, i2, i1)

      a.hashCode() mustEqual b.hashCode()
      a.equals(b) must be
      a mustEqual b
      Set(a,b).size mustEqual 1
      Seq(a,b).distinct.size mustEqual 1
    }


  }

}
