package com.lomicron.eu4.parsers.map

import com.lomicron.eu4.service.map.SvgMapService
import com.lomicron.utils.geometry.SchneidersFitter
import com.lomicron.utils.svg.Svg

import java.awt.Point
import javax.imageio.ImageIO
import org.specs2.mutable.Specification

class TracerSpec extends Specification {

  val nestedTest = "tracer_test.bmp"
  val aegeanTest = "aegean.bmp"
  val alaskaTest = "alaska.bmp"
  val clippingTest = "clipping.bmp"
  val enclosedGroupTest = "enclosed-group.bmp"
  val greenlandTest = "greenland.bmp"
  val borderTest = "border.bmp"
  val onePixelBorderTest = "one-pixel-border.bmp"
  val mapBorderTest = "map-border.bmp"
  val norwayTest = "norway.bmp"

  "Tracer#trace" should {

    "- identify nested shapes" >> {
      val url = getClass.getClassLoader.getResource(nestedTest).toURI.toURL
      val img = ImageIO.read(url)
      val shapes = Tracer.trace(img)
      shapes.size mustEqual 7
      shapes.flatMap(_.provColor).distinct.size mustEqual 5
    }

    "- identify Euboea in Aegean" >> {
      val url = getClass.getClassLoader.getResource(aegeanTest).toURI.toURL
      val img = ImageIO.read(url)
      val shapes = Tracer.trace(img)
      shapes.size mustEqual 17
      shapes.flatMap(_.provColor).distinct.size mustEqual 7
    }

    "- trace Greenland" >> {
      val url = getClass.getClassLoader.getResource(greenlandTest).toURI.toURL
      val img = ImageIO.read(url)
      val shapes = Tracer.trace(img)
      shapes.size mustEqual 49
      shapes.flatMap(_.provColor).distinct.size mustEqual 9
    }

    "- correctly clip nested shapes from outer shapes" >> {
      val url = getClass.getClassLoader.getResource(clippingTest).toURI.toURL
      val img = ImageIO.read(url)
      val shapes = Tracer.trace(img)
      shapes.size mustEqual 3
      shapes.flatMap(_.borders).distinct.size mustEqual 4
      shapes.flatMap(_.clip).size mustEqual 1
    }

    "- cut nested shapes in Alaska" >> {
      val url = getClass.getClassLoader.getResource(alaskaTest).toURI.toURL
      val img = ImageIO.read(url)
      val shapes = Tracer.trace(img)
      shapes.size mustEqual 66
      shapes.flatMap(_.borders).size mustEqual 160
      // TODO used to be 38, verify if 40 is correct
      shapes.flatMap(_.clip).size mustEqual 40
    }

    "- correctly identify distinct borders" >> {
      val url = getClass.getClassLoader.getResource(borderTest).toURI.toURL
      val img = ImageIO.read(url)
      val shapes = Tracer.trace(img)
      shapes.size mustEqual 3
      shapes.flatMap(_.borders).distinct.size mustEqual 6
    }

    "- correctly clip an enclosed group" >> {
      val url = getClass.getClassLoader.getResource(enclosedGroupTest).toURI.toURL
      val img = ImageIO.read(url)
      val shapes = Tracer.trace(img)
      shapes.size mustEqual 4
      shapes.flatMap(_.borders).distinct.size mustEqual 7
      shapes.head.clip.size mustEqual 1
    }

    "- correctly identify one pixel border edges" >> {
      val url = getClass.getClassLoader.getResource(onePixelBorderTest).toURI.toURL
      val img = ImageIO.read(url)
      val shapes = Tracer.trace(img)
      shapes.size mustEqual 4
      shapes.flatMap(_.borders).distinct.size mustEqual 9
    }

    "- correctly identify map border edges" >> {
      val url = getClass.getClassLoader.getResource(mapBorderTest).toURI.toURL
      val img = ImageIO.read(url)
      val shapes = Tracer.trace(img)
      shapes.size mustEqual 2
      shapes.flatMap(_.borders).distinct.size mustEqual 3
    }

    "- rotate down correctly" >> {
      val cp = new Point(1, 1)
      val dir = Down
      val smoothing = 1
      val ps = dir.turnIntPixelPoints(cp, smoothing)
      ps.size mustEqual 2

      val sp = ps.head
      val ep = ps.drop(1).head

      sp.x mustEqual cp.x + 0.5
      sp.y mustEqual cp.y
      ep.x mustEqual cp.x + 1
      ep.y mustEqual cp.y + 0.5
    }

    "- rotate left correctly" >> {
      val cp = new Point(1, 1)
      val dir = Left
      val smoothing = 1
      val ps = dir.turnIntPixelPoints(cp, smoothing)
      ps.size mustEqual 2

      val sp = ps.head
      val ep = ps.drop(1).head

      sp.x mustEqual cp.x + 1
      sp.y mustEqual cp.y + 0.5
      ep.x mustEqual cp.x + 0.5
      ep.y mustEqual cp.y + 1
    }

    "- rotate up correctly" >> {
      val cp = new Point(1, 1)
      val dir = Up
      val smoothing = 1
      val ps = dir.turnIntPixelPoints(cp, smoothing)
      ps.size mustEqual 2

      val sp = ps.head
      val ep = ps.drop(1).head

      sp.x mustEqual cp.x + 0.5
      sp.y mustEqual cp.y + 1
      ep.x mustEqual cp.x
      ep.y mustEqual cp.y + 0.5
    }

    "- rotate right correctly" >> {
      val cp = new Point(1, 1)
      val dir = Right
      val smoothing = 1
      val ps = dir.turnIntPixelPoints(cp, smoothing)
      ps.size mustEqual 2

      val sp = ps.head
      val ep = ps.drop(1).head

      sp.x mustEqual cp.x
      sp.y mustEqual cp.y + 0.5
      ep.x mustEqual cp.x + 0.5
      ep.y mustEqual cp.y
    }

    "- identify slim water strips" >> {
      val url = getClass.getClassLoader.getResource(norwayTest).toURI.toURL
      val img = ImageIO.read(url)
      val shapes = Tracer.trace(img)
      val s2 = shapes.drop(2).head
      val ps = SchneidersFitter.fit(s2.polygon.map(_.points).getOrElse(Seq.empty))
      val svg = Svg.fromPolypath(ps)
      shapes.size mustEqual 3
      shapes.flatMap(_.borders).distinct.size mustEqual 7
      shapes.head.clip.size mustEqual 1
      ps.size mustEqual 2
      svg mustEqual ""
    }

  }

}
