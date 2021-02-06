package com.lomicron.oikoumene.model.map

import com.lomicron.oikoumene.model.map.spherical.SphericalMap
import com.lomicron.utils.geometry.TPath.Polypath
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

  private def fit(points: Seq[Point2D]) = SchneidersFitter.fit(points)

  def recalcCurves: MercatorMap = {
    val bconfigs = groupBorders(borders)
    val bs = bconfigs.keys.toList

    def getBorderPath(b: Border): Polypath = {
      val confPath = bconfigs.get(b).map(_.path).getOrElse(fit(b.points))
      if (confPath.isEmpty) Seq.empty
      else if (confPath.head.points.head == b.points.head) confPath
      else confPath.map(_.reverse).reverse
    }
    def getPolygonPath(p: Polygon): Polypath =
      getBorderPath(Border(p.points))

    val ps = provinces
      .map(p => {
        val path = p.borders.flatMap(getBorderPath)
        val clipPaths = p.clip.map(p => p.withPath(getPolygonPath(p))).filter(_.path.nonEmpty)
        p.copy(path = path, clip = clipPaths)
      })

    val rs = rivers
      .map(r => {
        val segs = r.path.map(seg => seg.withPath(fit(seg.points)))
        r.copy(path = segs)
      })

    copy(provinces = ps, borders = bs, rivers = rs)
  }

  private def groupBorders(borders: Seq[Border]): Map[Border, BConf] = {
    borders
      .zipWithIndex
      .map { case (b, i) =>
        val path = fit(b.points)
        (b.withPath(path), BConf(i, path))
      }
      .toMap
  }

}

private case class BConf(id: Int, path: Seq[TPath] = Seq.empty)
