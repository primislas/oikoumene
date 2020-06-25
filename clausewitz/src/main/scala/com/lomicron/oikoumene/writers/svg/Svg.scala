package com.lomicron.oikoumene.writers.svg

import java.text.DecimalFormat

import com.lomicron.oikoumene.model.Color
import com.lomicron.oikoumene.parsers.map.Point2D
import com.lomicron.utils.collection.CollectionUtils.toOption

object Svg {

  val svgHeader: SvgElement = SvgElement(
    tag = SvgTags.SVG,
    customAttrs = Some("xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink= \"http://www.w3.org/1999/xlink\"")
  )

  def colorToSvg(c: Color) = s"rgb(${c.r},${c.g},${c.b})"

  def pointsToSvgPointsAttribute(ps: Seq[Point2D] = Seq.empty): String = {
    val sb = StringBuilder.newBuilder
    sb.append(" points=\"")
    sb.append(ps.map(pointToSvg(_)).mkString(" "))
    sb.append("\"")
    sb.toString()
  }

  def pointsToSvgLinearPath(ps: Seq[Point2D] = Seq.empty, isClosed: Boolean = false): String = {
    val head = ps.headOption.map(p => s"M ${pointToSvg(p, " ")}").toSeq
    val polyline = ps
      .sliding(2, 1)
      .flatMap(pair => toPath(pair.head, pair.last))
    val closing = head.headOption.filter(_ => isClosed).map(_ => "Z").toSeq
    (head ++ polyline ++ closing).mkString(" ")
  }

  def pointsToQuadraticPath(ps: Seq[Point2D]): String = {
    if (ps.size < 3) pointsToSvgLinearPath(ps)
    else {
      val head = ps.head
      val moveTo = Seq(s"M ${pointToSvg(head, " ")}")

      var remainingPs = ps.drop(1)
      val q1 = remainingPs.head
      remainingPs = remainingPs.drop(1)
      val q2 = remainingPs.head
      val quad = Seq(s"q ${offset(q1, head)}, ${offset(q2, head)}")

      val tail =
        if (remainingPs.size == 1) Seq.empty
        else remainingPs
          .sliding(2, 1)
          .map(s => s"t ${offset(s.last, s.head)}")

      (moveTo ++ quad ++ tail).mkString(" ")
    }
  }

  def textPath(pathId: String, curve: Seq[Point2D], content: String, textLength: Int): Seq[SvgElement] = {
    val letterOffset = 7.0 - content.map(c => if (c == 'I') 0.5 else 1.0).sum
    val fontSizeOffset =
      if (letterOffset > 0) letterOffset
      else {
        if (textLength >= 80) letterOffset
        else letterOffset / (2 - Math.pow(1.05, textLength - 80))
      }
//      if (textLength < 80) letterOffset
//      else if (textLength < 160) letterOffset * 2
//      else letterOffset * 3
    val font = (2 + textLength.toDouble / 5) + fontSizeOffset
    val fontSize =
      if (font < 7 || textLength < 16) 0
//      else if (font > 60) 60
      else font

    val offsetCurve = textPathCurveOffset(curve, font)
    val quadPath = Svg.pointsToQuadraticPath(offsetCurve)
    val path = SvgElements.path.copy(id = pathId, fill = SvgFill.none, path = quadPath)

    val textPath = SvgElements.textPath
      .copy(
        href = s"#$pathId",
        startOffset = "50%",
        textAnchor = "middle",
        textLength = s"$textLength",
        fontSize = s"${doubleToSvg(fontSize)}px",
        customContent = content,
      )

    val text = SvgElements.text
      .copy(dominantBaseline = "middle")
      .addClass("tn")
      .add(textPath)
    val tinyText = if (font < 10) text.addClass("tn-tiny") else text

    Seq(path, tinyText)
  }

  def textPathCurveOffset(curve: Seq[Point2D], fontSize: Double): Seq[Point2D] = {
    val (a, b, c) = (curve.head, curve(1), curve.last)
    val dxAB = b.dx(a)
    val dyAB = -b.dy(a)
    val dxAC = c.dx(a)
    val dyAC = -c.dy(a)
    val angleAB = if (dxAB != 0) dyAB / dxAB else 0
    val angleAC = if (dxAC != 0) dyAC / dxAC else 0

    val isConvex = angleAB >= angleAC || (dxAB <= 0 && dxAC >= 0) || (dyAB <= 0 && dyAC >= 0)
    val alpha = Math.atan(angleAC)
    val dx = Math.sin(alpha)
    val dy = Math.cos(alpha)

    val offsetCoeff =
      if (isConvex) fontSize / 4
//      else -fontSize / 12
      else 0
    val offset = Point2D(offsetCoeff * dx, offsetCoeff * dy)

    curve.map(_ + offset)
  }

  def toPath(p1: Point2D, p2: Point2D): Option[String] = {
    if (p1 == p2) Option.empty
    else if (p1.x == p2.x) s"v ${dySvg(p1, p2)}"
    else if (p1.y == p2.y) s"h ${dxSvg(p1, p2)}"
    else s"l ${dxSvg(p1, p2)} ${dySvg(p1, p2)}"
  }

  def offset(p2: Point2D, p1: Point2D): String = {
    val o = p2 - p1
    s"${doubleToSvg(o.x)} ${doubleToSvg(o.y)}"
  }

  private def dxSvg(p1: Point2D, p2: Point2D): String = doubleToSvg(p2.dx(p1))

  private def dySvg(p1: Point2D, p2: Point2D): String = doubleToSvg(p2.dy(p1))

  val df = new DecimalFormat("#.#")

  def doubleToSvg(d: Double): String = df.format(d)

  def pointToSvg(p: Point2D, separator: String = ","): String =
    s"${doubleToSvg(p.x)}$separator${doubleToSvg(p.y)}"

}
