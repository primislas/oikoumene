package com.lomicron.oikoumene.parsers.map

import java.awt.Point

import scala.math.BigDecimal.RoundingMode

case class MercatorMap(provinces: Seq[Polygon], width: Int, height: Int) {

  def toSphere: SphericalMap = {

    val circumference = if (width > height) width else height
    val radius = BigDecimal(circumference) / (2 * Math.PI)
    val radiusInt = radius.setScale(0, RoundingMode.CEILING).toInt
    val center = new Point(radiusInt, radiusInt)
    val offset = new Point(radiusInt - width / 2, radiusInt - height / 2)
    Geometry.toSpherical(provinces, center, radius.toDouble, offset)
  }

}
