package com.lomicron.utils.geometry

import com.lomicron.utils.geometry.SchneidersFitter.{DEFAULT_EPSILON, DEFAULT_ERROR, WU_BARSKY_COEF}
import com.lomicron.utils.geometry.TPath.Polypath

import scala.collection.mutable.ArrayBuffer

object SchneidersFitter {
  val DEFAULT_ERROR = 1.5
  val WU_BARSKY_COEF = 3.0
  val DEFAULT_EPSILON = 1.0E-6
  def apply(points: Seq[Point2D]): SchneidersFitter = SchneidersFitter(points.toVector)
  def fit(points: Seq[Point2D], error: Double = DEFAULT_ERROR): Polypath =
    SchneidersFitter(points).fit(error)
}

case class SchneidersFitter
(
  points: Vector[Point2D],
) {

  def length: Int = size

  def size: Int = points.size

  def fit(error: Double = DEFAULT_ERROR): Polypath = {
    val pSize = size
    if (pSize > 1) {
      val curves = ArrayBuffer[TPath]()
      val tan1 = (curveAt(1) - curveAt(0)).normalize
      val tan2 = (curveAt(pSize - 2) - curveAt(pSize - 1)).normalize
      fitCubic(curves, error, 0, pSize - 1, tan1, tan2)
      curves.toList
    } else
      Seq.empty
  }

  def fitCubic
  (
    curves: ArrayBuffer[TPath],
    error: Double,
    first: Int,
    last: Int,
    tan1: Point2D,
    tan2: Point2D
  ): Polypath = {

    // TODO tidy up for idiomatic scala
    /* 2 point case */
    if (last - first == 1) {
      val p1 = points(first)
      val p2 = points(last)
      curves += Polyline(Seq(p1, p2))
      curves.toList
    } else {
      /* parameterize points and attempt to fit the curve */
      val uPrime = chordLengthParameterize(first, last)
      var maxError = Math.max(error, error * error)
      var split = -1
      var parametersInOrder = true

      /* 5 iterations */
      val isFitted = (0 to 4)
        .to(LazyList)
        .map(_ => {
          val bezier = generateBezier(first, last, uPrime, tan1, tan2)
          /* Find max deviation of points to fitted curve */
          val max = findMaxError(first, last, bezier, uPrime)
          if (max._1 < error && parametersInOrder) {
            curves += bezier
            Fitted
          } else {
            split = max._2
            if (max._1 >= maxError)
              NoMoreIterations
            else {
              parametersInOrder = reparameterize(first, last, uPrime, bezier)
              maxError = max._1
              Iterating
            }
          }
        })
        .find(_ != Iterating)
        .contains(Fitted)

      if (!isFitted) {
        var tanCenter = points(split - 1) - points(split + 1)
        tanCenter = tanCenter.normalize
        fitCubic(curves, error, first, split, tan1, tanCenter)
        fitCubic(curves, error, split, last, tanCenter * (-1), tan2)
      }

      curves.toList
    }
  }

  def curveAt(index: Int): Point2D = points(index)

  def chordLengthParameterize(first: Int, last: Int): Array[Double] = {
    val u = Array.fill(last - first + 1)(0.0)
    for (i <- first + 1 to last)
      u(i - first) = u(i - first - 1) + points(i).distance(points(i - 1))
    val m = last - first
    for (i <- 1 to m)
      u(i) = u(i) / u(m)
    u
  }

  def generateBezier
  (
    first: Int,
    last: Int,
    uPrime: Array[Double],
    tan1: Point2D,
    tan2: Point2D,
    epsilon: Double = DEFAULT_EPSILON,
  ): BezierCurve = {
    val p1 = points(first)
    val p2 = points(last)

    val (mC, mX) = calculateMatrices(first, last, uPrime, tan1, tan2)
    val (alpha1, alpha2) = calculateAlphas(mC, mX, epsilon)
    val (pta, ptb) = calculateControlPoints(p1, p2, tan1, tan2, alpha1, alpha2, epsilon)

    BezierCurve(p1, pta, ptb, p2)
  }

  def calculateMatrices
  (
    first: Int,
    last: Int,
    uPrime: Array[Double],
    tan1: Point2D,
    tan2: Point2D,
  ): (Array[Array[Double]], Array[Double]) = {
    val p1 = points(first)
    val p2 = points(last)

    /* C and X matrices */
    val C = Array(Array(0.0, 0.0), Array(0.0, 0.0))
    val X = Array(0.0, 0.0)

    for (i <- 0 until last - first + 1) {
      val u = uPrime(i)
      val t = 1 - u
      val b = 3 * u * t
      val b0 = t * t * t
      val b1 = b * t
      val b2 = b * u
      val b3 = u * u * u
      val a1 = tan1 * b1
      val a2 = tan2 * b2
      val tmp = points(first + i) - (p1 * (b0 + b1)) - (p2 * (b2 + b3))
      C(0)(0) += a1 * a1
      C(0)(1) += a1 * a2
      C(1)(0) = C(0)(1)
      C(1)(1) += a2 * a2
      X(0) += a1 * tmp
      X(1) += a2 * tmp
    }

    (C, X)
  }

  def calculateAlphas(C: Array[Array[Double]], X: Array[Double], epsilon: Double): (Double, Double) = {
    /* determinant of C and X */
    val detC0C1 = C(0)(0) * C(1)(1) - C(1)(0) * C(0)(1)
    val (alpha1, alpha2) = if (Math.abs(detC0C1) > epsilon) {
      /* Kramer's rule */
      val detC0X = C(0)(0) * X(1) - C(1)(0) * X(0)
      val detXC1 = X(0) * C(1)(1) - X(1) * C(0)(1)

      /* alpha values */
      (detXC1 / detC0C1, detC0X / detC0C1)
    } else {
      val c0 = C(0)(0) + C(0)(1)
      val c1 = C(1)(0) + C(1)(1)
      val a =
        if (Math.abs(c0) > epsilon)
          X(0) / c0
        else if (Math.abs(c1) > epsilon)
          X(1) / c1
        else
          0
      (a, a)
    }
    (alpha1, alpha2)
  }

  def calculateControlPoints
  (
    p1: Point2D,
    p2: Point2D,
    tan1: Point2D,
    tan2: Point2D,
    alpha1: Double,
    alpha2: Double,
    epsilon: Double,
  ): (Point2D, Point2D) = {

    /* If alpha negative, use the Wu/Barsky heuristic
     * (if alpha is 0, you get coincident control points that lead to divide by zero in any subsequent
     * findRoot() call. */
    val segLength = p2.distance(p1)
    val eps = epsilon * segLength
    val (handle1, handle2) = if (alpha1 < eps || alpha2 < eps) {
    /* fall back on standard (probably inaccurate) formula, and subdivide further if needed. */
      /* NOTE: The above comment is from the original cpp lib, however the outcome seems to match
      *         the outcome for Wu/Barsky heuristic. Either a faulty comment or a faulty execution path? */
      (None, None)
    } else {
      /* Check if the found control points are in the right order when projected onto the line through pt1 and pt2. */
      val line = p2 - p1
      /* Control points 1 and 2 are positioned at alpha distance out on the tangent vectors, left and right, respectively */
      val h1 = tan1 * alpha1
      val h2 = tan2 * alpha2
      if (h1 * line - h2 * line > segLength * segLength)
      /* Fall back to the Wu/Barsky heuristic above */
        (None, None)
      else
        (Some(h1), Some(h2))
    }

    val pta = handle1.map(_ + p1).getOrElse(p1 + tan1 * (segLength / WU_BARSKY_COEF))
    val ptb = handle2.map(_ + p2).getOrElse(p2 + tan2 * (segLength / WU_BARSKY_COEF))

    (pta, ptb)
  }

  def findMaxError(first: Int, last: Int, bezier: BezierCurve, u: Array[Double]): (Double, Int) = {
    var index = (last - first + 1) / 2
    var maxDist = 0.0
    for (i <- first + 1 until last) {
      val P = evaluate(3, bezier, u(i - first))
      val v = P - points(i)
      val dist = v.sqrDistance()
      if (dist >= maxDist) {
        maxDist = dist
        index = i
      }
    }
    (maxDist, index)
  }

  def evaluate(degree: Int, bezier: BezierCurve, t: Double): Point2D = {
    val tmp = bezier.toArray

    /* triangle computation */
    for (i <- 1 to degree) {
      for (j <- 0 to degree - i) {
        tmp(j) = tmp(j) * (1 - t) + tmp(j + 1) * t
      }
    }

    tmp(0)
  }

  def findRoot(bezierCurve: BezierCurve, point: Point2D, u: Double): Double = {
    val bezier = bezierCurve.toArray
    val curve1 = Array.fill(3)(Point2D.ZERO)
    val curve2 = Array.fill(2)(Point2D.ZERO)

    /* control vertices for Q' */
    for (i <- 0 to 2) curve1(i) = (bezier(i + 1) - bezier(i)) * 3.0
    /* control vertices for Q'' */
    for (i <- 0 to 1) curve2(i) = (curve1(i + 1) - curve1(i)) * 2.0

    /* compute Q(u), Q'(u) and Q''(u) */
    val p = evaluate(3, BezierCurve(bezier), u)
    val p1 = evaluate(2, BezierCurve(curve1), u)
    val p2 = evaluate(1, BezierCurve(curve2), u)
    val diff = p - point
    val df = p1 * p1 + diff * p2

    /* f(u) / f'(u) */
    if (Math.abs(df) < 1.0E-6) u
    /* u = u - f(u)/f'(u) */
    else u - (diff * p1) / df
  }

  def reparameterize(first: Int, last: Int, u: Array[Double], bezier: BezierCurve): Boolean = {
    for (i <- first to last)
      u(i - first) = findRoot(bezier, points(i), u(i - first))
    (1 until u.length).forall(i => u(i) > u(i - 1))
  }

}

sealed trait FittingStatus
object Iterating extends FittingStatus
object Fitted extends FittingStatus
object NoMoreIterations extends FittingStatus
