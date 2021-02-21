package com.lomicron.utils.geometry

import com.lomicron.utils.json.JsonMapper
import org.specs2.mutable.Specification

class BezierCurveSpec extends Specification {
  val points: Array[Point2D] = Array(Point2D(1.1, 1.2), Point2D(2.1, 2.2), Point2D(3.1, 3.2), Point2D(4.1, 4.2))
  val bezier: BezierCurve = BezierCurve(points(0), points(1), points(2), points(3))
  val bezierArrayJson = "[[1.1,1.2],[2.1,2.2],[3.1,3.2],[4.1,4.2]]"
  val bezierJson = s"""{"bezier":$bezierArrayJson}"""

  "BezierCurve" should {

    "- correctly serialize bezier curves" >> {
      val json = JsonMapper.toJson(bezier)
      json mustEqual bezierJson
    }

    "- correctly deserialize bezier curves" >> {
      val curve = JsonMapper.fromJson[BezierCurve](bezierJson)
      curve mustEqual bezier
    }

    "- correctly deserialize bezier curves from array" >> {
      val curve = JsonMapper.fromJson[BezierCurve](bezierArrayJson)
      curve mustEqual bezier
    }

  }

}
