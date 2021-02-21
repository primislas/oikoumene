package com.lomicron.utils.geometry

import com.lomicron.utils.json.JsonMapper
import org.specs2.mutable.Specification

class TPathSpec extends Specification {
  val points: Array[Point2D] = Array(Point2D(1.1, 1.2), Point2D(2.1, 2.2), Point2D(3.1, 3.2), Point2D(4.1, 4.2))
  val bezier: TPath = BezierCurve(points(0), points(1), points(2), points(3))
  val bezierJson = """{"bezier":[[1.1,1.2],[2.1,2.2],[3.1,3.2],[4.1,4.2]]}"""
  val polyline: TPath = Polyline(Seq(points(0), points(1), points(2)))
  val polylinJson = """{"polyline":[[1.1,1.2],[2.1,2.2],[3.1,3.2]]}"""

  "TPath" should {

    "- correctly serialize bezier curves" >> {
      val json = JsonMapper.toJson(bezier)
      json mustEqual bezierJson
    }

    "- correctly deserialize bezier curves" >> {
      val curve = JsonMapper.fromJson[TPath](bezierJson)
      curve mustEqual bezier
    }

    "- correctly serialize polylines" >> {
      val json = JsonMapper.toJson(polyline)
      json mustEqual polylinJson
    }

    "- correctly deserialize polylines" >> {
      val pl = JsonMapper.fromJson[TPath](polylinJson)
      pl mustEqual polyline
    }

  }

}
