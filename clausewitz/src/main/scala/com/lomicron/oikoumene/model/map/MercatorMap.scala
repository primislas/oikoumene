package com.lomicron.oikoumene.model.map

import java.awt.Point

import com.lomicron.utils.geometry.{Geometry, Shape, SphericalMap}

import scala.math.BigDecimal.RoundingMode

case class MercatorMap(provinces: Seq[Shape] = Seq.empty, width: Int = 0, height: Int = 0) {

  def toSphere: SphericalMap = {

    val circumference = if (width > height) width else height
    val radius = BigDecimal(circumference) / (2 * Math.PI)
    val radiusInt = radius.setScale(0, RoundingMode.CEILING).toInt
    val center = new Point(radiusInt, radiusInt)
    val offset = new Point(radiusInt - width / 2, radiusInt - height / 2)
    Geometry.toSpherical(provinces.flatMap(_.polygon), center, radius.toDouble, offset)
  }

}
