package com.lomicron.eu4.model.map

import com.lomicron.eu4.model.map.spherical.SphericalMap
import com.lomicron.utils.geometry._

case class MercatorMap
(
  provinces: Seq[Shape] = Seq.empty,
  borders: Seq[Border] = Seq.empty,
  rivers: Seq[River] = Seq.empty,
  width: Int = 0,
  height: Int = 0,
) {

  def toSphere: SphericalMap = SphericalMap.ofMercator(this)

}
