package com.lomicron.oikoumene.parsers.map

import java.awt.Point

import javax.imageio.ImageIO
import org.specs2.mutable.Specification

class TracerSpec extends Specification {

  val provincesResource = "provinces.bmp"

  "Tracer#trace" should {

    "- produce a polygon outline" >> {
      val url = getClass.getClassLoader.getResource(provincesResource).toURI.toURL
      val img = ImageIO.read(url)
      val startingPoint = new Point(124, 0)
      val polygon = Tracer(img, startingPoint).trace()
      polygon.points.size must be equalTo 58
    }

  }


}
