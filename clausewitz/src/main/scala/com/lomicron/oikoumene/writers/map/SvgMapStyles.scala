package com.lomicron.oikoumene.writers.map

import com.lomicron.oikoumene.model.Color
import com.lomicron.oikoumene.writers.svg.{Svg, SvgElement, SvgTags}

object SvgMapStyles {
  val uncolonizedColor: Color = Color(165, 152, 144)
  val wastelandColor: Color = Color(145, 132, 124)
  val seaColor: Color = Color(157, 239, 254)
  val lakeColor: Color = Color(135, 248, 250)

  val defaultMapStyle: SvgElement = SvgElement(
    tag = SvgTags.STYLE,
    customContent = Some(
      s"""
         |.province {
         |  stroke-width: 0;
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
         |  opacity: 0.5;
         |}
         |.river {
         |  fill: none;
         |  stroke-linecap: round;
         |  stroke-linejoin: round;
         |  opacity: 0.4;
         |}
         |.river-narrowest {
         |  stroke: ${Svg.colorToSvg(lakeColor)};
         |  stroke-width: 1;
         |}
         |.river-narrow {
         |  stroke: ${Svg.colorToSvg(lakeColor)};
         |  stroke-width: 2;
         |}
         |.river-wide {
         |  stroke: ${Svg.colorToSvg(seaColor)};
         |  stroke-width: 2;
         |}
         |.river-widest {
         |  stroke: ${Svg.colorToSvg(seaColor)};
         |  stroke-width: 3;
         |}
         |.border {
         |  fill: none;
         |  stroke-width: 0;
         |  stroke-linecap: round;
         |  stroke-linejoin: round;
         |  stroke: rgb(50,50,50);
         |  stroke-opacity: 0.1;
         |}
         |.border-country {
         |  stroke: rgb(50,50,50);
         |  stroke-width: 2;
         |  stroke-opacity: 0.2;
         |}
         |.border-land-area { stroke:rgb(50,50,50);stroke-opacity:0.1;stroke-width:1; }
         |.border-sea {stroke:rgb(0,0,50);stroke-opacity:0.1;}
         |.border-sea-area {stroke:rgb(0,0,50);stroke-opacity:0.2;}
         |.border-sea-shore {stroke:rgb(50,175,200);stroke-opacity:0.4;stroke-width:1;}
         |.border-lake-shore {stroke:rgb(50,200,200);stroke-opacity:0.4;stroke-width:1;}
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
