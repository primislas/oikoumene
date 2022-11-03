package com.lomicron.utils.geometry

import com.lomicron.utils.collection.Emptiable

case class Shape
(
  borders: Seq[Border] = Seq.empty,
  provColor: Option[Int] = None,
  provId: Option[Int] = None,
  groupId: Option[Int] = None,
  polygon: Option[Polygon] = None,
  path: Seq[TPath] = Seq.empty,
  clip: Seq[Polygon] = Seq.empty,
) extends Emptiable
{

  override def isEmpty: Boolean = !polygon.exists(_.nonEmpty)

  def withProvinceId(id: Int): Shape = {
    val oid = Some(id)
    val p = polygon.map(_.copy(provinceId = oid))
    copy(provId = oid, polygon = p)
  }

  def withPolygon: Shape =
    if (polygon.nonEmpty)
      if (clip.nonEmpty && polygon.exists(_.clip.isEmpty))
        copy(polygon = polygon.map(_.copy(clip = clip)))
      else
        this
    else {
      val ps = borders
        .map(_.points)
        .map(_.drop(1))
        .reduce(_ ++ _)
      val outline = ps.last +: ps.take(ps.size - 1)
      val p = Polygon(outline, provColor.getOrElse(-1), provId, clip)
      copy(polygon = Some(p))
    }

  def withPath(path: Seq[TPath]): Shape =
    copy(path = path)

  def offset(diff: Point2D): Shape = {
    val obs = borders.map(_.offset(diff))
    val op = polygon.map(_.offset(diff))
    copy(borders = obs, polygon = op)
  }

  def reflectY(height: Double): Shape = {
    val bs = borders.map(_.reflectY(height))
    val p = polygon.map(_.reflectY(height))
    val cs = clip.map(_.reflectY(height))
    copy(borders = bs, polygon = p, clip = cs)
  }

  def isClipped: Boolean = clip.nonEmpty

}
