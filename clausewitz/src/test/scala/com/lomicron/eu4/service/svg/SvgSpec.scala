package com.lomicron.eu4.service.svg

import org.specs2.mutable.Specification

class SvgSpec extends Specification {

  "Svg#doubleToSvg" should {

    "- strip trailing zeros" >> {
      val d = 0.500
      val v = Svg.doubleToSvg(d)
      v mustEqual "0.5"
    }

    "- show no decimal part when it the number is an int" >> {
      val d = 1.000
      val v = Svg.doubleToSvg(d)
      v mustEqual "1"
    }

    "- return zero as zero" >> {
      val d = 0.0
      val v = Svg.doubleToSvg(d)
      v mustEqual "0"
    }

  }

}
