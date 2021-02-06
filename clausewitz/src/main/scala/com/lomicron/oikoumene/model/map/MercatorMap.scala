package com.lomicron.oikoumene.model.map

import com.lomicron.oikoumene.model.map.spherical.SphericalMap
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

  def recalcCurves: MercatorMap = {
    case class BConf(id: Int, path: Seq[TPath] = Seq.empty)

    val bbc = borders.zipWithIndex
      .map { case (b, i) =>
        val path = SchneidersFitter(b.points).fit()
        (b.withPath(path), BConf(i, path))
      }
    val bs = bbc.map(_._1)
    val bconfigs = bbc.toMap

    val ps = provinces
      .map(p => {
        if (p.provId.contains(20) && p.borders.head.points.head == Point2D(2943.0, 220.5))
          println()
        val path = p.borders
          .flatMap(b => {
            val confPath = bconfigs.get(b).map(_.path).getOrElse(SchneidersFitter(b.points).fit())
            if (confPath.head.points.head == b.points.head) confPath
            else confPath.map(_.reverse).reverse
          })
        p.withPath(path)
      })

    val rs = rivers
      .map(r => {
        val segs = r.path
          .map(seg => {
            val path = SchneidersFitter(seg.points).fit()
            seg.withPath(path)
          })
        r.copy(path = segs)
      })

    copy(provinces = ps, borders = bs, rivers = rs)
  }

}
