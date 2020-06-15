package com.lomicron.oikoumene.parsers.map

case class Shape
(
  borders: Seq[Border] = Seq.empty,
  provColor: Option[Int] = None,
  provId: Option[Int] = None,
  groupId: Option[Int] = None,
  polygon: Option[Polygon] = None,
  clip: Seq[Polygon] = Seq.empty,
) {

  def withProvinceId(id: Int): Shape = {
    val oid = Some(id)
    val p = polygon.map(_.copy(provinceId = oid))
    copy(provId = oid, polygon = p)
  }

  def withPolygon: Shape =
    if (polygon.nonEmpty) this
    else {
      val ps = borders
        .map(_.points)
        .map(_.drop(1))
        .reduce(_ ++ _)
      val outline = ps.last +: ps.take(ps.size - 1)
      val p = Polygon(outline, provColor.getOrElse(-1), provId)
      copy(polygon = Some(p))
    }

  def isClipped: Boolean = clip.nonEmpty

}
