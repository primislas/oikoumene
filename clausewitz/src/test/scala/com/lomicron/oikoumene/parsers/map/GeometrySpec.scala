package com.lomicron.oikoumene.parsers.map

import org.specs2.mutable.Specification

class GeometrySpec extends Specification {

  def p(x: Int, y: Int): Point2D = Point2D(x, y)

  "Geometry#clean" should {

    "- eliminate instances of repeating double points" >> {
      val ps = Seq(p(0, 0), p(0,0), p(1,0), p(2,0), p(2,0), p(3,0), p(4,0), p(5,0), p(5,0), p(5,0))
      val cleaned = Geometry.clean(ps)
      cleaned.length mustEqual 6
    }

    "- keep clean collection intact" >> {
      val ps = Seq(p(0, 0), p(1,0), p(3,0), p(5,0))
      val cleaned = Geometry.clean(ps)
      cleaned.length mustEqual 4
    }

  }


}
