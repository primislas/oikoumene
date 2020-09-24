package com.lomicron.utils.geometry

import org.specs2.mutable.Specification

class QuadPolynomialSpec extends Specification {

  "QuadPolynomialSpec" should {

    "- correctly evaluate approximate line distance" >> {
      val curve = QuadPolynomial(5, 0, 0)
      val distance = curve.distance(0, 1)
      distance mustEqual 1.0
    }

    "- correctly evaluate approximate angled line distance" >> {
      val curve = QuadPolynomial(1, 4.0 / 3.0, 0)
      val distance = curve.distance(0, 3)
      distance mustEqual 5.0
    }

    "- correctly evaluate approximate curved distance" >> {
      val curve = QuadPolynomial(Array(0, 36.03, -0.001))
      val distance = curve.distance(4737, 4768)
      distance mustEqual 822.859147155537
    }

  }


}
