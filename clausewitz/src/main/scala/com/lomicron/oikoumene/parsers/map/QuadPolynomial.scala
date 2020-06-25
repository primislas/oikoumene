package com.lomicron.oikoumene.parsers.map

case class QuadPolynomial
(
  a: Double,
  b: Double,
  c: Double
) {

  def at(x: Double): Double = a + b * x + c * x * x

  def pointAt(x: Double): Point2D = Point2D(x, at(x))

  def derivativeAt(x: Double): Double = b + 2 * c * x

  def distance(x1: Double, x2: Double, points: Int = 10): Double =
    if (x1 == x2) 0
    else {
      val dx = (x2 - x1) / points
      (0 to points)
        .map(i => pointAt(x1 + i * dx))
        .sliding(2, 1)
        .map(ps => ps.last.distance(ps.head))
        .sum
    }

  def toBezier(x1: Double, x2: Double): Seq[Point2D] = {
    val start = pointAt(x1)
    val end = pointAt(x2)
    val control =
      if (x1 == x2) Point2D(x1, (end.y - start.y) / 2)
      else {
        val cx = (x1 + x2) / 2
        val cy = at(x1) + derivativeAt(x1) * (x2 - x1) / 2
        Point2D(cx, cy)
      }
    Seq(start, control, end)
  }

}

object QuadPolynomial {

  def apply(coeffs: Array[Double]): QuadPolynomial = {
    val a = if (coeffs.length > 0) coeffs(0) else 0
    val b = if (coeffs.length > 1) coeffs(1) else 0
    val c = if (coeffs.length > 2) coeffs(2) else 0
    QuadPolynomial(a, b, c)
  }

}
