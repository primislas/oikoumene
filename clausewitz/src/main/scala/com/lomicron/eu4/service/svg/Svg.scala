package com.lomicron.eu4.service.svg

import com.lomicron.oikoumene.model.Color
import com.lomicron.utils.collection.CollectionUtils.toOption
import com.lomicron.utils.geometry.TPath.Polypath
import com.lomicron.utils.geometry.{BezierCurve, Point2D, Polyline, TPath}

import java.text.DecimalFormat

object Svg {

  val svgHeader: SvgElement = SvgElement(
    tag = SvgTags.SVG,
    customAttrs = Some("xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink= \"http://www.w3.org/1999/xlink\"")
  )

  def circle(cx: Double, cy: Double, radius: Double): SvgElement =
    SvgElements.circle.copy(centerX = cx, centerY = cy, radius = radius)

  def colorToSvg(c: Color) = s"rgb(${c.r},${c.g},${c.b})"

  def pointsToSvgPointsAttribute(ps: Seq[Point2D] = Seq.empty, precision: Int = 1): String = {
    val sb = new StringBuilder()
    sb.append(" points=\"")
    sb.append(ps.map(pointToSvg(_, precision = precision)).mkString(" "))
    sb.append("\"")
    sb.toString()
  }

  def pointsToSvgLinearPath
  (
    ps: Seq[Point2D] = Seq.empty,
    isClosed: Boolean = false,
    precision: Int = 1
  ): String = {
    val head = ps.headOption.map(p => s"M ${pointToSvg(p, " ", precision)}").toSeq
    val polyline = ps
      .sliding(2, 1)
      .flatMap(pair => toPath(pair.head, pair.last, precision))
    val closing = head.headOption.filter(_ => isClosed).map(_ => "Z").toSeq
    (head ++ polyline ++ closing).mkString(" ")
  }

  def pointsToQuadraticPath(ps: Seq[Point2D], precision: Int = 1): String = {
    if (ps.size < 3) pointsToSvgLinearPath(ps)
    else {
      val head = ps.head
      val moveTo = Seq(s"M ${pointToSvg(head, " ", precision)}")

      var remainingPs = ps.drop(1)
      val q1 = remainingPs.head
      remainingPs = remainingPs.drop(1)
      val q2 = remainingPs.head
      val quad = Seq(s"q ${pointOffset(q1, head)}, ${pointOffset(q2, head)}")

      val tail =
        if (remainingPs.size == 1) Seq.empty
        else remainingPs
          .sliding(2, 1)
          .map(s => s"t ${pointOffset(s.last, s.head)}")

      (moveTo ++ quad ++ tail).mkString(" ")
    }
  }

  def pointsToCubicPath(ps: Seq[Point2D], precision: Int = 1): String = {
    def psvg(p: Point2D): String = pointToSvg(p, " ", precision)

    ps match {
      case p1 :: p2 :: p3 :: p4 :: tail =>
        val h = s"M ${psvg(p1)} C ${psvg(p2)}, ${psvg(p3)}, ${psvg(p4)}"
        val t = stringCubicPath(tail, precision)
        (h +: t).mkString(" ")
      case _ :: _ :: _ :: Nil => pointsToQuadraticPath(ps)
      case _ => pointsToSvgLinearPath()
    }
  }

  def fromPolypath(pp: Polypath, precision: Int = 1): String = {
    pp match {
      case h::t => (startPath(h, precision) +: t.map(continuePath(_, precision))).mkString(" ")
      case Nil => ""
    }
  }

  def startPath(p: TPath, precision: Int = 1): String = {
    def psvg(p: Point2D): String = pointToSvg(p, " ", precision)
    p match {
      case pl: Polyline => pl.points.headOption.map(p => s"M ${psvg(p)} " + continuePath(pl, precision)).getOrElse("")
      case b: BezierCurve => s"M ${psvg(b.p1)} " + continuePath(b, precision)
    }
  }

  def continuePath(p: TPath, precision: Int = 1): String = {
    def psvg(p: Point2D): String = pointToSvg(p, " ", precision)
    p match {
      case Polyline(points) => points.sliding(2, 1).flatMap(ps => toPath(ps.head, ps.last)).mkString(" ")
      case bc: BezierCurve => s"c ${psvg(bc.cp1 - bc.p1)}, ${psvg(bc.cp2 - bc.p1)}, ${psvg(bc.p2 - bc.p1)}"
    }
  }

  @scala.annotation.tailrec
  def stringCubicPath(ps: Seq[Point2D], precision: Int = 1, stringed: Seq[String] = Seq.empty): Seq[String] = ps match {
    case p1 :: p2 :: tail =>
      val next = s"S ${pointToSvg(p1, " ", precision)}, ${pointToSvg(p2, " ", precision)}"
      stringCubicPath(tail, precision, stringed :+ next)
    case p1 :: Nil => stringed :+ s"L ${pointToSvg(p1, " ", precision)}"
    case _ => stringed
  }

  def textPath
  (
    pathId: String,
    curve: Seq[Point2D],
    content: String,
    curveLength: Double,
    fontSizeLimit: Double
  ): Seq[SvgElement] = {

    val lengthCoef =
      if (curveLength > 80) 0.7
      else 0.1 * (8 - Math.pow(1.08, curveLength - 80))
    val textLength = curveLength * lengthCoef

    val font =
      if (textLength >= 80) 2 + textLength / 5.5
      else 2 + textLength / (5.0 + 0.5 * Math.pow(1.08, textLength - 80))
    val realTextLength = effectiveTextLength(content)
    val letterOffset = 7.0 - realTextLength
    val fontSizeOffset =
      if (letterOffset > 0) letterOffset
      else {
        if (textLength >= 80) letterOffset * textLength / 80
        else letterOffset / (2 - Math.pow(1.05, textLength - 80))
      }
    val offsetFont = font + fontSizeOffset
    val fontSize =
      if (offsetFont < 5.0) 0
      else if (offsetFont > 10.0 && offsetFont > fontSizeLimit) fontSizeLimit
      else offsetFont

    val offsetCurve = textPathCurveOffset(curve, font)
    val quadPath = Svg.pointsToQuadraticPath(offsetCurve)
    val path = SvgElements.path.copy(id = pathId, fill = SvgFill.none, path = quadPath)
    val realNameLength = adjustNameLengthToTextLength(textLength, content, fontSize)

    val textPath = SvgElements.textPath
      .copy(
        href = s"#$pathId",
        startOffset = "50%",
        textAnchor = "middle",
        textLength = s"${realNameLength.toInt}",
        fontSize = s"${doubleToSvg(fontSize)}px",
        customContent = content,
      )

    val text = SvgElements.text
      .copy(dominantBaseline = "middle")
      .addClass("tn")
      .add(textPath)
    val tinyText = if (font < 10.0) text.addClass("tn-tiny") else text

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
      if (isConvex) fontSize / 5.0
      else fontSize / 8
    //      else 0
    val offset = Point2D(offsetCoeff * dx, offsetCoeff * dy)

    curve.map(_ + offset)
  }

  /**
    * Short names have to be adjusted to look more readable and compact in SVG.
    *
    * @param tl   text length
    * @param name adjust name
    * @param fs   font size
    * @return text length adjusted to character count
    */
  def adjustNameLengthToTextLength(tl: Double, name: String, fs: Double): Double =
    if (name.length < 2) 0.0
    else if (name.length < 7) {
      val realTextLength = effectiveTextLength(name)
      val coeff = 1.0 - (7.0 - realTextLength) * 0.05
      val adjustedLength = tl * coeff
      val minFontLength = fs / 2 * realTextLength
      if (adjustedLength > minFontLength) adjustedLength else minFontLength
    } else tl

  def effectiveTextLength(t: String): Double =
    t.map(c => if (c == 'I') 0.5 else if (c == 'M' || c == 'W') 1.5 else 1.0).sum

  def toPath(p1: Point2D, p2: Point2D, precision: Int = 1): Option[String] = {
    if (p1 == p2) Option.empty
    else if (p1.x == p2.x) s"v ${dySvg(p1, p2, precision)}"
    else if (p1.y == p2.y) s"h ${dxSvg(p1, p2, precision)}"
    else s"l ${dxSvg(p1, p2, precision)} ${dySvg(p1, p2, precision)}"
  }

  def pointOffset(p2: Point2D, p1: Point2D): String = {
    val o = p2 - p1
    s"${doubleToSvg(o.x)} ${doubleToSvg(o.y)}"
  }

  private def dxSvg(p1: Point2D, p2: Point2D, precision: Int = 1): String =
    doubleToSvg(p2.dx(p1), precision)

  private def dySvg(p1: Point2D, p2: Point2D, precision: Int = 1): String =
    doubleToSvg(p2.dy(p1), precision)

  val df = new DecimalFormat("#.#")
  val df2 = new DecimalFormat("#.##")
  val df3 = new DecimalFormat("#.###")

  def precisionFormat(precision: Int): DecimalFormat = precision match {
    case 1 => df
    case 2 => df2
    case 3 => df3
    case _ => df
  }

  def doubleToSvg(d: Double, precision: Int = 1): String =
    precisionFormat(precision).format(d)

  def pointToSvg(p: Point2D, separator: String = ",", precision: Int = 1): String =
    s"${doubleToSvg(p.x, precision)}$separator${doubleToSvg(p.y, precision)}"

}
