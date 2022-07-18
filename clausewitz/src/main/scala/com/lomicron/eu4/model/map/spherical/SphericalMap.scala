package com.lomicron.eu4.model.map.spherical

import com.lomicron.eu4.model.map.Map2DProjection
import com.lomicron.utils.geometry._

case class SphericalMap
(
  center: Point2D,
  radius: Double,
  provinces: Seq[SphericalShape] = Seq.empty,
  borders: Seq[SphericalBorder] = Seq.empty,
  rivers: Seq[SphericalRiver] = Seq.empty,
) {

  def rotate(c: SphericalCoord): SphericalMap =
    rotate(c.polar, c.azimuth)

  def rotate(polarRot: Double, azimuthRot: Double): SphericalMap = {
    val rps = provinces.map(_.rotate(polarRot, azimuthRot))
    val rbs = borders.map(_.rotate(polarRot, azimuthRot))
    val rrs = rivers.map(_.rotate(polarRot, azimuthRot))
    SphericalMap(center, radius, rps, rbs, rrs)
  }

  def project: Map2DProjection = {
    // TODO patch
//    val mProvs = provinces.map(_.project(center)).filter(_.nonEmpty)
//    val mBorders = borders.map(_.project(center)).filter(_.nonEmpty)
//    val mRivers = rivers.map(_.project(center)).filter(_.nonEmpty)
//    val diameter = (radius * 2).ceil.toInt
//    Map2DProjection(mProvs, mBorders, mRivers, diameter, diameter)
    Map2DProjection()
  }

}

object SphericalMap {

  def ofMercator(mercatorMap: Map2DProjection): SphericalMap = ???
//  {
//    val width = mercatorMap.width.toDouble
//    val height = mercatorMap.height.toDouble
//    val circumference = if (width > height) width else height
//    val radius = circumference / (2.0 * Math.PI)
//    val center = new Point2D(width / 2.0, height / 2.0)
//    val provinces = mercatorMap.provinces
//      .map(SphericalShape(_, center, radius))
//    val borders = mercatorMap.provinces.flatMap(_.borders).distinct
//      .map(SphericalBorder(_, center, radius))
//    val rivers = mercatorMap.rivers
//      .map(SphericalRiver(_, center, radius))
//
//    val cint = radius.ceil.toInt
//    val sphCenter = Point2D(cint, cint)
//    SphericalMap(sphCenter, radius, provinces, borders, rivers)
//  }

}
