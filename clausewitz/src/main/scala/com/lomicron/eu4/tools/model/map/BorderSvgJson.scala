package com.lomicron.eu4.tools.model.map

import com.lomicron.oikoumene.model.Color
import com.lomicron.utils.collection.CollectionUtils.toOption
import com.lomicron.utils.geometry.Border
import com.lomicron.utils.svg.Svg

case class BorderSvgJson
(
  left: Option[Color] = None,
  right: Option[Color] = None,
  lProv: Option[Int] = None,
  rProv: Option[Int] = None,
  path: String = "",
  classes: Seq[String] = Seq.empty,
) {

  def setLeftProv(pid: Int): BorderSvgJson = copy(left = None, lProv = pid)
  def setRightProv(pid: Int): BorderSvgJson = copy(right = None, rProv = pid)
  def addClass(c: String): BorderSvgJson = copy(classes = classes :+ c)
}

object BorderSvgJson {
  def apply(b: Border): BorderSvgJson = {
    val path = Svg.pointsToSvgLinearPath(b.points)
    if (b.left.isEmpty || b.right.isEmpty) {
      val left = b.left.orElse(b.right).map(Color(_))
      BorderSvgJson(left = left, path = path)
    } else
      BorderSvgJson(path = path, left = b.left.map(Color(_)), right = b.right.map(Color(_)))
  }
}
