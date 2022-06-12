package com.lomicron.imperator.service.svg

import com.lomicron.eu4.model.map.MapModes
import com.lomicron.eu4.service.map.MapBuilderSettings
import com.lomicron.imperator.repository.api.RepositoryFactory
import com.lomicron.oikoumene.model.Color
import com.lomicron.utils.collection.CollectionUtils.toOption
import com.lomicron.utils.geometry.Point2D
import com.lomicron.utils.svg.{Svg, SvgElement, SvgElements, SvgFill, SvgTags}

object SvgMapStyles {
  val uncolonizedColor: Color = Color(165, 152, 144)
  val wastelandColor: Color = Color(145, 132, 124)
  val oceanColor: Color = Color(157, 239, 254)
  val lakeColor: Color = Color(135, 248, 250)
  val riverColor: Color = Color(50, 180, 220)

  val bgWater = "https://raw.githubusercontent.com/primislas/eu4-svg-map/master/resources/colormap-water.png"
  val bgAutumn = "https://raw.githubusercontent.com/primislas/eu4-svg-map/master/resources/colormap-autumn-nowater.png"
  val bgWinter = "https://raw.githubusercontent.com/primislas/eu4-svg-map/master/resources/colormap-winter-nowater.png"
  val bgSpring = "https://raw.githubusercontent.com/primislas/eu4-svg-map/master/resources/colormap-spring-nowater.png"
  val bgSummer = "https://raw.githubusercontent.com/primislas/eu4-svg-map/master/resources/colormap-summer-nowater.png"

  def bgOfSeason(season: String): String = s"https://raw.githubusercontent.com/primislas/eu4-svg-map/master/resources/colormap-$season-nowater.png"

  val riverStyle: String =
    s""".river {
       |  fill:none;
       |  stroke-linecap:round;
       |  stroke-linejoin:round;
       |  opacity:0.3;
       |}
       |.river-narrowest { stroke:${Svg.colorToSvg(riverColor)}; stroke-width:1; }
       |.river-narrow { stroke:${Svg.colorToSvg(riverColor)}; stroke-width:1.7; }
       |.river-wide { stroke:${Svg.colorToSvg(riverColor)}; stroke-width:2.4; }
       |.river-widest { stroke:${Svg.colorToSvg(riverColor)}; stroke-width:3; }
       |""".stripMargin
  val borderStyle: String =
    s""".border {
       |  fill: none;
       |  stroke-width: 1;
       |  stroke-linecap: round;
       |  stroke-linejoin: round;
       |  stroke: rgb(50,50,50);
       |  stroke-opacity: 0;
       |}
       |.border-land { stroke:rgb(50,50,50); stroke-opacity:0.1; }
       |.border-map { stroke:rgb(100,50,0); stroke-width:0; stroke-opacity:0.4; }
       |.border-country { stroke:rgb(50,50,50); stroke-width:2; stroke-opacity:0.6; }
       |.border-country-shore { stroke:rgb(50,175,200); stroke-opacity:0.4; stroke-width:1; }
       |.border-land-area { stroke:rgb(50,50,50); stroke-opacity:0.2; stroke-width:1; }
       |.border-sea { stroke:rgb(0,0,50); stroke-opacity:0.1; }
       |.border-sea-area { stroke:rgb(0,0,50); stroke-opacity:0.2; }
       |.border-sea-shore { stroke:rgb(50,175,200); stroke-opacity:0.4; stroke-width:1; }
       |.border-lake-shore { stroke:rgb(50,200,200); stroke-opacity:0.4; stroke-width:1; }
       |""".stripMargin
  val nameStyle: String =
    s"""@import url("https://fonts.googleapis.com/css2?family=Dosis:wght@800");
       |.tn {
       |  font-family: 'Dosis';
       |  paint-order: stroke;
       |  fill: black;
       |  stroke: white;
       |  stroke-opacity: 0.3;
       |  stroke-width: 1;
       |  font-weight: bold;
       |  opacity: 0.9;
       |}
       |.tn-tiny { stroke-width: 0; }
       |""".stripMargin

  val politicalProvinceStyle: String =
    s""".province { stroke-width:0; fill:none; opacity:0.7; }
       |.city { stroke-width:0; opacity:0.7; }
       |.wasteland { fill:${Svg.colorToSvg(wastelandColor)}; opacity:0.1; }
       |.uncolonized { fill:${Svg.colorToSvg(uncolonizedColor)}; opacity:0; }
       |.sea { fill:${Svg.colorToSvg(oceanColor)}; opacity:0.3; }
       |.lake { fill:${Svg.colorToSvg(lakeColor)}; opacity:0.5; }
       |.elevated-lake { fill:${Svg.colorToSvg(lakeColor)}; opacity:0; }
       |""".stripMargin
  val outlineProvinceStyle: String =
    s""".province { stroke:black; stroke-width:0.5; opacity:1; }
       |.city { fill:${Svg.colorToSvg(uncolonizedColor)}; }
       |.wasteland { fill:${Svg.colorToSvg(wastelandColor)}; }
       |.uncolonized { fill:${Svg.colorToSvg(uncolonizedColor)}; }
       |.sea { fill:${Svg.colorToSvg(oceanColor)}; }
       |.lake { fill:${Svg.colorToSvg(lakeColor)}; }
       |.elevated-lake { fill:${Svg.colorToSvg(lakeColor)}; }""".stripMargin
  val terrainProvinceStyle: String =
    s""".province {
       |  stroke: black;
       |  stroke-width: 1;
       |  stroke-linecap: round;
       |  fill: none;
       |}
       |""".stripMargin

  val politicalMapStyle: SvgElement = SvgElement(
    tag = SvgTags.STYLE,
    customContent = Some(Seq(nameStyle, politicalProvinceStyle, riverStyle, borderStyle).mkString("\n"))
  )

  val terrainMapStyle: SvgElement = SvgElements
    .style
    .addContent(terrainProvinceStyle)

  private val validSeasons = Set("autumn", "winter", "spring", "summer")

  def styleOf(settings: MapBuilderSettings, repos: RepositoryFactory): SvgElement = {
    var style = SvgElements.style
    if (settings.withNames) style = style.addContent(nameStyle)
    val provStyle = settings.mapMode match {
      case MapModes.PROVINCE_OUTLINE => outlineProvinceStyle
      case MapModes.TERRAIN => terrainProvinceStyle
      case _ => politicalProvinceStyle
    }
    style = style.addContent(provStyle)
    if (settings.withRivers) style = style.addContent(riverStyle)
    if (settings.withBorders) style = style.addContent(borderStyle)

    val modeStyle = settings.mapMode match {
      case MapModes.POLITICAL => buildTagStyles(repos)
      case MapModes.SIMPLE_TERRAIN => buildTerrainStyles(repos)
      case _ => ""
    }

    style.addContent(modeStyle)
  }

  def buildTagStyles(repos: RepositoryFactory): String =
    repos
      .tags
      .distinctProvinceOwners
      .map(tag => s".${tag.id} { fill:${Svg.colorToSvg(tag.color)} }")
      .mkString("\n")

  def buildTerrainStyles(repos: RepositoryFactory): String = ???

  def background(season: String, repos: RepositoryFactory): Seq[SvgElement] = {
    val mercator = repos.geography.map.mercator
    val isMapModded = repos.resources.isMapModded
    val img = SvgElements.image.copy(x = 0, y = 0, width = mercator.width, height = mercator.height)
    Option(season)
      .filterNot(_ => isMapModded)
      .filter(validSeasons.contains)
      .map(s => {
        val pattern = SvgElements.pattern.copy(
          patternUnits = "userSpaceOnUse",
          width = mercator.width,
          height = mercator.height,
        )
        val water = pattern.copy(id = "background-water").add(img.copy(href = bgWater))
        val bg = pattern.copy(id = "background-terrain").add(img.copy(href = bgOfSeason(s)))
        Seq(water, bg)
      })
      .map(imgs => {
        val defs = SvgElements.defs.add(imgs)
        val polys = imgs.flatMap(patternDefBackground)
        Seq(defs) ++ polys
      })
      .getOrElse(Seq.empty)
  }

  def patternDefBackground(svgDef: SvgElement): Option[SvgElement] = {
    def p(x: Int, y: Int): Point2D = Point2D(x, y)
    for {
      id <- svgDef.id
      width <- svgDef.width
      height <- svgDef.height
    } yield SvgElements
      .polygon
      .addClass("terrain")
      .copy(points = Seq(p(0, 0), p(width, 0), p(width, height), p(0, height)))
      .copy(fill = SvgFill.url(id))
  }


}
