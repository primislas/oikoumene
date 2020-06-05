package com.lomicron.oikoumene.parsers.map

import com.lomicron.oikoumene.model.Color
import com.lomicron.oikoumene.model.provinces.{Province, ProvinceTypes}
import com.lomicron.oikoumene.repository.api.RepositoryFactory
import com.lomicron.utils.collection.CollectionUtils.toOption

case class WorldMap
(
  private val sphere: SphericalMap,
  private val repos: RepositoryFactory
) {

  val uncolonizedProvinceColor: Color = Color(165, 152, 144)
  val impassableTerrainColor: Color = Color(145, 132, 124)

  def project
  (
    mapMode: String,
    rotation: Option[SphericalCoord] = None,
  ): Seq[Polygon] =
    rotation
      .map(sphere.rotate)
      .getOrElse(sphere)
      .project
      .map(setMapMode(_, mapMode))

  def setMapMode(p: Polygon, mapMode: String): Polygon =
    p.provinceId
      .flatMap(repos.provinces.find(_).toOption)
      .flatMap(mapModeColor(_, mapMode))
      .map(c => p.copy(color = c.toInt))
      .getOrElse(p)

  def mapModeColor(p: Province, mapMode: String): Color = {
    val c = mapMode match {
      case MapModes.POLITICAL => ownerColor(p)
      case MapModes.TRADE_NODES => tradeNodeColor(p)
      case MapModes.SIMPLE_TERRAIN => simpleTerrainColor(p)
      case _ => Option(p.color)
    }
    c.getOrElse(defaultColor(p))
  }

  def ownerColor(p: Province): Option[Color] =
    p.state.owner.flatMap(repos.tags.find(_).toOption).map(_.color)

  def tradeNodeColor(p: Province): Option[Color] =
    p.geography.tradeNode
      .flatMap(repos.tradeNodes.find(_).toOption)
      .flatMap(_.color)

  def simpleTerrainColor(p: Province): Option[Color] =
    p.geography.terrain
      .flatMap(repos.geography.terrain.find(_).toOption)
      .flatMap(_.color)

  def defaultColor(p: Province): Color =
    p.geography.`type`.map {
      case ProvinceTypes.sea => Color(157, 239, 254)
      case ProvinceTypes.lake => Color(135, 248, 250)
      case _ => if (p.geography.isImpassable) impassableTerrainColor else uncolonizedProvinceColor
    } .getOrElse(uncolonizedProvinceColor)

}

object WorldMap {

  def apply(sphere: SphericalMap, repos: RepositoryFactory): WorldMap = {
    val updated = addProvinceMeta(sphere.polygons, repos)
    new WorldMap(SphericalMap(sphere.center, updated), repos)
  }

  def addProvinceMeta(ps: Seq[SphericalPolygon], repos: RepositoryFactory): Seq[SphericalPolygon] = {
    val psByColor = repos.provinces.findAll.groupBy(_.color)

    ps
      .map(poly => psByColor
        .get(Color(poly.color))
        .flatMap(_.headOption)
        .map(p => poly.copy(provinceId = p.id))
        .getOrElse(poly)
      )
  }

}
