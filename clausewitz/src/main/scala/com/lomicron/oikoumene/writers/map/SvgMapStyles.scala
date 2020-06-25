package com.lomicron.oikoumene.writers.map

import com.lomicron.oikoumene.model.Color
import com.lomicron.oikoumene.writers.svg.{Svg, SvgElement, SvgTags}

object SvgMapStyles {
  val uncolonizedColor: Color = Color(165, 152, 144)
  val wastelandColor: Color = Color(145, 132, 124)
  val seaColor: Color = Color(157, 239, 254)
  val lakeColor: Color = Color(135, 248, 250)
  val riverColor: Color = Color(50, 180, 220)

  val defaultMapStyle: SvgElement = SvgElement(
    tag = SvgTags.STYLE,
    customContent = Some(
      s"""
         |@import url("https://fonts.googleapis.com/css2?family=Dosis:wght@800");
         |.province { stroke-width:0; fill:none; opacity:0.7; }
         |.owned { stroke-width:0; opacity:0.7; }
         |.wasteland { fill:${Svg.colorToSvg(wastelandColor)}; opacity:0; }
         |.uncolonized { fill:${Svg.colorToSvg(uncolonizedColor)}; opacity:0; }
         |.sea { fill:${Svg.colorToSvg(seaColor)}; opacity:0.3;}
         |.lake { fill:${Svg.colorToSvg(lakeColor)}; opacity:0.5; }
         |.elevated-lake { fill:${Svg.colorToSvg(lakeColor)}; opacity:0; }
         |.river {
         |  fill:none;
         |  stroke-linecap:round;
         |  stroke-linejoin:round;
         |  opacity:0.3;
         |}
         |.river-narrowest { stroke:${Svg.colorToSvg(riverColor)}; stroke-width:1; }
         |.river-narrow { stroke:${Svg.colorToSvg(riverColor)}; stroke-width:1.7; }
         |.river-wide { stroke:${Svg.colorToSvg(riverColor)}; stroke-width:2.4; }
         |.river-widest { stroke:${Svg.colorToSvg(riverColor)}; stroke-width:3; }
         |.border {
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
         |.border-land-area { stroke:rgb(50,50,50); stroke-opacity:0.2; stroke-width:1; }
         |.border-sea { stroke:rgb(0,0,50); stroke-opacity:0.1;}
         |.border-sea-area { stroke:rgb(0,0,50); stroke-opacity:0.2; }
         |.border-sea-shore { stroke:rgb(50,175,200); stroke-opacity:0.4; stroke-width:1; }
         |.border-lake-shore { stroke:rgb(50,200,200); stroke-opacity:0.4; stroke-width:1; }
         |.tn { font-family: 'Dosis'; paint-order: stroke; fill: black; stroke: white; stroke-opacity: 0.3; stroke-width: 1; font-weight: bold; opacity: 0.9; }
         |.tn-tiny { stroke-width: 0; }
         |"""
        .stripMargin)
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

}
