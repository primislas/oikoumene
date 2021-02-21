package com.lomicron.utils.geometry

import com.lomicron.utils.json.JsonMapper
import org.specs2.mutable.Specification

class Point2DSpec extends Specification {
  val jsonArray = "[0.1,0.2]"
  val jsonObj = """{"x": 0.1, "y": 0.2}"""

  "Point2D" should {

    "- be serialized to json array" >> {
      val x = 0.1
      val y = 0.2
      val p = Point2D(x, y)
      val json = JsonMapper.toJson(p)
      json mustEqual jsonArray
    }

    "- be serialized from Seq" >> {
      val points = Seq(Point2D(0.0, 0.0), Point2D(100.0, 0.0), Point2D(100.0, 11.0))
      val json = JsonMapper.toJson(points)
      json mustEqual "[[0.0,0.0],[100.0,0.0],[100.0,11.0]]"
    }

    "- be deserialized from json array" >> {
      val p = Point2D.fromJson(jsonArray)
      p.x mustEqual 0.1
      p.y mustEqual 0.2
    }

    "- be deserialized from json object" >> {
      val p = Point2D.fromJson(jsonObj)
      p.x mustEqual 0.1
      p.y mustEqual 0.2
    }

  }

}
