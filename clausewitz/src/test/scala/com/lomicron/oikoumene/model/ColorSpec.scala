package com.lomicron.oikoumene.model

import org.specs2.mutable.Specification

class ColorSpec extends Specification {

  "Color#toInt" should {

    "- return the same value that was provided to constructor" >> {
      val toColor = -13775925
      val fromColor = Color(toColor).toInt
      toColor must beEqualTo(fromColor)
    }

  }


}
