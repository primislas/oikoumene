package com.lomicron.utils.geometry

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

  "Geometry#cleanSameLinePoints" should {

    "- eliminate redundant points belonging to the same line(s)" >> {
      val ps = Seq(p(0,0), p(1, 1), p(2, 2), p(3, 3), p(4,2), p(5, 1))
      val cleaned = Geometry.cleanSameLinePoints(ps)
      cleaned.length mustEqual 3
    }

    "- account for tail points potentially belonging to the same line as starting points" >> {
      val ps = Seq(p(0,0), p(1, 1), p(3, 3), p(4,2), p(-2, -3), p(-2, -2), p(-1, -1))
      val cleaned = Geometry.cleanSameLinePoints(ps)
      cleaned.length mustEqual 4
    }

    "- don't clean up tail points for open polylines" >> {
      val ps = Seq(p(0,0), p(1, 1), p(3, 3), p(4,2), p(-2, -3), p(-2, -2), p(-1, -1))
      val isPolyline = false
      val cleaned = Geometry.cleanSameLinePoints(ps, isPolyline)
      cleaned.length mustEqual 6
    }

  }

}
