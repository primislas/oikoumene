package com.lomicron.utils.geometry

import com.lomicron.utils.json.JsonMapper
import org.specs2.mutable.Specification

class SchneidersFitterSpec extends Specification {

  "SchneidersFitter" should {

    "- generate expected path" >> {
      val json = """{"borders":[{"points":[{"x":2943.0,"y":220.5},{"x":2943.5,"y":220.0},{"x":2944.0,"y":220.5},{"x":2944.0,"y":222.5},{"x":2942.5,"y":224.0},{"x":2935.5,"y":224.0},{"x":2935.0,"y":223.5},{"x":2935.5,"y":223.0},{"x":2939.5,"y":223.0},{"x":2940.5,"y":222.0},{"x":2941.5,"y":222.0},{"x":2943.0,"y":220.5}],"left":-12659972,"right":-16627712,"left_group":5272,"right_group":7013,"empty":false,"closed":true}],"prov_color":-16627712,"prov_id":20,"group_id":7013,"polygon":{"points":[{"x":2943.5,"y":220.0},{"x":2944.0,"y":220.5},{"x":2944.0,"y":222.5},{"x":2942.5,"y":224.0},{"x":2935.5,"y":224.0},{"x":2935.0,"y":223.5},{"x":2935.5,"y":223.0},{"x":2939.5,"y":223.0},{"x":2940.5,"y":222.0},{"x":2941.5,"y":222.0},{"x":2943.5,"y":220.0}],"color":-16627712,"province_id":20,"empty":false},"empty":false,"clipped":false}"""
      val shape = JsonMapper.fromJson[Shape](json)
      val points = shape.borders.head.points
      val path = SchneidersFitter.fit(points)
      path.size shouldEqual 3
    }


  }

}
