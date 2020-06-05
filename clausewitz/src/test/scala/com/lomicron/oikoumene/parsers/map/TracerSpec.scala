package com.lomicron.oikoumene.parsers.map

import javax.imageio.ImageIO
import org.specs2.mutable.Specification

class TracerSpec extends Specification {

  val nestedTest = "tracer_test.bmp"
  val aegeanTest = "aegean_test.bmp"

  "Tracer#trace" should {

    "- identify nested shapes" >> {
      val url = getClass.getClassLoader.getResource(nestedTest).toURI.toURL
      val img = ImageIO.read(url)
      val polygons = Tracer.trace(img)
      polygons.size must equalTo(7)
      polygons.map(_.color).distinct.size must equalTo(5)
    }

    "- identify Euboea in Aegean" >> {
      val url = getClass.getClassLoader.getResource(aegeanTest).toURI.toURL
      val img = ImageIO.read(url)
      val polygons = Tracer.trace(img)
      polygons.size must equalTo(17)
      polygons.map(_.color).distinct.size must equalTo(7)
    }

  }

}
