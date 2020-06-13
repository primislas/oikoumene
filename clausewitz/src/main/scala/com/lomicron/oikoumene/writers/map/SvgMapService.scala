package com.lomicron.oikoumene.writers.map

import com.lomicron.oikoumene.model.Color
import com.lomicron.oikoumene.model.provinces.{Province, ProvinceTypes}
import com.lomicron.oikoumene.parsers.map.{MapModes, MercatorMap, Polygon, WorldMap}
import com.lomicron.oikoumene.repository.api.RepositoryFactory
import com.lomicron.oikoumene.writers.svg.{Svg, SvgElement, SvgFill, SvgTags}
import com.lomicron.utils.collection.CollectionUtils.toOption

import scala.collection.immutable.ListSet

case class SvgMapService(repos: RepositoryFactory) {

  val uncolonizedColor: Color = Color(165, 152, 144)
  val wastelandColor: Color = Color(145, 132, 124)
  val seaColor: Color = Color(157, 239, 254)
  val lakeColor: Color = Color(135, 248, 250)

  val provinceGroup: SvgElement = SvgElement(
    tag = SvgTags.GROUP,
    id = "provinces",
  )
  val defaultMapStyle: SvgElement = SvgElement(
    tag = SvgTags.STYLE,
    customContent = Some(
      s"""
        |.province {
        |  stroke: rgb(0,0,0);
        |  stroke-width: 1;
        |  stroke-opacity: 0.2;
        |  stroke-linecap: round;
        |  opacity: 0.6;
        |}
        |.wasteland {
        |  fill: ${Svg.colorToSvg(wastelandColor)};
        |  opacity: 0.0;
        |}
        |.uncolonized {
        |  fill: ${Svg.colorToSvg(uncolonizedColor)};
        |  opacity: 0.0;
        |}
        |.sea {
        |  fill: ${Svg.colorToSvg(seaColor)};
        |  opacity: 0.3;
        |}
        |.lake {
        |  fill: ${Svg.colorToSvg(lakeColor)};
        |  opacity: 0.3;
        |}
      """.stripMargin)
  )
  val physicalMapStyle: SvgElement = SvgElement(
    tag = SvgTags.STYLE,
    customContent = Some(
      """
        |.province {
        |  stroke: rgb(0,0,0);
        |  stroke-width: 1;
        |  stroke-linecap: round;
        |  stroke-opacity: 0.3;
        |  fill: none;
        |}
        |""".stripMargin)
  )

  def worldSvg(worldMap: WorldMap, mapMode: Option[String] = None): String = {
    val withMode = for {
      mercator <- worldMap.mercator
      mode <- mapMode
    } yield ofMode(mercator, mode)

    val style = if (mapMode.contains(MapModes.TERRAIN)) physicalMapStyle else defaultMapStyle

    Svg.svgHeader
      .add(style)
      .add(withMode.toSeq)
      .toSvg
  }

  def ofMode(map: MercatorMap, mapMode: String): SvgElement = {
    val ps = map.provinces.map(provinceToSvgElem(_, mapMode))
    provinceGroup.add(ps)
  }

  def provinceToSvgElem(polygon: Polygon, mapMode: String): SvgElement = {
    polygon.provinceId
      .flatMap(repos.provinces.find(_).toOption)
      .map(prov =>
        defaultProvincePolygon(polygon).copy(
          classes = SvgMapClasses.of(prov),
          fill = mapModeColor(prov, mapMode).map(SvgFill(_)),
        )
      )
      .getOrElse(defaultProvincePolygon(polygon))
  }

  def defaultProvincePolygon(polygon: Polygon): SvgElement =
    SvgElement(
      tag = SvgTags.POLYGON,
      id = polygon.provinceId.map(_.toString),
      classes = ListSet(SvgMapClasses.PROVINCE),
      points = polygon.points,
    )

  def mapModeColor(p: Province, mapMode: String): Option[Color] =
    mapMode match {
      case MapModes.POLITICAL => ownerColor(p)
      case MapModes.TRADE_NODES => tradeNodeColor(p)
      case MapModes.SIMPLE_TERRAIN => simpleTerrainColor(p)
      case _ => None
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
      case _ => if (p.geography.isImpassable) wastelandColor else uncolonizedColor
    }.getOrElse(uncolonizedColor)


}
