package com.lomicron.oikoumene.parsers.map

import com.lomicron.oikoumene.service.map.SvgMapStyles
import com.lomicron.oikoumene.service.svg.{Svg, SvgElements}
import com.lomicron.utils.geometry.SchneidersFitter
import org.specs2.mutable.Specification

import javax.imageio.ImageIO

class SchneidersFitterSpec extends Specification {

  val aegeanTest = "aegean.bmp"


  "ShneidersFitter" should {

    "- fit Aegean curves" >> {
      val url = getClass.getClassLoader.getResource(aegeanTest).toURI.toURL
      val img = ImageIO.read(url)
      val shapes = Tracer.trace(img)
      val borders = shapes.flatMap(_.borders).distinct
      val curves = borders.map(b => SchneidersFitter(b.points.toVector).fit(1.5))
      val svgPaths = curves
        .map(Svg.bezierCurvesToPath(_))
        .map(path => SvgElements.path.copy(path = Some(path)).addClass("border"))
      val style = SvgElements.style.addContent(SvgMapStyles.borderStyle)
      println(Svg.svgHeader.add(style).add(svgPaths).toSvg)
      curves.size shouldEqual(borders.size)
    }

  }

}
