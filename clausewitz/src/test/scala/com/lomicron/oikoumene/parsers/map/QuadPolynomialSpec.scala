package com.lomicron.oikoumene.parsers.map

import org.specs2.mutable.Specification

class QuadPolynomialSpec extends Specification {

  "QuadPolynomialSpec" should {

    "- correctly evaluate approximate distance" >> {
      val curve = QuadPolynomial(Array(0, 36.03, -0.001))
      val distance = curve.distance(4737, 4768)
      distance mustEqual 10.0
    }

  }


}
