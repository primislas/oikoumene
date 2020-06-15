package com.lomicron.oikoumene.parsers.map

import java.awt.Point

import javax.imageio.ImageIO
import org.specs2.mutable.Specification

class TracerSpec extends Specification {

  val nestedTest = "tracer_test.bmp"
  val aegeanTest = "aegean.bmp"
  val greenlandTest = "greenland.bmp"

  "Tracer#trace" should {

    "- identify nested shapes" >> {
      val url = getClass.getClassLoader.getResource(nestedTest).toURI.toURL
      val img = ImageIO.read(url)
      val shapes = Tracer.trace(img)
      shapes.size must equalTo(7)
      shapes.flatMap(_.provColor).distinct.size must equalTo(5)
    }

    "- identify Euboea in Aegean" >> {
      val url = getClass.getClassLoader.getResource(aegeanTest).toURI.toURL
      val img = ImageIO.read(url)
      val shapes = Tracer.trace(img)
      shapes.size must equalTo(17)
      shapes.flatMap(_.provColor).distinct.size must equalTo(7)
    }

    "- trace Greenland" >> {
      val url = getClass.getClassLoader.getResource(greenlandTest).toURI.toURL
      val img = ImageIO.read(url)
      val shapes = Tracer.trace(img)
      shapes.size must equalTo(49)
      shapes.flatMap(_.provColor).distinct.size must equalTo(9)
    }

    "- rotate down correctly" >> {
      val cp = new Point(1, 1)
      val dir = Down
      val smoothing = 1
      val ps = dir.turnIntPixelPoints(cp, smoothing)
      ps.size must equalTo(2)

      val sp = ps.head
      val ep = ps.drop(1).head

      sp.x must equalTo(cp.x + 0.5)
      sp.y must equalTo(cp.y)
      ep.x must equalTo(cp.x + 1)
      ep.y must equalTo(cp.y + 0.5)
    }

    "- rotate left correctly" >> {
      val cp = new Point(1, 1)
      val dir = Left
      val smoothing = 1
      val ps = dir.turnIntPixelPoints(cp, smoothing)
      ps.size must equalTo(2)

      val sp = ps.head
      val ep = ps.drop(1).head

      sp.x must equalTo(cp.x + 1)
      sp.y must equalTo(cp.y + 0.5)
      ep.x must equalTo(cp.x + 0.5)
      ep.y must equalTo(cp.y + 1)
    }

    "- rotate up correctly" >> {
      val cp = new Point(1, 1)
      val dir = Up
      val smoothing = 1
      val ps = dir.turnIntPixelPoints(cp, smoothing)
      ps.size must equalTo(2)

      val sp = ps.head
      val ep = ps.drop(1).head

      sp.x must equalTo(cp.x + 0.5)
      sp.y must equalTo(cp.y + 1)
      ep.x must equalTo(cp.x)
      ep.y must equalTo(cp.y + 0.5)
    }

    "- rotate right correctly" >> {
      val cp = new Point(1, 1)
      val dir = Right
      val smoothing = 1
      val ps = dir.turnIntPixelPoints(cp, smoothing)
      ps.size must equalTo(2)

      val sp = ps.head
      val ep = ps.drop(1).head

      sp.x must equalTo(cp.x)
      sp.y must equalTo(cp.y + 0.5)
      ep.x must equalTo(cp.x + 0.5)
      ep.y must equalTo(cp.y)
    }

  }

}
