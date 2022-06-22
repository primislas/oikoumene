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
    val ocs = clip.map(_.offset(diff))
    val op = polygon.map(_.offset(diff))
    copy(borders = obs, clip = ocs, polygon = op)
  }

  def rotate(angle: Double, rotationCenter: Point2D = Point2D.ZERO): Shape = {
    val rbs = borders.map(_.rotate(rotationCenter, angle))
    val rcs = clip.map(_.rotate(rotationCenter, angle))
    val rp = polygon.map(_.rotate(rotationCenter, angle))
    copy(borders = rbs, clip = rcs, polygon = rp)
  }

  def scale(coef: Double): Shape = {
    val sbs = borders.map(_.scale(coef))
    val scs = clip.map(_.scale(coef))
    val sp = polygon.map(_.scale(coef))
    copy(borders = sbs, clip = scs, polygon = sp)
  }

  def isClipped: Boolean = clip.nonEmpty

}
