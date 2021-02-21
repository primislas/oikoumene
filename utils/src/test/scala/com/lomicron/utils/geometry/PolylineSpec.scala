package com.lomicron.utils.geometry

import com.lomicron.utils.json.JsonMapper
import org.specs2.mutable.Specification

class PolylineSpec extends Specification {

  val points: Array[Point2D] = Array(Point2D(1.1, 1.2), Point2D(2.1, 2.2), Point2D(3.1, 3.2), Point2D(4.1, 4.2))
  val polyline: Polyline = Polyline(Seq(points(0), points(1), points(2), points(3)))
  val polylineArrayJson = "[[1.1,1.2],[2.1,2.2],[3.1,3.2],[4.1,4.2]]"
  val polylineJson = s"""{"polyline":$polylineArrayJson}"""

  "Polyline" should {

    "- correctly serialize polylines" >> {
      val json = JsonMapper.toJson(polyline)
      json mustEqual polylineJson
    }

    "- correctly deserialize polylines" >> {
      val curve = JsonMapper.fromJson[Polyline](polylineJson)
      curve mustEqual polyline
    }

    "- correctly deserialize polylines from array" >> {
      val curve = JsonMapper.fromJson[Polyline](polylineArrayJson)
      curve mustEqual polyline
    }

  }

}
